package org.jqassistant.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scope;

/**
 * Scope for qualifying URIs to scan.
 */
public enum JiraScope implements Scope {
    SERVER;

    public String getPrefix() {
        return "jira";
    }

    public String getName() {
        return this.name();
    }
}
