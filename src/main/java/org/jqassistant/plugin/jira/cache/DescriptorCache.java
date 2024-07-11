package org.jqassistant.plugin.jira.cache;

import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.xo.api.Query;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.jira.model.JiraComment;
import org.jqassistant.plugin.jira.model.JiraComponent;
import org.jqassistant.plugin.jira.model.JiraIssue;
import org.jqassistant.plugin.jira.model.JiraIssueType;
import org.jqassistant.plugin.jira.model.JiraLabel;
import org.jqassistant.plugin.jira.model.JiraPriority;
import org.jqassistant.plugin.jira.model.JiraProject;
import org.jqassistant.plugin.jira.model.JiraStatus;
import org.jqassistant.plugin.jira.model.JiraUser;
import org.jqassistant.plugin.jira.model.JiraVersion;

/**
 * Cache implementation of created descriptors.
 */
@Slf4j
class DescriptorCache {
    private final LoadingCache<Long, JiraProject> projects;
    private final LoadingCache<Long, JiraIssue> issues;
    private final LoadingCache<String, JiraUser> users;
    private final LoadingCache<Long, JiraVersion> versions;
    private final LoadingCache<Long, JiraComponent> components;
    private final LoadingCache<Long, JiraIssueType> issueTypes;
    private final LoadingCache<Long, JiraPriority> priorities;
    private final LoadingCache<Long, JiraStatus> statuses;
    private final LoadingCache<Long, JiraComment> comments;
    private final LoadingCache<String, JiraLabel> labels;
    private final Store store;

    public DescriptorCache(Store store) {
        this.store = store;
        this.projects = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:Project{jiraId: $jiraId}) RETURN i", Map.of("jiraId", s), JiraProject.class));
        this.issues = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:Issue{jiraId: $jiraId}) RETURN i", Map.of("jiraId", s), JiraIssue.class));
        this.users = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:User{name: $name}) RETURN i", Map.of("name", s), JiraUser.class));
        this.versions = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:Version{jiraId: $jiraId}) RETURN i", Map.of("jiraId", s), JiraVersion.class));
        this.components = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:Component{jiraId: $jiraId}) RETURN i", Map.of("jiraId", s), JiraComponent.class));
        this.issueTypes = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:IssueType{jiraId: $jiraId}) RETURN i", Map.of("jiraId", s), JiraIssueType.class));
        this.priorities = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:Priority{jiraId: $jiraId}) RETURN i", Map.of("jiraId", s), JiraPriority.class));
        this.statuses = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:Status{jiraId: $jiraId}) RETURN i", Map.of("jiraId", s), JiraStatus.class));
        this.comments = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:Comment{jiraId: $jiraId}) RETURN i", Map.of("jiraId", s), JiraComment.class));
        this.labels = Caffeine.newBuilder().maximumSize(8192L)
                .build((s) -> this.executeQuery("MATCH (i:Jira:Label{name: $name}) RETURN i", Map.of("name", s), JiraLabel.class));
    }

    <T> T executeQuery(String query, Map<String, Object> properties, Class<T> resultType) {
        try (Query.Result<Query.Result.CompositeRowObject> result = this.store.executeQuery(query, properties)) {
            if (result.hasResult()) {
                return result.getSingleResult().get("i", resultType);
            } else {
                return null;
            }
        }
    }

    Optional<JiraProject> getProject(Long projectID) {
        return Optional.ofNullable(this.projects.get(projectID));
    }

    void put(JiraProject jiraProject) {
        this.projects.put(jiraProject.getJiraId(), jiraProject);
    }

    Optional<JiraIssue> getIssue(Long issueID) {
        return Optional.ofNullable(this.issues.get(issueID));
    }

    void put(JiraIssue jiraIssue) {
        this.issues.put(jiraIssue.getJiraId(), jiraIssue);
    }

    Optional<JiraUser> getUser(String userID) {
        return Optional.ofNullable(this.users.get(userID));
    }

    void put(JiraUser jiraUser) {
        this.users.put(jiraUser.getName(), jiraUser);
    }

    Optional<JiraVersion> getVersion(Long versionID) {
        return Optional.ofNullable(this.versions.get(versionID));
    }

    void put(JiraVersion jiraVersion) {
        this.versions.put(jiraVersion.getJiraId(), jiraVersion);
    }

    Optional<JiraComponent> getComponent(Long componentID) {
        return Optional.ofNullable(this.components.get(componentID));
    }

    void put(JiraComponent jiraComponent) {
        this.components.put(jiraComponent.getJiraId(), jiraComponent);
    }

    Optional<JiraIssueType> getIssueType(Long issueTypeID) {
        return Optional.ofNullable(this.issueTypes.get(issueTypeID));
    }

    void put(JiraIssueType jiraIssueType) {
        this.issueTypes.put(jiraIssueType.getJiraId(), jiraIssueType);
    }

    Optional<JiraPriority> getPriority(Long priorityID) {
        return Optional.ofNullable(this.priorities.get(priorityID));
    }

    void put(JiraPriority jiraPriority) {
        this.priorities.put(jiraPriority.getJiraId(), jiraPriority);
    }

    Optional<JiraStatus> getStatus(Long statusID) {
        return Optional.ofNullable(this.statuses.get(statusID));
    }

    void put(JiraStatus jiraStatus) {
        this.statuses.put(jiraStatus.getJiraId(), jiraStatus);
    }

    Optional<JiraComment> getComment(Long commentID) {
        return Optional.ofNullable(this.comments.get(commentID));
    }

    void put(JiraComment jiraComment) {
        this.comments.put(jiraComment.getJiraId(), jiraComment);
    }

    Optional<JiraLabel> getLabel(String labelName) {
        return Optional.ofNullable(this.labels.get(labelName));
    }

    void put(JiraLabel jiraLabel) {
        this.labels.put(jiraLabel.getName(), jiraLabel);
    }
}
