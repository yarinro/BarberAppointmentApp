package com.example.barberappointmentapp.utils;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class TimeUtils {
    private TimeUtils() {}
    // Input:  202602010800L (long)
    // Output: LocalDateTime object: 2026-02-01 08:00
    public static LocalDateTime toLocalDateTime(long dateNum) {
        String dateString = String.valueOf(dateNum);
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    }

    // Input:  LocalDateTime object: 2026-02-01T08:00
    // Output: 202602010800L (long)
    public static long toLong(LocalDateTime dateTime) {
        String formatted = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return Long.parseLong(formatted);
    }

    // Returns current date-time in format yyyyMMddHHmm as long
    // output: 202602151437L
    public static long now() {
        return toLong(LocalDateTime.now());
    }

    public static String formatDate(long dateNum) {
        return toLocalDateTime(dateNum).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String formatHHmm(long dateNum) {
        return toLocalDateTime(dateNum).format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String formatDateAndTimeRange(long start, long end) {
        return formatDate(start) + "  " + formatHHmm(start) + " - " + formatHHmm(end);
    }

    // formats minute (int) to "HH:mm"
    public static String formatMinuteOfDay(int minuteOfDay) {
        if (minuteOfDay < 0 || minuteOfDay > 1440) throw new IllegalArgumentException("minuteOfDay must be between 0 and 1440");

        int hours = minuteOfDay / 60;
        int minutes = minuteOfDay % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

    // Input:  "09:00"
    // Output: 540
    public static int timeStringToMinutes(@NonNull String time) {
        LocalTime lt = LocalTime.parse(time);
        return lt.getHour() * 60 + lt.getMinute();
    }

    // Input:  540
    // Output: "09:00"
    public static String minutesToTimeString(int minutes) {
        return LocalTime.of(minutes / 60, minutes % 60)
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static long millisecondsToDays(long durationMillis) {
        // 1 day = 24 hours * 60 minutes * 60 seconds * 1000 milliseconds
        return durationMillis / (24L * 60L * 60L * 1000L);
    }

    public static long daysToMilliseconds(int days) {
        // 1 day = 24 hours * 60 minutes * 60 seconds * 1000 milliseconds
        return (long) days * 24L * 60L * 60L * 1000L;
    }



    public static long addMinutesToDate(long dateOnlyLong, int totalMinutes) {
        int hours = totalMinutes / 60;
        int mins = totalMinutes % 60;

        // striping the zeros
        long yyyymmdd = dateOnlyLong;
        if (String.valueOf(dateOnlyLong).length() > 8) {
            yyyymmdd = dateOnlyLong / 10000;
        }

        return (yyyymmdd * 10000) + (hours * 100) + mins;
    }
}
