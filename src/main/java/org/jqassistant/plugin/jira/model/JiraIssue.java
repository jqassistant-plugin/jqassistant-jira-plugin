package org.jqassistant.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import java.time.ZonedDateTime;
import java.util.List;
import org.jqassistant.plugin.jira.model.basic.Jira;
import org.jqassistant.plugin.jira.model.basic.JiraAuditInformation;
import org.jqassistant.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.plugin.jira.model.basic.JiraID;

@Label("Issue")
public interface JiraIssue extends Jira, JiraID, JiraAuditInformation, JiraDescription {
    @Property("key")
    String getKey();

    void setKey(String var1);

    @Property("summary")
    String getSummary();

    void setSummary(String var1);

    @Property("dueDate")
    ZonedDateTime getDueDate();

    void setDueDate(ZonedDateTime var1);

    @Relation("REPORTED_BY")
    JiraUser getReporter();

    void setReporter(JiraUser var1);

    @Relation("ASSIGNED_TO")
    JiraUser getAssignee();

    void setAssignee(JiraUser var1);

    @Relation("CONCERNS")
    List<JiraComponent> getComponents();

    @Relation("IS_OF_TYPE")
    JiraIssueType getIssueType();

    void setIssueType(JiraIssueType var1);

    @Relation("IS_OF_PRIORITY")
    JiraPriority getPriority();

    void setPriority(JiraPriority var1);

    @Relation("HAS_STATUS")
    JiraStatus getStatus();

    void setStatus(JiraStatus var1);

    @Relation("HAS_COMMENT")
    List<JiraComment> getComments();

    @Relation("AFFECTS")
    List<JiraVersion> getAffectedVersions();

    @Relation("FIXES")
    List<JiraVersion> getFixedVersions();

    @Relation("HAS_LINK")
    List<JiraIssueLink> getIssueLinks();

    @Relation("HAS_SUBTASK")
    List<JiraIssue> getSubtasks();

    @Relation("HAS_LABEL")
    List<JiraLabel> getLabels();
}
