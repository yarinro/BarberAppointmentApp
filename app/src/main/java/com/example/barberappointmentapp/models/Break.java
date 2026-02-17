package com.example.barberappointmentapp.models;

import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.firebase.database.Exclude;

import java.time.LocalDateTime;

public class Break {
// Represents a break during a work day.
    private int startMinute;
    private int endMinute;

    public Break() {}

    public Break(int startMinute, int endMinute) {
        if (startMinute < 0 || startMinute > 1440 || endMinute < 0 || endMinute > 1440) throw new IllegalArgumentException("Minutes must be between 0 and 1440");
        if (endMinute <= startMinute) throw new IllegalArgumentException("End minute must be > start minute");

        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }

    // Getters and setters
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

