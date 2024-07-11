package org.jqassistant.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Version;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.plugin.jira.model.JiraComponent;
import org.jqassistant.plugin.jira.model.JiraIssue;
import org.jqassistant.plugin.jira.model.JiraPriority;
import org.jqassistant.plugin.jira.model.JiraProject;
import org.jqassistant.plugin.jira.model.JiraVersion;

@RequiredArgsConstructor
@Slf4j
public class IssueBuilder {
    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;
    private final CommentBuilder commentBuilder;
    private final IssueLinkBuilder issueLinkBuilder;
    private final SubtaskRelationBuilder subtaskRelationBuilder;
    private final Map<String, String> customIssueFields;
    private final String customJql;

    void handleIssues(JiraProject jiraProject) {
        int batchSize = 250;
        int currentStartIndex = 0;

        // batch through issues with 250 elements each
        // every 1000 elements processed, flush the data

        SearchResult searchResult;
        int processed;
        do {
            processed = 0;
            searchResult = this.jiraRestClientWrapper.retrieveIssues(jiraProject.getKey(), this.customJql, batchSize, currentStartIndex);
            for (Issue issue : searchResult.getIssues()) {
                LOGGER.info(String.format("Processing issue with KEY: '%s'", issue.getKey()));

                Map<String, String> customFields = extractCustomFields(issue);
                JiraIssue jiraIssue = this.cacheEndpoint.findOrCreateIssue(issue, customFields);
                jiraProject.getIssues().add(jiraIssue);

                handleAssignee(issue, jiraIssue);
                handleReporter(issue, jiraIssue);
                handleComponents(issue, jiraIssue);
                jiraIssue.setIssueType(this.cacheEndpoint.findOrCreateIssueType(issue.getIssueType()));
                handlePriority(issue, jiraIssue);
                jiraIssue.setStatus(this.cacheEndpoint.findOrCreateStatus(issue.getStatus()));
                handleComments(issue, jiraIssue);
                handleAffectedVersions(issue, jiraIssue);
                handleFixVersions(issue, jiraIssue);
                handleIssueLinks(issue, jiraIssue);
                handleSubtasks(issue, jiraIssue);
                handleLabels(issue, jiraIssue);

                processed++;
            }
            if (currentStartIndex % 1000 == 0 || processed == 0) { // flush every 1000 issues and last run
                this.cacheEndpoint.flush();
            }
            currentStartIndex += batchSize;
        } while (processed != 0);

        LOGGER.info("Finished loading issues.");
    }

    private Map<String, String> extractCustomFields(Issue issue) {
        Map<String, String> customFields = new HashMap<>();
        for (IssueField field : issue.getFields()) {
            if (field.getValue() != null) {
                for (Map.Entry<String, String> entry : this.customIssueFields.entrySet()) {
                    if (entry.getKey().equals(field.getName())) {
                        LOGGER.debug("Field Value: {}, Field Type: {}", field.getValue().toString(), field.getValue().getClass());
                        if (field.getValue() instanceof JSONObject) {
                            try {
                                String label = ((JSONObject)field.getValue()).getString("value");
                                customFields.put(entry.getValue(), label);
                            } catch (JSONException e) {
                                LOGGER.warn("Failed to process JsonObject of custom field {} (Value: {}) for issue {}", field.getName(), field.getValue(), issue.getKey(), e);
                            }
                        } else {
                            customFields.put(entry.getValue(), String.valueOf(field.getValue()));
                        }
                        break;
                    }
                }
            }
        }

        return customFields;
    }

    private void handleAssignee(Issue issue, JiraIssue jiraIssue) {
        if (issue.getAssignee() != null) {
            jiraIssue.setAssignee(this.cacheEndpoint.findOrCreateUser(issue.getAssignee()));
        }
    }

    private void handleReporter(Issue issue, JiraIssue jiraIssue) {
        if (issue.getReporter() != null) {
            jiraIssue.setReporter(this.cacheEndpoint.findOrCreateUser(issue.getReporter()));
        }
    }

    private void handleComponents(Issue issue, JiraIssue jiraIssue) {
        for (BasicComponent component : issue.getComponents()) {
            JiraComponent jiraComponent = this.cacheEndpoint.findComponentOrThrowException(component);
            jiraIssue.getComponents().add(jiraComponent);
        }
    }

    private void handlePriority(Issue issue, JiraIssue jiraIssue) {
        if (issue.getPriority() != null) {
            JiraPriority jiraPriority = this.cacheEndpoint.findPriorityOrThrowException(issue.getPriority());
            jiraIssue.setPriority(jiraPriority);
        }
    }

    private void handleComments(Issue issue, JiraIssue jiraIssue) {
        for (Comment comment : issue.getComments()) {
            this.commentBuilder.handleComment(jiraIssue, comment);
        }
    }

    private void handleAffectedVersions(Issue issue, JiraIssue jiraIssue) {
        if (issue.getAffectedVersions() != null) {
            for (Version affectedVersion : issue.getAffectedVersions()) {
                JiraVersion jiraVersion = this.cacheEndpoint.findOrCreateVersion(affectedVersion);
                jiraIssue.getAffectedVersions().add(jiraVersion);
            }
        }
    }

    private void handleFixVersions(Issue issue, JiraIssue jiraIssue) {
        if (issue.getFixVersions() != null) {
            for (Version fixVersion : issue.getFixVersions()) {
                JiraVersion jiraVersion = this.cacheEndpoint.findOrCreateVersion(fixVersion);
                jiraIssue.getFixedVersions().add(jiraVersion);
            }
        }
    }

    private void handleIssueLinks(Issue issue, JiraIssue jiraIssue) {
        if (issue.getIssueLinks() != null) {
            this.issueLinkBuilder.cache(jiraIssue.getJiraId(), issue.getIssueLinks());
        }
    }

    private void handleSubtasks(Issue issue, JiraIssue jiraIssue) {
        if (issue.getSubtasks() != null) {
            this.subtaskRelationBuilder.cache(jiraIssue.getJiraId(), issue.getSubtasks());
        }
    }

    private void handleLabels(Issue issue, JiraIssue jiraIssue) {
        for (String label : issue.getLabels()) {
            jiraIssue.getLabels().add(this.cacheEndpoint.findOrCreateLabel(label));
        }
    }
}
