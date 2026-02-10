package com.example.barberappointmentapp.models;

public class WorkWindow {
    private int dayOfWeek;
    private int startMinute;
    private int endMinute;

    public WorkWindow() {}

    public WorkWindow(int dayOfWeek, int startMinuteOfDay, int endMinuteOfDay) {
        this.dayOfWeek = dayOfWeek;
        this.startMinute = startMinuteOfDay;
        this.endMinute = endMinuteOfDay;
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

    public boolean isValid() {
        return dayOfWeek >= 1
                && dayOfWeek <= 7
                && startMinute >= 0
                && startMinute < endMinute
                && endMinute <= 1440;
    }
}


