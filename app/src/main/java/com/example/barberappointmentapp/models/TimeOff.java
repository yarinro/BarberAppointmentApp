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
}
