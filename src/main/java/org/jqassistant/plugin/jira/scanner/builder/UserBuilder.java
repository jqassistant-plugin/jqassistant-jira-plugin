package org.jqassistant.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.plugin.jira.model.JiraUser;

@Slf4j
@RequiredArgsConstructor
class UserBuilder {
    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;

    JiraUser findUserInCacheOrLoadItFromJira(BasicUser basicUser) {
        if (this.cacheEndpoint.isUserAlreadyCached(basicUser)) {
            return this.cacheEndpoint.findUserOrThrowException(basicUser);
        } else {
            User userInJira;
            try {
                userInJira = this.jiraRestClientWrapper.retrieveUser(basicUser.getSelf());
            } catch (RestClientException e) {
                LOGGER.warn(String.format("An error occurred while retrieving an user with self link: '%s'", basicUser.getSelf()), e);
                return null;
            }

            return this.cacheEndpoint.findOrCreateUser(userInJira);
        }
    }
}
