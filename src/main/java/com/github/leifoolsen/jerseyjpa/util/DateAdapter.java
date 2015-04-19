package com.github.leifoolsen.jerseyjpa.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateAdapter extends XmlAdapter<String, Date> {
    private Date date;

    public DateAdapter(String v) { this.date = stringToDate(v); }

    public DateAdapter(Date v) { this.date = v; }

    public DateAdapter(LocalDate v) {
        Instant instant = v.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        this.date = Date.from(instant);
    }

    public Date getDate(){
        return this.date;
    }

    @Override
    public Date unmarshal(String v) {
        return stringToDate(v);
    }

    @Override
    public String marshal(Date v) {
        return dateToString(v);
    }

    @Deprecated
    public static String dateToString(final Date v) {
        if(v != null) {
            return new SimpleDateFormat("yyyy-MM-dd").format(v);
        }
        return null;
    }

    public static String dateToString(final LocalDate v) {
        //return v != null ? v.format(DateTimeFormatter.BASIC_ISO_DATE) : null;
        return v != null ? v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
    }

    public static Date stringToDate(String v) {

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
}
