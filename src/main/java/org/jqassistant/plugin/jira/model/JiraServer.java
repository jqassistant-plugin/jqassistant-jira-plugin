package org.jqassistant.plugin.jira.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import java.time.ZonedDateTime;
import java.util.List;
import org.jqassistant.plugin.jira.model.basic.Jira;

@Label("Server")
public interface JiraServer extends Jira, FileDescriptor {
    @Property("baseUri")
    String getBaseUri();

    void setBaseUri(String var1);

    @Property("version")
    String getVersion();

    void setVersion(String var1);

    @Property("buildNumber")
    long getBuildNumber();

    void setBuildNumber(long var1);

    @Property("buildDate")
    ZonedDateTime getBuildDate();

    void setBuildDate(ZonedDateTime var1);

    @Property("serverTime")
    ZonedDateTime getServerTime();

    void setServerTime(ZonedDateTime var1);

    @Property("scmInfo")
    String getScmInfo();

    void setScmInfo(String var1);

    @Property("serverTitle")
    String getServerTitle();

    void setServerTitle(String var1);

    @Relation("DEFINES_PROJECT")
    List<JiraProject> getProjects();

    @Relation("DEFINES_PRIORITY")
    List<JiraPriority> getPriorities();

    @Relation("DEFINES_STATUS")
    List<JiraStatus> getStatuses();
}
