package com.github.leifoolsen.jerseyjpa.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateTimeAdapter extends XmlAdapter<String, Date> {

    // See: http://blog.bdoughan.com/2010/07/xmladapter-jaxbs-secret-weapon.html
    // See: http://blog.bdoughan.com/2011/05/jaxb-and-joda-time-dates-and-times.html
    // See: http://stackoverflow.com/questions/3052513/jax-rs-json-java-util-date-unmarshall
    // See: http://eclipse.org/eclipselink/documentation/2.4/moxy/advanced_concepts006.htm

    private Date date;

    public DateTimeAdapter() {}

    public DateTimeAdapter(final String v) { this.date = stringToDate(v); }

    public DateTimeAdapter(final Date v) { this.date = new Date(v.getTime()); }

    public DateTimeAdapter(final LocalDateTime v) {
        this.date = v != null ? Date.from(v.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    @Override
    public Date unmarshal(final String v) {
        return stringToDate(v);
    }

    @Override
    public String marshal(final Date v) {
        return dateToString(v);
    }

    public Date getDate(){ return new Date(date.getTime()); }


    public static String dateToString(final Date v) {
        return v != null
                ? dateToString(LocalDateTime.ofInstant(Instant.ofEpochMilli(v.getTime()), ZoneId.systemDefault()))
                : null;
    }

    public static String dateToString(final LocalDateTime v) {
        return v != null ? v.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }


    public static Date stringToDate(final String v) {
        final String d = StringUtil.blankToNull(v);
        if(d != null) {
            try {
                LocalDateTime l = LocalDateTime.parse(d, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return Date.from(l.atZone(ZoneId.systemDefault()).toInstant());
            }
            catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Unparsable date: " + v, e);
            }
        }
        return null;
    }


    /*
    public static String dateToString(final Date v) {
        return v != null ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(v) : null;
    }

    public static Date stringToDate(final String v) {

        final String d = StringUtil.blankToNull(v);
        if(d != null) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(d);
            }
            catch (ParseException e) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(d);
                }
                catch (ParseException e2) {
                    throw new IllegalArgumentException("Unparsable date: " + v, e2);
                }
            }
        }
        return null;
    }
    */
}
