package org.jqassistant.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.plugin.jira.model.JiraServer;
import org.jqassistant.plugin.jira.utils.TimeConverter;

@Slf4j
@RequiredArgsConstructor
public class ServerInfoBuilder {
    private final JiraRestClientWrapper jiraRestClientWrapper;

    public void handleServerInfo(JiraServer jiraServer) {
        ServerInfo serverInfo;
        try {
            serverInfo = this.jiraRestClientWrapper.retrieveServerInfo();
        } catch (RestClientException e) {
            LOGGER.warn("An error occured while retrieving the server info:", e);
            return;
        }

        jiraServer.setBaseUri(serverInfo.getBaseUri().toString());
        jiraServer.setVersion(serverInfo.getVersion());
        jiraServer.setBuildNumber(serverInfo.getBuildNumber());
        jiraServer.setBuildDate(TimeConverter.convertTime(serverInfo.getBuildDate()));
        jiraServer.setServerTime(TimeConverter.convertTime(serverInfo.getServerTime()));
        jiraServer.setScmInfo(serverInfo.getScmInfo());
        jiraServer.setServerTitle(serverInfo.getServerTitle());
    }
}
