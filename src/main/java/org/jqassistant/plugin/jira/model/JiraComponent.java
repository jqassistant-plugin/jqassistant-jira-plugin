package org.jqassistant.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import org.jqassistant.plugin.jira.model.basic.Jira;
import org.jqassistant.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.plugin.jira.model.basic.JiraID;
import org.jqassistant.plugin.jira.model.basic.JiraName;

@Label("Component")
public interface JiraComponent extends Jira, JiraID, JiraName, JiraDescription {
    @Relation("LEAD_BY")
    JiraUser getLeader();

    void setLeader(JiraUser var1);
}
