package org.jqassistant.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.Subtask;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.cache.EntityNotFoundException;
import org.jqassistant.plugin.jira.model.JiraIssue;

@Slf4j
@RequiredArgsConstructor
public class SubtaskRelationBuilder {
    private final CacheEndpoint cacheEndpoint;
    private final Map<Long, Iterable<Subtask>> subtaskCache = new HashMap<>();

    public void handleSubtaskRelations() {
        for (Map.Entry<Long, Iterable<Subtask>> entry : this.subtaskCache.entrySet()) {
            for (Subtask subtask : entry.getValue()) {
                try {
                    String targetIssueUri = subtask.getIssueUri().toString();
                    long targetIssueId = Long.parseLong(targetIssueUri.substring(targetIssueUri.lastIndexOf(47) + 1));
                    JiraIssue targetIssue = this.cacheEndpoint.findIssueOrThrowException(targetIssueId);
                    JiraIssue sourceIssue = this.cacheEndpoint.findIssueOrThrowException(entry.getKey());
                    sourceIssue.getSubtasks().add(targetIssue);
                } catch (EntityNotFoundException e) {
                    LOGGER.warn(String.format("Creating a relation between subtasks failed with message: '%s'. Here is the 'Subtask' object: '%s'. This can happen as subtask relations can point at issues which have not been loaded, e.g. if they are in other projects.", subtask.toString(), e.getMessage()));
                } catch (Exception e) {
                    LOGGER.error("Failed to import Subtask relation", e);
                }
            }
        }
    }

    public void cache(Long issueID, Iterable<Subtask> subtasks) {
        this.subtaskCache.put(issueID, subtasks);
    }
}
