package com.example.barberappointmentapp.utils;

import com.example.barberappointmentapp.models.TimeInterval;
import com.example.barberappointmentapp.models.WorkWindow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {
    private TimeUtils() {}
    private static SimpleDateFormat sdf(String pattern) {
        SimpleDateFormat s = new SimpleDateFormat(pattern, Locale.getDefault());
        s.setTimeZone(AppConfig.APP_TIMEZONE);
        return s;
    }
    public static long minutesToMillis(int minutes) {
        return minutes * 60_000L;
    }

    public static String formatHHmm(long epochMillis) {
        return sdf("HH:mm").format(new Date(epochMillis));
    }

    public static String formatDate(long epochMillis) {
        return sdf("dd/MM/yyyy").format(new Date(epochMillis));
    }

    public static String formatDateTime(long epochMillis) {
        return sdf("dd/MM/yyyy HH:mm").format(new Date(epochMillis));
    }

    public static String formatTimeRange(long start, long end) {
        return formatHHmm(start) + " - " + formatHHmm(end);
    }

    public static String formatDateAndTimeRange(long startEpoch, long endEpoch) {
        String date = sdf("dd/MM/yyyy").format(new Date(startEpoch));
        String range = formatHHmm(startEpoch) + "–" + formatHHmm(endEpoch);
        return date + "  " + range;
    }



}
