package com.github.leifoolsen.jerseyjpa.util;

import com.google.common.base.MoreObjects;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateLocalDateUtil {
    // A lot of code from: http://stackoverflow.com/questions/21242110/convert-java-util-date-to-java-time-localdate

    private DateLocalDateUtil() {}


    /**
     * Creates {@link LocalDate} from {@code java.util.Date} or it's subclasses.
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(final Date date) {
        return dateToLocalDate(date, ZoneId.systemDefault());
    }

    /**
     * Creates {@link LocalDate} from {@code java.util.Date} or it's subclasses.
     *
     * @param date
     * @param zone
     * @return
     */
    public static LocalDate dateToLocalDate(final Date date, ZoneId zone) {
        if (date == null)
            return null;

        return date instanceof java.sql.Date
                ? ((java.sql.Date) date).toLocalDate()
                : Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDate();
    }


    /**
     * Calls {@link #dateToLocalDateTime(Date, ZoneId)} with the system default time zone.
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(java.util.Date date) {
        return dateToLocalDateTime(date, ZoneId.systemDefault());
    }

    /**
     * Creates {@link LocalDateTime} from {@code java.util.Date} or it's subclasses. Null-safe.
     *
     * @param date
     * @param zone
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(java.util.Date date, ZoneId zone) {
        if (date == null)
            return null;

        return date instanceof java.sql.Timestamp
                ? ((java.sql.Timestamp) date).toLocalDateTime()
                :  Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDateTime();
    }

    /**
     * Calls {@link #toDate(Object, ZoneId)} with the system default time zone.
     *
     * @param date
     * @return
     */
    public static java.util.Date toDate(final Object date) {
        //return localDate != null ? Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null;
        return toDate(date, ZoneId.systemDefault());
    }

    /**
     * Creates a {@link java.util.Date} from various date objects. Currently supports:
     * <ul>
     * <li>{@link java.util.Date}
     * <li>{@link java.sql.Date}
     * <li>{@link java.sql.Timestamp}
     * <li>{@link java.time.LocalDate}
     * <li>{@link java.time.LocalDateTime}
     * <li>{@link java.time.ZonedDateTime}
     * <li>{@link java.time.Instant}
     * </ul>
     *
     * @param zone Time zone, used only if the input object is LocalDate or LocalDateTime.
     *
     * @return {@link java.util.Date} (exactly this class, not a subclass, such as java.sql.Date)
     * @throws UnsupportedOperationException if the date parameter is not supported
     */

    public static java.util.Date toDate(final Object date, final ZoneId zone) {
        if (date == null)
            return null;

        if (date instanceof java.sql.Date || date instanceof java.sql.Timestamp)
            return new java.util.Date(((java.util.Date) date).getTime());

        if (date instanceof java.util.Date)
           //return (java.util.Date) date;
            return new java.util.Date(((Date) date).getTime());

        if (date instanceof LocalDate)
            return java.util.Date.from(((LocalDate) date).atStartOfDay(zone).toInstant());

        if (date instanceof LocalDateTime)
            return java.util.Date.from(((LocalDateTime) date).atZone(zone).toInstant());

        if (date instanceof ZonedDateTime)
            return java.util.Date.from(((ZonedDateTime) date).toInstant());

        if (date instanceof Instant)
            return java.util.Date.from((Instant) date);

        throw new UnsupportedOperationException(
                "Could not convert " + date.getClass().getName() + " to java.util.Date");
    }


    /**
     * Creates an {@link Instant} from {@code java.util.Date} or it's subclasses. Null-safe.
     *
     * @param date
     * @return
     */
    public static Instant dateToInstant(Date date) {
        return date != null ? Instant.ofEpochMilli(date.getTime()) : null;
    }

    /**
     * Calls {@link #asZonedDateTime(Date, ZoneId)} with the system default time zone.
     *
     * @param date
     * @return
     */
    public static ZonedDateTime asZonedDateTime(Date date) {
        return asZonedDateTime(date, ZoneId.systemDefault());
    }

    /**
     * Creates {@link ZonedDateTime} from {@code java.util.Date} or it's subclasses. Null-safe.
     *
     * @param date
     * @param zone
     * @return
     */
    public static ZonedDateTime asZonedDateTime(Date date, ZoneId zone) {
        return date != null ? dateToInstant(date).atZone(zone) : null;
    }

    /**
     *
     * @param date
     * @return
     */
    public static String dateToString(final Date date) {
        return dateToString(dateToLocalDate(date));
    }

    /**
     *
     * @param date
     * @return
     */
    public static String dateToString(final LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    /**
     *
     * @param date
     * @return
     */
    public static Date stringToDate(final String date) {
        if(MoreObjects.firstNonNull(date, "").trim().length() > 0) {
            try {
                LocalDate d = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
                return toDate(d);
            }
            catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Unparsable date: " + date, e);
            }
        }
        return null;
    }

}
