package org.jqassistant.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Version;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.plugin.jira.model.JiraIssueType;
import org.jqassistant.plugin.jira.model.JiraProject;
import org.jqassistant.plugin.jira.model.JiraServer;
import org.jqassistant.plugin.jira.model.JiraUser;
import org.jqassistant.plugin.jira.model.JiraVersion;

@Slf4j
public class ProjectBuilder {
    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;
    private final ComponentBuilder componentBuilder;
    private final IssueBuilder issueBuilder;
    private final UserBuilder userBuilder;

    public ProjectBuilder(CacheEndpoint cacheEndpoint, JiraRestClientWrapper jiraRestClientWrapper, Map<String, String> customIssueFields, String customJql) {
        this.cacheEndpoint = cacheEndpoint;
        this.jiraRestClientWrapper = jiraRestClientWrapper;
        this.userBuilder = new UserBuilder(cacheEndpoint, jiraRestClientWrapper);
        this.componentBuilder = new ComponentBuilder(cacheEndpoint, jiraRestClientWrapper, this.userBuilder);
        CommentBuilder commentBuilder = new CommentBuilder(cacheEndpoint, this.userBuilder);
        IssueLinkBuilder issueLinkBuilder = new IssueLinkBuilder(cacheEndpoint);
        SubtaskRelationBuilder subtaskRelationBuilder = new SubtaskRelationBuilder(cacheEndpoint);
        this.issueBuilder = new IssueBuilder(cacheEndpoint, jiraRestClientWrapper, commentBuilder, issueLinkBuilder, subtaskRelationBuilder, customIssueFields, customJql);
    }

    public void handleProjects(JiraServer jiraServer, List<String> projects) {
        for (String projectKey : projects) {
            LOGGER.debug("Importing project with key '{}'", projectKey);
            Project project = this.jiraRestClientWrapper.retrieveProject(projectKey);
            JiraProject jiraProject = this.cacheEndpoint.findOrCreateProject(project);
            jiraServer.getProjects().add(jiraProject);

            for (Version version : project.getVersions()) {
                JiraVersion jiraVersion = this.cacheEndpoint.findOrCreateVersion(version);
                jiraProject.getVersions().add(jiraVersion);
            }

            for (IssueType issueType : project.getIssueTypes()) {
                JiraIssueType jiraIssueType = this.cacheEndpoint.findOrCreateIssueType(issueType);
                jiraProject.getIssueTypes().add(jiraIssueType);
            }

            this.componentBuilder.handleComponents(jiraProject, project.getComponents());
            this.resolveLeaderForProject(jiraProject, project.getLead());
            this.issueBuilder.handleIssues(jiraProject);
        }

        LOGGER.info("Imported {} Jira projects", jiraServer.getProjects().size());
        LOGGER.info("Resolving Issue Links");
        this.issueBuilder.resolveIssueLinks();

        LOGGER.info("Resolving Subtask Relations");
        this.issueBuilder.resolveSubtaskRelations();
    }

    private void resolveLeaderForProject(JiraProject jiraProject, BasicUser basicUser) {
        JiraUser jiraUser = this.userBuilder.findUserInCacheOrLoadItFromJira(basicUser);
        jiraProject.setLead(jiraUser);
    }
}
