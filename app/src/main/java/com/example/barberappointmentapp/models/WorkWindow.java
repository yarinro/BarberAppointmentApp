package com.example.barberappointmentapp.models;

public class WorkWindow {
    private String id;
    private int dayOfWeek;
    private int startMinute;
    private int endMinute;

    public WorkWindow() {}

    public WorkWindow(String id, int dayOfWeek, int startMinuteOfDay, int endMinuteOfDay) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startMinute = startMinuteOfDay;
        this.endMinute = endMinuteOfDay;
    }

    // deterministic id based on actual fields
    public static String generateId(int dayOfWeek, int startMinute, int endMinute) {
        return "ww_" + dayOfWeek + "_" + startMinute + "_" + endMinute;
    }
    // Create ID in case of missing field (when pulling data from DB)
    public void ensureId() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = generateId(this.dayOfWeek, this.startMinute, this.endMinute);
        }
    }

    public static WorkWindow create(int dayOfWeek, int startMinute, int endMinute) {
        WorkWindow ww = new WorkWindow(WorkWindow.generateId(dayOfWeek, startMinute, endMinute), dayOfWeek, startMinute, endMinute);
        if (!ww.isValid()) {
            throw new IllegalArgumentException("WorkWindow invalid: dayOfWeek 1-7, minutes 0-1440, start < end");
        }
        return ww;
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

    public boolean isValid() {
        return dayOfWeek >= 1
                && dayOfWeek <= 7
                && startMinute >= 0
                && startMinute < endMinute
                && endMinute <= 1440;
    }
}


