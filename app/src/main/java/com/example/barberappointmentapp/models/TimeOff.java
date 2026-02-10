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

    public boolean isValid() {
        return endEpoch > startEpoch;
    }
}
