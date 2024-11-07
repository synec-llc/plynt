package com.synec.plynt.functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String getRelativeTime(String newsDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // Parse the input date
            Date date = dateFormat.parse(newsDate);
            Date now = new Date();

            // Calculate the time difference in milliseconds
            long diffInMillis = now.getTime() - date.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);

            // Determine relative time
            if (diffInDays == 0) {
                if (diffInHours < 1) {
                    return diffInMinutes + " minute" + (diffInMinutes == 1 ? "" : "s") + " ago";
                } else {
                    return diffInHours + " hour" + (diffInHours == 1 ? "" : "s") + " ago";
                }
            } else if (diffInDays == 1) {
                return "Yesterday";
            } else if (diffInDays < 7) {
                return diffInDays + " day" + (diffInDays == 1 ? "" : "s") + " ago";
            } else if (diffInDays < 30) {
                long weeks = diffInDays / 7;
                return weeks + " week" + (weeks == 1 ? "" : "s") + " ago";
            } else if (diffInDays < 365) {
                long months = diffInDays / 30;
                return months + " month" + (months == 1 ? "" : "s") + " ago";
            } else {
                long years = diffInDays / 365;
                return years + " year" + (years == 1 ? "" : "s") + " ago";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
