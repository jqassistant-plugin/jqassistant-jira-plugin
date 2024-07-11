package org.jqassistant.plugin.jira.scanner.builder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.plugin.jira.model.JiraServer;

@Slf4j
@RequiredArgsConstructor
public class StatusBuilder {
    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;

    public void handleStatuses(JiraServer jiraServer) {
        this.jiraRestClientWrapper.retrieveStatuses()
                .map(this.cacheEndpoint::findOrCreateStatus)
                .forEach(s -> jiraServer.getStatuses().add(s));
    }
}
