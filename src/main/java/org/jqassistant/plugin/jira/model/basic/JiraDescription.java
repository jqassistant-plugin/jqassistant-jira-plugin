package org.jqassistant.plugin.jira.model.basic;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Property;

public interface JiraDescription extends Descriptor {
    @Property("description")
    String getDescription();

    void setDescription(String var1);
}
