package org.jqassistant.plugin.jira.model.basic;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Property;

public interface JiraID extends Descriptor {
    @Property("jiraId")
    Long getJiraId();

    void setJiraId(Long var1);

    @Property("self")
    String getSelf();

    void setSelf(String var1);
}
