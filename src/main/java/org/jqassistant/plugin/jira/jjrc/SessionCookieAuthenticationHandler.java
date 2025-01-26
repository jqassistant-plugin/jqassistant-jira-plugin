package org.jqassistant.plugin.jira.jjrc;

import com.atlassian.httpclient.api.Request;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;

/**
 * Authentication handler for session cookie.
 */
public class SessionCookieAuthenticationHandler implements AuthenticationHandler {
    private static final String COOKIE_HEADER = "cookie";
    private final String cookie;

    public SessionCookieAuthenticationHandler(String cookie) {
        this.cookie = cookie;
    }

    /**
     * Configure the given request builder with the authorization header.
     *
     * @param builder The request builder.
     */
    public void configure(Request.Builder builder) {
        builder.setHeader(COOKIE_HEADER, this.cookie);
    }
}
