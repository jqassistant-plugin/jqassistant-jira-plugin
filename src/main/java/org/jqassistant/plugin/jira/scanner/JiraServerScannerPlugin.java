package org.jqassistant.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.impl.scanner.AbstractUriScannerPlugin;
import java.net.URI;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.jira.jjrc.DefaultJiraRestClientWrapper;
import org.jqassistant.plugin.jira.jjrc.JiraRestClientWrapper;

/**
 * jQAssistant scanner plugin for Jira instances. This scanner is just used to create a server connection.
 */
@Slf4j
public class JiraServerScannerPlugin extends AbstractUriScannerPlugin<JiraRestClientWrapper> {
    private static final String PROPERTY_NAME_COOKIE = "jira.authentication.cookie";
    private static final String PROPERTY_NAME_TOKEN = "jira.authentication.token";
    private String cookie;
    private String token;

    protected void configure() {
        this.cookie = this.getStringProperty(PROPERTY_NAME_COOKIE, null);
        this.token = this.getStringProperty(PROPERTY_NAME_TOKEN, null);
        if (this.cookie == null && this.token == null) {
            throw new IllegalArgumentException("You must specify cookie or token");
        }
    }

    public boolean accepts(URI uri, String path, Scope scope) {
        return JiraScope.SERVER == scope;
    }

    protected Optional<JiraRestClientWrapper> getResource(URI uri, ScannerContext context) {
        return this.resolve(uri, () -> this.connect(uri, this.token, this.cookie), context);
    }

    private JiraRestClientWrapper connect(URI uri, String token, String cooke) {
        LOGGER.info("Connecting to Jira server at {}", uri);
        return new DefaultJiraRestClientWrapper(uri, token, cookie);
    }
}
