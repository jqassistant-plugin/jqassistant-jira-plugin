package org.jqassistant.plugin.jira.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

@Label("Label")
public interface JiraLabel extends Descriptor {
    @Property("name")
    String getName();

    void setName(String var1);
}
