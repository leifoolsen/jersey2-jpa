package com.github.leifoolsen.jerseyjpa.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAdapter {
    // See: http://blog.bdoughan.com/2010/07/xmladapter-jaxbs-secret-weapon.html
    // See: http://stackoverflow.com/questions/3052513/jax-rs-json-java-util-date-unmarshall

    private Date date;

    public DateAdapter() {}

    public DateAdapter(String date){
        this.date = getDateFromString(date);
    }

    public DateAdapter(Date date){
        this.date = date;
    }

    public Date getDate(){
        return this.date;
    }

    public static Date getDateFromString(String v) {

        final String d = StringUtil.blankToNull(v);
        if(d != null) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                return df.parse(d);
            } catch (ParseException e) {
                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    return df.parse(d);
                } catch (ParseException e2) {
                    try {
                        DateFormat df = new SimpleDateFormat("HH:mm:ss");
                        return df.parse(d);
                    } catch (ParseException e3) {
                        throw new IllegalArgumentException(e3);
                    }
                }
            }
        }
        return null;
    }
}
