package com.github.leifoolsen.jerseyjpa.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateAdapter extends XmlAdapter<String, Date> {
    // See: http://blog.bdoughan.com/2010/07/xmladapter-jaxbs-secret-weapon.html
    // See: http://blog.bdoughan.com/2011/05/jaxb-and-joda-time-dates-and-times.html
    // See: http://stackoverflow.com/questions/3052513/jax-rs-json-java-util-date-unmarshall
    // See: http://eclipse.org/eclipselink/documentation/2.4/moxy/advanced_concepts006.htm

    private Date date;

    public DateAdapter() {}

    public DateAdapter(final String v) { this.date = stringToDate(v); }

    public DateAdapter(final Date v) { this.date = v; }

    public DateAdapter(final LocalDate v) {
        this.date = localDateToDate(v);
    }

    @Override
    public Date unmarshal(final String v) {
        return stringToDate(v);
    }

    @Override
    public String marshal(final Date v) { return dateToString(v); }

    public Date getDate(){ return date; }

    public LocalDate getLocalDate(){ return dateTocalDate(date); }

    public static Date localDateToDate(final LocalDate v) {
        return v != null ? Date.from(v.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public static LocalDate dateTocalDate(final Date v) {
        return v != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(v.getTime()), ZoneId.systemDefault()).toLocalDate()
                : null;
    }

    public static String dateToString(final Date v) {
        return v != null
            ? dateToString(LocalDateTime.ofInstant(Instant.ofEpochMilli(v.getTime()), ZoneId.systemDefault()).toLocalDate())
            : null;
    }

    public static String dateToString(final LocalDate v) {
        return v != null ? v.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }


    public static Date stringToDate(final String v) {
        final String d = StringUtil.blankToNull(v);
        if(d != null) {
            try {
                LocalDate l = LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE);
                return Date.from(l.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            }
            catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Unparsable date: " + v, e);
            }
        }
        return null;
    }
}
