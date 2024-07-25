package org.jqassistant.plugin.jira.scanner;

import java.util.List;
import java.util.Map;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.plugin.jira.model.JiraServer;
import org.jqassistant.plugin.jira.scanner.builder.IssueLinkBuilder;
import org.jqassistant.plugin.jira.scanner.builder.PriorityBuilder;
import org.jqassistant.plugin.jira.scanner.builder.ProjectBuilder;
import org.jqassistant.plugin.jira.scanner.builder.ServerInfoBuilder;
import org.jqassistant.plugin.jira.scanner.builder.StatusBuilder;
import org.jqassistant.plugin.jira.scanner.builder.SubtaskRelationBuilder;

public class GraphBuilder {
    private final ServerInfoBuilder serverInfoBuilder;
    private final PriorityBuilder priorityBuilder;
    private final StatusBuilder statusBuilder;
    private final IssueLinkBuilder issueLinkBuilder;
    private final SubtaskRelationBuilder subtaskRelationBuilder;
    private final ProjectBuilder projectBuilder;

    GraphBuilder(JiraRestClientWrapper jiraRestClientWrapper, CacheEndpoint cacheEndpoint, Map<String, String> customIssueFields, String customJql) {
        this.serverInfoBuilder = new ServerInfoBuilder(jiraRestClientWrapper);
        this.priorityBuilder = new PriorityBuilder(cacheEndpoint, jiraRestClientWrapper);
        this.statusBuilder = new StatusBuilder(cacheEndpoint, jiraRestClientWrapper);
        this.issueLinkBuilder = new IssueLinkBuilder(cacheEndpoint);
        this.subtaskRelationBuilder = new SubtaskRelationBuilder(cacheEndpoint);
        this.projectBuilder = new ProjectBuilder(cacheEndpoint, jiraRestClientWrapper, customIssueFields, customJql);
    }

    void startTraversal(JiraServer jiraServer, List<String> projects) {
        this.serverInfoBuilder.handleServerInfo(jiraServer);
        this.priorityBuilder.handlePriorities(jiraServer);
        this.statusBuilder.handleStatuses(jiraServer);
        this.projectBuilder.handleProjects(jiraServer, projects);
        this.issueLinkBuilder.handleIssueLinks();
        this.subtaskRelationBuilder.handleSubtaskRelations();
    }
}