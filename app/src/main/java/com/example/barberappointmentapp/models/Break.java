package com.example.barberappointmentapp.models;

public class Break {
// Represents a break of time during a work day.
    private String id;
    private int dayOfWeek;   // 1-7
    private int startMinute;
    private int endMinute;

    public Break() {}

    public Break(String id, int dayOfWeek, int startMinute, int endMinute) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }

    // deterministic id
    public static String generateId(int dayOfWeek, int startMinute, int endMinute) {
        return "br_" + dayOfWeek + "_" + startMinute + "_" + endMinute;
    }
    public void ensureId() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = generateId(this.dayOfWeek, this.startMinute, this.endMinute);
        }
    }
    public boolean isValid() {
        return dayOfWeek >= 1 && dayOfWeek <= 7 && startMinute >= 0 && startMinute < endMinute && endMinute <= 1440;
    }

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
}

