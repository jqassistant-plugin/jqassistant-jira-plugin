package org.jqassistant.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import java.time.ZonedDateTime;
import org.jqassistant.plugin.jira.model.basic.Jira;
import org.jqassistant.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.plugin.jira.model.basic.JiraID;
import org.jqassistant.plugin.jira.model.basic.JiraName;

@Label("Version")
public interface JiraVersion extends Jira, JiraID, JiraName, JiraDescription {
    @Property("isArchived")
    boolean isArchived();

    void setArchived(boolean var1);

    @Property("isReleased")
    boolean isReleased();

    void setReleased(boolean var1);

    @Property("releaseDate")
    ZonedDateTime getReleaseDate();

    void setReleaseDate(ZonedDateTime var1);
}
