package com.example.barberappointmentapp.models;

public class TimeOff {
    private String id;
    private long startEpoch;
    private long endEpoch;
    private String reason;
    private String note;
    private long createdAtEpoch;
    /// ////////////////////////////////////////////
    private boolean isRecurring;
    // The following three are relevant if isRecurring=true
    private int dayOfWeek; // 1–7
    private int startMinuteOfDay;
    private int endMinuteOfDay;

    public TimeOff() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(long startEpoch) {
        this.startEpoch = startEpoch;
    }

    public long getEndEpoch() {
        return endEpoch;
    }

    public void setEndEpoch(long endEpoch) {
        this.endEpoch = endEpoch;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCreatedAtEpoch() {
        return createdAtEpoch;
    }

    public void setCreatedAtEpoch(long createdAtEpoch) {
        this.createdAtEpoch = createdAtEpoch;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
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
}
