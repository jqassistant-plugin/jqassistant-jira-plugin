package org.jqassistant.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import org.jqassistant.plugin.jira.model.basic.Jira;
import org.jqassistant.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.plugin.jira.model.basic.JiraID;
import org.jqassistant.plugin.jira.model.basic.JiraName;

@Label("IssueType")
public interface JiraIssueType extends Jira, JiraID, JiraName, JiraDescription {
    @Property("isSubtask")
    boolean isSubtask();

    void setSubtask(boolean var1);

    @Property("iconUri")
    String getIconUri();

    void setIconUri(String var1);
}
