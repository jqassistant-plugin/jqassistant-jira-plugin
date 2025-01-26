package org.jqassistant.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Component;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Wrapper for the jira rest client with convenience methods.
 */
@Slf4j
public class DefaultJiraRestClientWrapper implements JiraRestClientWrapper {
    private static final String JQL_ISSUE_QUERY = "project=%s";
    private static final Set<String> ALL_FIELDS = Collections.singleton("*all");
    private final JiraRestClient jiraRestClient;

    public DefaultJiraRestClientWrapper(URI uri, String token, String cookie) {
        AsynchronousJiraRestClientFactory clientFactory = new AsynchronousJiraRestClientFactory();
        AuthenticationHandler authenticationHandler;
        if (token != null) {
            authenticationHandler = new BearerHttpAuthenticationHandler(token);
        } else if (cookie != null) {
            authenticationHandler = new SessionCookieAuthenticationHandler(cookie);
        } else {
            throw new IllegalArgumentException("Either token or cookie must be provided");
        }
        this.jiraRestClient = clientFactory.createWithAuthenticationHandler(uri, authenticationHandler);
    }

    public ServerInfo retrieveServerInfo() {
        return this.jiraRestClient.getMetadataClient().getServerInfo().claim();
    }

    public Stream<Priority> retrievePriorities() {
        return StreamSupport.stream(this.jiraRestClient.getMetadataClient().getPriorities().claim().spliterator(), false);
    }

    public Stream<Status> retrieveStatuses() {
        return StreamSupport.stream(this.jiraRestClient.getMetadataClient().getStatuses().claim().spliterator(), false);
    }

    public Project retrieveProject(String key) {
        return this.jiraRestClient.getProjectClient().getProject(key).claim();
    }

    public Component retrieveComponent(URI uri) {
        return this.jiraRestClient.getComponentClient().getComponent(uri).claim();
    }

    public User retrieveUser(URI uri) {
        return this.jiraRestClient.getUserClient().getUser(uri).claim();
    }

    public SearchResult retrieveIssues(String projectKey, String customJql, int maxResults, int startAt) {
        String query = String.format(JQL_ISSUE_QUERY, projectKey);
        if (StringUtils.isNotBlank(customJql)) {
            query = query + " and " + customJql;
        }

        LOGGER.info("Executing Jira issue scan with JQL '{}', batch size: '{}', and start index: '{}'", query, maxResults, startAt);
        return this.jiraRestClient.getSearchClient()
                .searchJql(query, maxResults, startAt, ALL_FIELDS).claim();
    }
}
