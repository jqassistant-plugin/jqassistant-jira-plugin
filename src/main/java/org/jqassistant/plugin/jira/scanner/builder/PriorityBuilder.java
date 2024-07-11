package org.jqassistant.plugin.jira.scanner.builder;

import lombok.RequiredArgsConstructor;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.plugin.jira.model.JiraServer;

@RequiredArgsConstructor
public class PriorityBuilder {
    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;

    public void handlePriorities(JiraServer jiraServer) {
        this.jiraRestClientWrapper.retrievePriorities()
                .map(this.cacheEndpoint::findOrCreatePriority)
                .forEach(p -> jiraServer.getPriorities().add(p));
    }
}
