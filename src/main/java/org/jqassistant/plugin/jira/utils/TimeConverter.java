package org.jqassistant.plugin.jira.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.joda.time.DateTime;

/**
 * Utility to convert time objects.
 */
public abstract class TimeConverter {

    public static ZonedDateTime convertTime(DateTime dateTime) {
        return dateTime == null ? null : ZonedDateTime.ofLocal(
                LocalDateTime.of(dateTime.getYear(),
                        dateTime.getMonthOfYear(),
                        dateTime.getDayOfMonth(),
                        dateTime.getHourOfDay(),
                        dateTime.getMinuteOfHour(),
                        dateTime.getSecondOfMinute(),
           dateTime.getMillisOfSecond() * 1000000),
                ZoneId.of(dateTime.getZone().getID(), ZoneId.SHORT_IDS),
                ZoneOffset.ofTotalSeconds(dateTime.getZone().getOffset(dateTime) / 1000));
    }
}
