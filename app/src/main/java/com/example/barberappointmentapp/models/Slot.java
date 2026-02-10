package com.example.barberappointmentapp.models;

public class Slot {
    private final long startEpoch;
    private final long endEpoch;

    public Slot(long startEpoch, long endEpoch) {
        this.startEpoch = startEpoch;
        this.endEpoch = endEpoch;
    }

    public long getStartEpoch() {
        return startEpoch;
    }

    public long getEndEpoch() {
        return endEpoch;
    }

    public long getDurationMillis() { return endEpoch - startEpoch; }

}
