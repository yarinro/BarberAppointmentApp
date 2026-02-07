package com.example.barberappointmentapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {
    private TimeUtils() {}

    public static long minutesToMillis(int minutes) {
        return minutes * 60_000L;
    }

    public static String formatHHmm(long epochMillis) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(epochMillis));
    }

}
