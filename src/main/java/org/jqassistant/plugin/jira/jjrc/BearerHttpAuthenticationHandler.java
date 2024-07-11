package org.jqassistant.plugin.jira.jjrc;

import com.atlassian.httpclient.api.Request;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;

/**
 * Authentication handler for bearer token.
 */
public class BearerHttpAuthenticationHandler implements AuthenticationHandler {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String token;

    public BearerHttpAuthenticationHandler(String token) {
        this.token = token;
    }

    /**
     * Configure the given request builder with the authorization header.
     *
     * @param builder The request builder.
     */
    public void configure(Request.Builder builder) {
        builder.setHeader(AUTHORIZATION_HEADER, "Bearer " + this.token);
    }
}
