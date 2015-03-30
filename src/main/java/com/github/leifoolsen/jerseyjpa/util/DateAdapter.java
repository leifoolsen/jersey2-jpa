package com.github.leifoolsen.jerseyjpa.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAdapter {
    private Date date;

    public DateAdapter(String date){
        this.date = getDateFromString(date);
    }

    public Date getDate(){
        return this.date;
    }

    public static Date getDateFromString(String dateString) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return df.parse(dateString);
        } catch (ParseException e) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                return df.parse(dateString);
            } catch (ParseException e2) {
                //TODO: throw Exception ...
                return null;
            }
        }
    }
}
