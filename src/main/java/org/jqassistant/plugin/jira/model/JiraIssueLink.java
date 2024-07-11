package org.jqassistant.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import org.jqassistant.plugin.jira.model.basic.Jira;
import org.jqassistant.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.plugin.jira.model.basic.JiraName;

@Label("IssueLink")
public interface JiraIssueLink extends Jira, JiraName, JiraDescription {
    @Property("targetIssueKey")
    String getTargetIssueKey();

    void setTargetIssueKey(String var1);

    @Property("targetIssueUri")
    String getTargetIssueUri();

    void setTargetIssueUri(String var1);

    @Relation("POINTS_AT")
    JiraIssue getTargetIssue();

    void setTargetIssue(JiraIssue var1);
}
