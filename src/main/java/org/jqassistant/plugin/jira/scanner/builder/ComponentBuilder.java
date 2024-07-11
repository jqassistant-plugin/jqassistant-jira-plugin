package org.jqassistant.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.plugin.jira.model.JiraComponent;
import org.jqassistant.plugin.jira.model.JiraProject;
import org.jqassistant.plugin.jira.model.JiraUser;

@Slf4j
@RequiredArgsConstructor
class ComponentBuilder {
    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;
    private final UserBuilder userBuilder;

    void handleComponents(JiraProject jiraProject, Iterable<BasicComponent> basicComponentList) {
        for (BasicComponent basicComponent : basicComponentList) {
            Component component;
            try {
                component = this.jiraRestClientWrapper.retrieveComponent(basicComponent.getSelf());
            } catch (RestClientException e) {
                LOGGER.warn(String.format("An error occured while retrieving a component with self link: '%s'", basicComponent.getSelf()), e);
                continue;
            }

            JiraComponent jiraComponent = this.cacheEndpoint.findOrCreateComponent(component);
            if (component.getLead() != null) {
                JiraUser jiraUser = this.userBuilder.findUserInCacheOrLoadItFromJira(component.getLead());
                jiraComponent.setLeader(jiraUser);
            }

            jiraProject.getComponents().add(jiraComponent);
        }
    }
}
