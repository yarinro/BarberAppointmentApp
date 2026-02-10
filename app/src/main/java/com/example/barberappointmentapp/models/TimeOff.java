package com.example.barberappointmentapp.models;

public class TimeOff {
    private String id;
    private long startEpoch;
    private long endEpoch;
    private String reason;

    public TimeOff() {}

    public TimeOff(String id, long startEpoch, long endEpoch, String reason) {
        this.id = id;
        this.startEpoch = startEpoch;
        this.endEpoch = endEpoch;
        this.reason = reason;
    }

    public boolean isValid() {
        return endEpoch > startEpoch;
    }
}
