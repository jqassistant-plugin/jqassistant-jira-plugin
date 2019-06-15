package org.jqassistant.contrib.plugin.jira.rate_limits;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.atlassian.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;

/**
 * This test can be used to test the rate limits of the Jira REST API.
 * <p>
 * Username, password and URL can be configured via environment variables: JIRA_USERNAME, JIRA_PASSWORD and JIRA_URL.
 * All three must be provided for this test.
 */
@Slf4j
public class CloudRateLimitsTest {

    private final static String USERNAME = System.getenv("JIRA_USERNAME");
    private final static String PASSWORD = System.getenv("JIRA_PASSWORD");

    private final static String URL = System.getenv("JIRA_URL");

    private static JiraRestClient jiraRestClient;

    @BeforeClass
    public static void setUpJiraRestClient() {

        jiraRestClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(URL), USERNAME, PASSWORD);
    }

    @Ignore
    @Test
    public void when_oneRequestIsMadeAgainstJira_succeed() {

        log.info("Loading all projects from JIRA ...");
        Iterable<BasicProject> basicProjects = this.requestAllProjectsFromJIRASynchronously();


        for (BasicProject basicProject : basicProjects) {
            log.info("Found project: {}", basicProject);
        }
    }

    @Ignore
    @Test
    public void when_fivehundredRequestAreMadeAgainstJira_succeed() {

        for(int i = 0; i < 500; i++) {

            if (i % 10 == 0) {
                log.info("{} requests made against JIRA", i);
            }

            this.requestAllProjectsFromJIRASynchronously();
        }
    }

    private Iterable<BasicProject> requestAllProjectsFromJIRASynchronously() {

        Promise<Iterable<BasicProject>> basicProjectsPromise = jiraRestClient.getProjectClient().getAllProjects();
        return basicProjectsPromise.claim();
    }
}
