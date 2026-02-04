package com.example.barberappointmentapp.models;

public class Appointment {
    private String id;
    private String clientUid;
    private String serviceId;
    private AppointmentStatus status;
    private long startEpoch;
    private int durationMinutes;
    private long createdAtEpoch;

}
