package org.jqassistant.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.IssueLink;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.cache.EntityNotFoundException;
import org.jqassistant.plugin.jira.model.JiraIssue;
import org.jqassistant.plugin.jira.model.JiraIssueLink;

@Slf4j
@RequiredArgsConstructor
public class IssueLinkBuilder {
    private final CacheEndpoint cacheEndpoint;
    private final Map<Long, Iterable<IssueLink>> issueLinkCache = new HashMap<>();

    public void handleIssueLinks() {
        for (Map.Entry<Long, Iterable<IssueLink>> entry : this.issueLinkCache.entrySet()) {
            for (IssueLink issueLink : entry.getValue()) {
                try {
                    JiraIssueLink jiraIssueLink = this.cacheEndpoint.createIssueLink(issueLink);
                    this.cacheEndpoint.findIssueOrThrowException(entry.getKey()).getIssueLinks().add(jiraIssueLink);
                    String targetIssueUri = issueLink.getTargetIssueUri().toString();
                    long targetIssueId = Long.parseLong(targetIssueUri.substring(targetIssueUri.lastIndexOf("/") + 1));
                    JiraIssue targetIssue = this.cacheEndpoint.findIssueOrThrowException(targetIssueId);
                    jiraIssueLink.setTargetIssue(targetIssue);
                } catch (EntityNotFoundException e) {
                    LOGGER.warn(String.format("Creating a link between issues failed with message: '%s'. Here is the 'IssueLink' object: '%s'. This can happen as issue links can point at issues which have not been loaded, e.g. if they are in other projects.", issueLink.toString(), e.getMessage()));
                } catch (Exception e) {
                    LOGGER.error("Failed to import Issue link", e);
                }
            }
        }
    }

    public void cache(Long issueID, Iterable<IssueLink> issueLinks) {
        this.issueLinkCache.put(issueID, issueLinks);
    }
}
