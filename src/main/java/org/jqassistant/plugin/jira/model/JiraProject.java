package org.jqassistant.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import java.util.List;
import org.jqassistant.plugin.jira.model.basic.Jira;
import org.jqassistant.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.plugin.jira.model.basic.JiraID;
import org.jqassistant.plugin.jira.model.basic.JiraName;

@Label("Project")
public interface JiraProject extends Jira, JiraID, JiraName, JiraDescription {
    @Property("key")
    String getKey();

    void setKey(String var1);

    @Property("uri")
    String getUri();

    void setUri(String var1);

    @Relation("CONTAINS")
    List<JiraIssue> getIssues();

    @Relation("LEAD_BY")
    JiraUser getLead();

    void setLead(JiraUser var1);

    @Relation("HAS_VERSION")
    List<JiraVersion> getVersions();

    @Relation("HAS_COMPONENT")
    List<JiraComponent> getComponents();

    @Relation("DEFINES")
    List<JiraIssueType> getIssueTypes();
}
