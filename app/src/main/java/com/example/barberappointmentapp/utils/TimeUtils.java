package com.example.barberappointmentapp.utils;

import java.time.LocalDateTime;
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
    // Example output: 202602151437L
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

}
