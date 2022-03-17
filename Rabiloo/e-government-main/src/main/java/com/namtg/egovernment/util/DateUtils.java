package com.namtg.egovernment.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private static final String PATTERN_DEFAULT = "dd-MM-yyyy";

    public static Date getStartOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = toCalendar(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = toCalendar(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTime();
    }

    private static Calendar toCalendar(Date date) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_DEFAULT);
        return convertDateToStringCommon(date, sdf);
    }

    public static String convertDateToStringWithPattern(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return convertDateToStringCommon(date, sdf);
    }

    private static String convertDateToStringCommon(Date date, SimpleDateFormat sdf) {
        if (date == null) {
            return "";
        }
        return sdf.format(date);
    }
}
