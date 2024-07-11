package org.jqassistant.plugin.jira.cache;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Component;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.buschmais.jqassistant.core.store.api.Store;

import java.util.Map;

import org.jqassistant.plugin.jira.model.JiraComment;
import org.jqassistant.plugin.jira.model.JiraComponent;
import org.jqassistant.plugin.jira.model.JiraIssue;
import org.jqassistant.plugin.jira.model.JiraIssueLink;
import org.jqassistant.plugin.jira.model.JiraIssueType;
import org.jqassistant.plugin.jira.model.JiraLabel;
import org.jqassistant.plugin.jira.model.JiraPriority;
import org.jqassistant.plugin.jira.model.JiraProject;
import org.jqassistant.plugin.jira.model.JiraStatus;
import org.jqassistant.plugin.jira.model.JiraUser;
import org.jqassistant.plugin.jira.model.JiraVersion;
import org.jqassistant.plugin.jira.utils.TimeConverter;

/**
 * Endpoint to access the plugin internal node cache, to find, create and update items.
 */
public class CacheEndpoint {
    private final Store store;
    private final DescriptorCache descriptorCache;

    public CacheEndpoint(Store store) {
        this.store = store;
        this.descriptorCache = new DescriptorCache(store);
    }

    public void flush() {
        this.store.flush();
    }

    /**
     * Find or create a project descriptor.
     *
     * @param project The project to find or create.
     * @return The descriptor.
     */
    public JiraProject findOrCreateProject(Project project) {
        return this.descriptorCache.getProject(project.getId())
                .orElseGet(() -> this.createProject(project));
    }

    private JiraProject createProject(Project project) {
        JiraProject jiraProject = this.store.create(JiraProject.class);
        jiraProject.setSelf(project.getSelf().toString());
        jiraProject.setJiraId(project.getId());
        jiraProject.setKey(project.getKey());
        jiraProject.setName(project.getName());
        jiraProject.setDescription(project.getDescription());
        if (project.getUri() != null) {
            jiraProject.setUri(project.getUri().toString());
        }

        this.descriptorCache.put(jiraProject);

        return jiraProject;
    }

    /**
     * Find or create a issue descriptor.
     *
     * @param issue             The issue to find or create.
     * @param dynamicProperties Additional dynamic properties.
     * @return The descriptor.
     */
    public JiraIssue findOrCreateIssue(Issue issue, Map<String, String> dynamicProperties) {
        JiraIssue jiraIssue = this.descriptorCache.getIssue(issue.getId())
                .orElseGet(() -> this.createIssue(issue));
        if (!dynamicProperties.isEmpty()) {
            StringBuilder queryBuilder = new StringBuilder("MATCH (i:Jira:Issue) WHERE i.jiraId = ")
                    .append(issue.getId());
            for (Map.Entry<String, String> entry : dynamicProperties.entrySet()) {
                queryBuilder
                        .append(" SET i.")
                        .append(entry.getKey())
                        .append("=\"")
                        .append(entry.getValue().replaceAll("\"", "'"))
                        .append("\"");
            }

            this.store.executeQuery(queryBuilder.toString());
        }

        return jiraIssue;
    }

    private JiraIssue createIssue(Issue issue) {
        JiraIssue jiraIssue = this.store.create(JiraIssue.class);
        jiraIssue.setSelf(issue.getSelf().toString());
        jiraIssue.setJiraId(issue.getId());
        jiraIssue.setKey(issue.getKey());
        jiraIssue.setSummary(issue.getSummary());
        jiraIssue.setDescription(issue.getDescription());
        jiraIssue.setCreationDate(TimeConverter.convertTime(issue.getCreationDate()));
        jiraIssue.setUpdateDate(TimeConverter.convertTime(issue.getUpdateDate()));
        jiraIssue.setDueDate(TimeConverter.convertTime(issue.getDueDate()));

        this.descriptorCache.put(jiraIssue);

        return jiraIssue;
    }

    /**
     * Check if the given user is already cached.
     *
     * @param basicUser The user.
     * @return True if the user is already cached. False otherwise.
     */
    public boolean isUserAlreadyCached(BasicUser basicUser) {
        return this.descriptorCache.getUser(basicUser.getName()).isPresent();
    }

    /**
     * Find a given user. If it could not be found, throw an exception.
     *
     * @param basicUser The user to find.
     * @return The user if found.
     * @throws EntityNotFoundException If the user could not be found.
     */
    public JiraUser findUserOrThrowException(BasicUser basicUser) throws EntityNotFoundException {
        return this.descriptorCache.getUser(basicUser.getName())
                .orElseThrow(() -> new EntityNotFoundException("We can't find a JiraUser with ID: " + basicUser.getName()));
    }

    /**
     * Find or create a user descriptor.
     *
     * @param user The user to find or create.
     * @return The descriptor.
     */
    public JiraUser findOrCreateUser(User user) {
        return this.descriptorCache.getUser(user.getName())
                .orElseGet(() -> this.createUser(user));
    }

    private JiraUser createUser(User user) {
        JiraUser jiraUser = this.store.create(JiraUser.class);
        jiraUser.setSelf(user.getSelf().toString());
        jiraUser.setDisplayName(user.getDisplayName());
        jiraUser.setEmailAddress(user.getEmailAddress());
        jiraUser.setName(user.getName());
        jiraUser.setActive(user.isActive());

        this.descriptorCache.put(jiraUser);

        return jiraUser;
    }

    /**
     * Find or create a version descriptor.
     *
     * @param version The version to find or create.
     * @return The descriptor.
     */
    public JiraVersion findOrCreateVersion(Version version) {
        return this.descriptorCache.getVersion(version.getId())
                .orElseGet(() -> this.createVersion(version));
    }

    private JiraVersion createVersion(Version version) {
        JiraVersion jiraVersion = this.store.create(JiraVersion.class);
        jiraVersion.setSelf(version.getSelf().toString());
        jiraVersion.setJiraId(version.getId());
        jiraVersion.setDescription(version.getDescription());
        jiraVersion.setName(version.getName());
        jiraVersion.setArchived(version.isArchived());
        jiraVersion.setReleased(version.isReleased());
        jiraVersion.setReleaseDate(TimeConverter.convertTime(version.getReleaseDate()));

        this.descriptorCache.put(jiraVersion);

        return jiraVersion;
    }

    /**
     * Find or create a component descriptor.
     *
     * @param component The component to find or create.
     * @return The descriptor.
     */
    public JiraComponent findOrCreateComponent(Component component) {
        return this.descriptorCache.getComponent(component.getId())
                .orElseGet(() -> this.createComponent(component));
    }

    private JiraComponent createComponent(Component component) {
        JiraComponent jiraComponent = this.store.create(JiraComponent.class);
        jiraComponent.setSelf(component.getSelf().toString());
        jiraComponent.setJiraId(component.getId());
        jiraComponent.setDescription(component.getDescription());
        jiraComponent.setName(component.getName());

        this.descriptorCache.put(jiraComponent);

        return jiraComponent;
    }

    /**
     * Find a given component. If it could not be found, throw an exception.
     *
     * @param basicComponent The component to find.
     * @return The component if found.
     * @throws EntityNotFoundException If the component could not be found.
     */
    public JiraComponent findComponentOrThrowException(BasicComponent basicComponent) throws EntityNotFoundException {
        return this.descriptorCache.getComponent(basicComponent.getId())
                .orElseThrow(() -> new EntityNotFoundException("We can't find a JiraComponent with ID: " + basicComponent.getId()));
    }

    /**
     * Find or create a issueType descriptor.
     *
     * @param issueType The component to find or create.
     * @return The descriptor.
     */
    public JiraIssueType findOrCreateIssueType(IssueType issueType) {
        return this.descriptorCache.getIssueType(issueType.getId())
                .orElseGet(() -> this.createIssueType(issueType));
    }

    private JiraIssueType createIssueType(IssueType issueType) {
        JiraIssueType jiraIssueType = this.store.create(JiraIssueType.class);
        jiraIssueType.setSelf(issueType.getSelf().toString());
        jiraIssueType.setJiraId(issueType.getId());
        jiraIssueType.setDescription(issueType.getDescription());
        jiraIssueType.setName(issueType.getName());
        jiraIssueType.setSubtask(issueType.isSubtask());
        if (issueType.getIconUri() != null) {
            jiraIssueType.setIconUri(issueType.getIconUri().toString());
        }

        this.descriptorCache.put(jiraIssueType);

        return jiraIssueType;
    }

    /**
     * Find or create a priority descriptor.
     *
     * @param priority The priority to find or create.
     * @return The descriptor.
     */
    public JiraPriority findOrCreatePriority(Priority priority) {
        return this.descriptorCache.getPriority(priority.getId())
                .orElseGet(() -> this.createPriority(priority));
    }

    private JiraPriority createPriority(Priority priority) {
        JiraPriority jiraPriority = this.store.create(JiraPriority.class);
        jiraPriority.setSelf(priority.getSelf().toString());
        jiraPriority.setJiraId(priority.getId());
        jiraPriority.setDescription(priority.getDescription());
        jiraPriority.setName(priority.getName());
        jiraPriority.setStatusColor(priority.getStatusColor());
        if (priority.getIconUri() != null) {
            jiraPriority.setIconUri(priority.getIconUri().toString());
        }

        this.descriptorCache.put(jiraPriority);

        return jiraPriority;
    }

    /**
     * Find a given priority. If it could not be found, throw an exception.
     *
     * @param basicPriority The priority to find.
     * @return The priority if found.
     * @throws EntityNotFoundException If the priority could not be found.
     */
    public JiraPriority findPriorityOrThrowException(BasicPriority basicPriority) throws EntityNotFoundException {
        return this.descriptorCache.getPriority(basicPriority.getId())
                .orElseThrow(() -> new EntityNotFoundException("We can't find a JiraPriority with ID: " + basicPriority.getId()));
    }

    /**
     * Find or create a status descriptor.
     *
     * @param status The status to find or create.
     * @return The descriptor.
     */
    public JiraStatus findOrCreateStatus(Status status) {
        return this.descriptorCache.getStatus(status.getId())
                .orElseGet(() -> this.createStatus(status));
    }

    private JiraStatus createStatus(Status status) {
        JiraStatus jiraStatus = this.store.create(JiraStatus.class);
        jiraStatus.setSelf(status.getSelf().toString());
        jiraStatus.setJiraId(status.getId());
        jiraStatus.setDescription(status.getDescription());
        jiraStatus.setName(status.getName());
        if (status.getIconUrl() != null) {
            jiraStatus.setIconUri(status.getIconUrl().toString());
        }

        this.descriptorCache.put(jiraStatus);

        return jiraStatus;
    }

    /**
     * Find or create a comment descriptor.
     *
     * @param comment The comment to find or create.
     * @return The descriptor.
     */
    public JiraComment findOrCreateComment(Comment comment) {
        return this.descriptorCache.getComment(comment.getId())
                .orElseGet(() -> this.createComment(comment));
    }

    private JiraComment createComment(Comment comment) {
        JiraComment jiraComment = this.store.create(JiraComment.class);
        jiraComment.setSelf(comment.getSelf().toString());
        jiraComment.setJiraId(comment.getId());
        jiraComment.setCreationDate(TimeConverter.convertTime(comment.getCreationDate()));
        jiraComment.setUpdateDate(TimeConverter.convertTime(comment.getUpdateDate()));
        jiraComment.setBody(comment.getBody());

        this.descriptorCache.put(jiraComment);

        return jiraComment;
    }

    /**
     * Find or create a label descriptor.
     *
     * @param labelName The label to find or create.
     * @return The descriptor.
     */
    public JiraLabel findOrCreateLabel(String labelName) {
        return this.descriptorCache.getLabel(labelName)
                .orElseGet(() -> this.createLabel(labelName));
    }

    private JiraLabel createLabel(String labelName) {
        JiraLabel jiraLabel = this.store.create(JiraLabel.class);
        jiraLabel.setName(labelName);

        this.descriptorCache.put(jiraLabel);

        return jiraLabel;
    }

    /**
     * Create a issue link descriptor.
     *
     * @param issueLink The issue link to create.
     * @return The descriptor.
     */
    public JiraIssueLink createIssueLink(IssueLink issueLink) {
        JiraIssueLink jiraIssueLink = this.store.create(JiraIssueLink.class);
        jiraIssueLink.setName(issueLink.getIssueLinkType().getName());
        jiraIssueLink.setDescription(issueLink.getIssueLinkType().getDescription());
        jiraIssueLink.setTargetIssueKey(issueLink.getTargetIssueKey());
        jiraIssueLink.setTargetIssueUri(issueLink.getTargetIssueUri().toString());

        return jiraIssueLink;
    }

    /**
     * Find a given issue. If it could not be found, throw an exception.
     *
     * @param issueId The issue to find.
     * @return The issue if found.
     * @throws EntityNotFoundException If the issue could not be found.
     */
    public JiraIssue findIssueOrThrowException(Long issueId) throws EntityNotFoundException {
        return this.descriptorCache.getIssue(issueId)
                .orElseThrow(() -> new EntityNotFoundException("We can't find a JiraIssue with ID: " + issueId));
    }
}
