package org.jqassistant.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import org.jqassistant.plugin.jira.model.basic.Jira;
import org.jqassistant.plugin.jira.model.basic.JiraName;

@Label("User")
public interface JiraUser extends Jira, JiraName {
    @Property("self")
    String getSelf();

    void setSelf(String var1);

    @Property("displayName")
    String getDisplayName();

    void setDisplayName(String var1);

    @Property("emailAddress")
    String getEmailAddress();

    void setEmailAddress(String var1);

    @Property("active")
    boolean isActive();

    void setActive(boolean var1);
}
