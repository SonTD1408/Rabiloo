package com.namtg.egovernment.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String convertTimeToString(Date date) {
        long millisCurrent = System.currentTimeMillis();
        long createdDate = date.getTime();

        StringBuilder suffix = new StringBuilder("ngày");
        long distance = millisCurrent - createdDate;

        Long numberTimeBefore = TimeUnit.MILLISECONDS.toDays(distance);
        if (numberTimeBefore < 1) {
            numberTimeBefore = TimeUnit.MILLISECONDS.toHours(distance);
            suffix.delete(0, 4);
            suffix.append("giờ");
        }
        if (numberTimeBefore < 1) {
            numberTimeBefore = TimeUnit.MILLISECONDS.toMinutes(distance);
            suffix.delete(0, 3);
            suffix.append("phút");
        }
        if (numberTimeBefore < 1) {
            numberTimeBefore = TimeUnit.MILLISECONDS.toSeconds(distance);
            suffix.delete(0, 4);
            suffix.append("giây");
        }

        StringBuilder strBld = new StringBuilder();
        return strBld.append(numberTimeBefore.toString()).append(" ").append(suffix).toString();
    }
}
