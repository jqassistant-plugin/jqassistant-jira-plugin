package org.jqassistant.plugin.jira.cache;

/**
 * Exception to signal that an entity could not be found in the database.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
