package com.example.barberappointmentapp.models;

import androidx.annotation.NonNull;

import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;

public class WorkingDay {
    private int dayOfWeek; // 1-7 . this will be the key in firebase
    private boolean workDay; // is work day
    private int startMinute;
    private int endMinute;
    private ArrayList<Break> breaks = new ArrayList<>(); // List of breaks through the day

    public WorkingDay() {}

    public WorkingDay(int dayOfWeek, boolean workDay, int startMinute, int endMinute, ArrayList<Break> breaks) {
        // Validation
        if (dayOfWeek < 1 || dayOfWeek > 7) throw new IllegalArgumentException("Day of week must be between 1-7");
        if (startMinute < 0 || startMinute > 1440) throw new IllegalArgumentException("Start minute must be between 0-1440");
        if (endMinute <= startMinute) throw new IllegalArgumentException("End minute must be > start minute");

        ArrayList<Break> validatedBreaks = breaks == null ? new ArrayList<>() : breaks;;

        this.dayOfWeek = dayOfWeek;
        this.workDay = workDay;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
        this.breaks = validatedBreaks;
    }

    // Getters and setters
    public int getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    public boolean getWorkDay() {
        return workDay;
    }
    public void setWorkDay(boolean workDay) {
        this.workDay = workDay;
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
    public ArrayList<Break> getBreaks() {
        return breaks;
    }
    public void setBreaks(ArrayList<Break> breaks) {
        this.breaks = breaks;
    }

    @Exclude
    private String getDayName(int day) {
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[day];
    }

    // returns the working hours of a day in a string
    @Exclude
    @Override
    public String toString() {
        if (!workDay) return getDayName(dayOfWeek) + ": Closed";

        String output = getDayName(dayOfWeek) + ": ";

        if (breaks == null || breaks.isEmpty())
            return getDayName(dayOfWeek) + ": " + TimeUtils.formatMinuteOfDay(startMinute) + "-" + TimeUtils.formatMinuteOfDay(endMinute);

        ArrayList<Break> sortedBreaks = new ArrayList<>(breaks);
        sortedBreaks.sort((b1, b2) -> Integer.compare(b1.getStartMinute(), b2.getStartMinute()));

        int currStart = startMinute;
        for (Break br : sortedBreaks){
            output = output + TimeUtils.formatMinuteOfDay(currStart) + "-" + TimeUtils.formatMinuteOfDay(br.getStartMinute()) + " , ";
            currStart = br.getEndMinute();
        }
        output =  output  + TimeUtils.formatMinuteOfDay(currStart) + "-" + TimeUtils.formatMinuteOfDay(endMinute);

        return output;
    }
}
