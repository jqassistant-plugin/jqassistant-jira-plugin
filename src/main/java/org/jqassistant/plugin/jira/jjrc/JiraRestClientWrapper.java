package org.jqassistant.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.domain.Component;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.User;
import java.net.URI;
import java.util.stream.Stream;

/**
 * Wrapper for the Jira client with convenience methods.
 */
public interface JiraRestClientWrapper {

    /**
     * Retrieve meta data of the Jira server.
     *
     * @return The meta data.
     */
    ServerInfo retrieveServerInfo();

    /**
     * Retrieve priorities from Jira.
     *
     * @return The priorities.
     */
    Stream<Priority> retrievePriorities();

    /**
     * Retrieve statuses from Jira.
     *
     * @return The statuses.
     */
    Stream<Status> retrieveStatuses();

    /**
     * Retrieve a project by its key from Jira.
     *
     * @param key The project key.
     * @return The project.
     */
    Project retrieveProject(String key);

    /**
     * Retrieve a component by its uri from Jira.
     *
     * @param uri The components uri.
     * @return The component.
     */
    Component retrieveComponent(URI uri);

    /**
     * Retrieve a user by its uri from Jira.
     *
     * @param uri The users uri.
     * @return The user.
     */
    User retrieveUser(URI uri);

    /**
     * Retrieve all issues matching the given paramters from Jira.
     *
     * @param projectKey The project the issue needs to be part of.
     * @param customJql  The custom jql (Jira Query Language) statement.
     * @param maxResults The number of results to retrieve.
     * @param startAt    The offset.
     * @return The search result with the issues.
     */
    SearchResult retrieveIssues(String projectKey, String customJql, int maxResults, int startAt);
}
