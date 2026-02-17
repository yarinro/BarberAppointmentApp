package com.example.barberappointmentapp.models;
import com.google.firebase.database.Exclude;
import com.example.barberappointmentapp.utils.TimeUtils;

import java.time.LocalDateTime;

public class Appointment {
    private String id;
    private String clientUid;
    private String clientName;
    private String clientPhone;
    private String serviceName;
    private String serviceId;
    private long startDateTime;
    private long endDateTime;
    private boolean cancelled;

    public Appointment() {
    }

    public Appointment(String id, String clientUid, String clientName, String clientPhone, String serviceName, String serviceId, long startDateTime, long startEndTime, boolean cancelled) {
        this.id = id;
        this.clientUid = clientUid;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.startDateTime = startDateTime;
        this.endDateTime = startEndTime;
        this.cancelled = cancelled;
    }

    @Exclude
    //generate unique ID
    public static String generateId(String clientUid, long startDateTime, long endDateTime) {
        return "ap_" + clientUid + "_" + startDateTime + "_" + endDateTime;
    }
    @Exclude
    // Create a new appointment
    public static Appointment create(String clientUid, String clientName, String clientPhone, Service service, long startDateTime, long endDateTime) {
        // Validation
        if (clientUid == null || clientUid.isEmpty()) throw new IllegalArgumentException("clientUid required");
        if (service == null) throw new IllegalArgumentException("service required");
        if (startDateTime >= endDateTime) throw new IllegalArgumentException("startDateTime must be before endDateTime");
        if (clientName == null) throw new IllegalArgumentException("clientName must be a non-empty string");
        if (clientPhone == null) throw new IllegalArgumentException("Client phone must be a non-empty string");
        // generating unique ID
        String id = generateId(clientUid, startDateTime, endDateTime);
        return new Appointment(id, clientUid, clientName, clientPhone, service.getName(), service.getId(), startDateTime, endDateTime, false);
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
    public long getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }
    public long getEndDateTime() {
        return endDateTime;
    }
    public void setEndDateTime(long endDateTime) {
        this.endDateTime = endDateTime;
    }
    public boolean getCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    // Getters and setters with LocalDateTime objects
    @Exclude
    public LocalDateTime getStartDateTimeObj() {
        return TimeUtils.toLocalDateTime(startDateTime);
    }
    @Exclude
    public void setStartDateTimeObj(LocalDateTime dateTime) {this.startDateTime = TimeUtils.toLong(dateTime);}
    @Exclude
    public LocalDateTime getEndDateTimeObj() {return TimeUtils.toLocalDateTime(endDateTime);}
    @Exclude
    public void setEndDateTimeObj(LocalDateTime dateTime) {this.endDateTime = TimeUtils.toLong(dateTime);}

    @Exclude
    // Calculate the duration of appointment in minutes
    public int getDurationMinutes() {
        LocalDateTime start = TimeUtils.toLocalDateTime(startDateTime);
        LocalDateTime end = TimeUtils.toLocalDateTime(endDateTime);
        return (int) java.time.Duration.between(start, end).toMinutes();
    }
    @Exclude
    // Returns true if the appointment is in the past
    public boolean isPast() {
        return endDateTime <= TimeUtils.now();
    }
    @Exclude
    // Returns true if the appointment is in the future
    public boolean isFuture() {
        return startDateTime > TimeUtils.now();
    }
    @Exclude
    // Returns true if the appointment is happening right now
    public boolean isHappeningNow() {
        long now = TimeUtils.now();
        return startDateTime <= now && now < endDateTime;
    }
    @Exclude
    // Returns true if the appointment can be cancelled - not cancelled and in the future
    public boolean canBeCancelled() {
        return !cancelled && isFuture();
    }
}

