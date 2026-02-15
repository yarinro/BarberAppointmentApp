package com.example.barberappointmentapp.models;

public class WorkingHours {
    private String id;
    private int dayOfWeek;
    private int startMinute;
    private int endMinute;

    public WorkingHours() {}

    public WorkingHours(String id, int dayOfWeek, int startMinuteOfDay, int endMinuteOfDay) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startMinute = startMinuteOfDay;
        this.endMinute = endMinuteOfDay;
    }

    public String getId() {return id; }
    public void setId(String id) {this.id = id; }
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

    // Generates a unique ID for a WorkWindow
    public static String generateId(int dayOfWeek, int startMinute, int endMinute) {
        return "ww_" + dayOfWeek + "_" + startMinute + "_" + endMinute;
    }

    // Creates a WorkingHours object with the given parameters
    public static WorkingHours create(int dayOfWeek, int startMinute, int endMinute) {
        WorkingHours ww = new WorkingHours(WorkingHours.generateId(dayOfWeek, startMinute, endMinute), dayOfWeek, startMinute, endMinute);
        if (!ww.isValid()) {
            throw new IllegalArgumentException("WorkWindow invalid: dayOfWeek 1-7, minutes 0-1440, start < end");
        }
        return ww;
    }
    public boolean isValid() {
        return dayOfWeek >= 1 && dayOfWeek <= 7 && startMinute >= 0 && startMinute < endMinute && endMinute <= 1440;
    }
    //
    public int getDurationMinutes() {
        return endMinute - startMinute;
    }
}


