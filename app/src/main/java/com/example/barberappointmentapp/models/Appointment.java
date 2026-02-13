package com.example.barberappointmentapp.models;

public class Appointment {
    private String id;
    private String clientUid;
    private String clientName;
    private String clientPhone;
    private String serviceName;
    private String serviceId;
    private long startEpoch;
    private int durationMinutes;
    private boolean cancelled;

    public Appointment() {
    }

    public Appointment(String id, String clientUid, String clientName, String clientPhone, String serviceName, String serviceId, long startEpoch, int durationMinutes) {
        this.id = id;
        this.clientUid = clientUid;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.startEpoch = startEpoch;
        this.durationMinutes = durationMinutes;
        this.cancelled = false;
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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public long calcEndEpoch() {
        return startEpoch + durationMinutes * 60_000L;
    }
}
