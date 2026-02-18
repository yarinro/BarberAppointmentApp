package com.example.barberappointmentapp.models;

import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.firebase.database.Exclude;

public class Break {
// Represents a break during a work day.
    private String id;
    private int dayOfWeek; // 1-7
    private int startMinute;
    private int endMinute;

    public Break() {}

    public Break(String id,  int dayOfWeek, int startMinute, int endMinute) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }
    @Exclude
    public static String generateId(int dayOfWeek, int startMinute, int endMinute) {
        return "brk_" + dayOfWeek + "_" + startMinute + "_" + endMinute;
    }
    @Exclude
    public static Break create(int dayOfWeek, int startMinute, int endMinute) {
        if (startMinute < 0 || startMinute > 1440 || endMinute < 0 || endMinute > 1440) throw new IllegalArgumentException("Minutes must be between 0 and 1440");
        if (endMinute <= startMinute) throw new IllegalArgumentException("End minute must be > start minute");

        return new Break(generateId(dayOfWeek, startMinute, endMinute),dayOfWeek, startMinute, endMinute);
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartMinute() {
        return startMinute;
    }
    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }
    public int getEndMinute() {
        return endMinute;
    }
    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    @Exclude
    public String getStringDayOfWeek() {
        return String.valueOf(dayOfWeek);
    }

    @Exclude
    // Calculate the duration of appointment in minutes
    public int getDurationMinutes() {
        return endMinute - startMinute;
    }

    @Exclude
    @Override
    public String toString() {
        return TimeUtils.formatMinuteOfDay(startMinute) + "-" + TimeUtils.formatMinuteOfDay(endMinute);
    }
}

