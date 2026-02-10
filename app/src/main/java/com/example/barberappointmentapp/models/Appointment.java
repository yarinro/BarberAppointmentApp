package com.example.barberappointmentapp.models;

public class Appointment {
    private String id;
    private String clientUid;
    private String serviceId;
    private long startEpoch;
    private int durationMinutes;
    private boolean isCancelled;

    public Appointment() {
    }

    public Appointment(String id, String clientUid, String serviceId, long startEpoch, int durationMinutes) {
        this.id = id;
        this.clientUid = clientUid;
        this.serviceId = serviceId;
        this.startEpoch = startEpoch;
        this.durationMinutes = durationMinutes;
        this.isCancelled = false;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getClientUid() {
        return clientUid;
    }
    public void setClientUid(String clientUid) {
        this.clientUid = clientUid;
    }
    public String getServiceId() {
        return serviceId;
    }
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    public long getStartEpoch() {
        return startEpoch;
    }
    public void setStartEpoch(long startEpoch) {
        this.startEpoch = startEpoch;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public boolean isCancelled() {
        return isCancelled;
    }
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public long calcEndEpoch() {
        return startEpoch + durationMinutes * 60_000L;
    }
}
