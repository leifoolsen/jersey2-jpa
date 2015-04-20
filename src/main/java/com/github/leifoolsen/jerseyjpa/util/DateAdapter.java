package com.github.leifoolsen.jerseyjpa.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.util.Date;

public class DateAdapter extends XmlAdapter<String, Date> {
    // See: http://blog.bdoughan.com/2010/07/xmladapter-jaxbs-secret-weapon.html
    // See: http://blog.bdoughan.com/2011/05/jaxb-and-joda-time-dates-and-times.html
    // See: http://stackoverflow.com/questions/3052513/jax-rs-json-java-util-date-unmarshall
    // See: http://eclipse.org/eclipselink/documentation/2.4/moxy/advanced_concepts006.htm

    private Date date;

    public DateAdapter() {}

    public DateAdapter(final String v) { this.date = DateLocalDateUtil.stringToDate(v); }

    public DateAdapter(final LocalDate v) { this.date = DateLocalDateUtil.toDate(v); }

    @Override
    public Date unmarshal(final String v) { return DateLocalDateUtil.stringToDate(v); }

    @Override
    public String marshal(final Date v) { return DateLocalDateUtil.dateToString(v); }

    public LocalDate getLocalDate(){ return DateLocalDateUtil.dateToLocalDate(date); }

}
