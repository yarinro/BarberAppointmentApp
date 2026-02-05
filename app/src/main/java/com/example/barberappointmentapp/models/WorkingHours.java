package com.example.barberappointmentapp.models;

public class WorkingHours {
    private int dayOfWeek;
    private int startMinuteOfDay;
    private int endMinuteOfDay;
    private boolean isWorkDay;

    public WorkingHours() {
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartMinuteOfDay() {
        return startMinuteOfDay;
    }

    public void setStartMinuteOfDay(int startMinuteOfDay) {
        this.startMinuteOfDay = startMinuteOfDay;
    }

    public int getEndMinuteOfDay() {
        return endMinuteOfDay;
    }

    public void setEndMinuteOfDay(int endMinuteOfDay) {
        this.endMinuteOfDay = endMinuteOfDay;
    }

    public boolean isWorkDay() {
        return isWorkDay;
    }

    public void setWorkDay(boolean workDay) {
        isWorkDay = workDay;
    }
}
