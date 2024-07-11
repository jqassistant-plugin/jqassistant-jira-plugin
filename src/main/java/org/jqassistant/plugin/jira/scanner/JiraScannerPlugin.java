package org.jqassistant.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.plugin.jira.model.JiraServer;

/**
 * jQAssistant scanner implementation to scan a given Jira instance to which a connection has already been established.
 */
@Slf4j
public class JiraScannerPlugin extends AbstractScannerPlugin<JiraRestClientWrapper, JiraServer> {
    private static final String PROPERTY_NAME_PROJECTS = "jira.projects";
    private static final String PROPERTY_NAME_CUSTOM_ISSUE_FIELDS = "jira.custom.issue.fields";
    private static final String PROPERTY_NAME_CUSTOM_JQL = "jira.custom.jql";
    private List<String> projects;
    private final Map<String, String> customIssueFields = new HashMap<>();
    private String customJql;

    protected void configure() {
        String projectProperty = this.getStringProperty(PROPERTY_NAME_PROJECTS, null);
        if (StringUtils.isBlank(projectProperty)) {
            throw new RuntimeException("Missing configuration for property jira.projects");
        } else {
            this.projects = List.of(this.getStringProperty(PROPERTY_NAME_PROJECTS, "").split(","));
            String issueFields = this.getStringProperty(PROPERTY_NAME_CUSTOM_ISSUE_FIELDS, null);
            if (StringUtils.isNoneBlank(issueFields)) {
                Stream.of(issueFields.split(","))
                        .map(String::trim)
                        .map((f) -> f.split("="))
                        .forEach((a) -> {
                    this.customIssueFields.put(a[0].trim(), a[1].trim());
                });
            }

            this.customJql = this.getStringProperty(PROPERTY_NAME_CUSTOM_JQL, null);
        }
    }

    public boolean accepts(JiraRestClientWrapper jiraRestClientWrapper, String path, Scope scope) {
        return true;
    }

    public JiraServer scan(JiraRestClientWrapper jiraRestClientWrapper, String path, Scope scope, Scanner scanner) {
        JiraServer jiraServer = this.createRootDescriptor(scanner);
        CacheEndpoint cacheEndpoint = new CacheEndpoint(this.getScannerContext().getStore());
        this.buildCompleteDescriptorGraph(jiraServer, jiraRestClientWrapper, cacheEndpoint, this.customIssueFields);
        return jiraServer;
    }

    private JiraServer createRootDescriptor(Scanner scanner) {
        return scanner.getContext().getStore().create(JiraServer.class);
    }

    private void buildCompleteDescriptorGraph(JiraServer jiraServer, JiraRestClientWrapper jiraRestClientWrapper, CacheEndpoint cacheEndpoint, Map<String, String> customIssueFields) {
        GraphBuilder graphBuilder = new GraphBuilder(jiraRestClientWrapper, cacheEndpoint, customIssueFields, this.customJql);
        graphBuilder.startTraversal(jiraServer, this.projects);
    }
}
