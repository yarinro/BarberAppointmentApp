package com.example.barberappointmentapp.models;

import com.example.barberappointmentapp.utils.TimeUtils;

import java.time.LocalDateTime;

public class TimeOff {
    private String id;
    private long startDateTime;
    private long endDateTime;
    private String reason;

    public TimeOff() {}

    public TimeOff(String id, long startDateTime, long endDateTime, String reason) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    // Getters and setters with LocalDateTime objects
    public void setStartDateTimeObj(long startDateTime) {this.startDateTime = startDateTime;}
    public void setEndDateTimeObj(long endDateTime) {this.endDateTime = endDateTime;}
    public LocalDateTime getStartDateTimeObj() {return TimeUtils.toLocalDateTime(startDateTime);}
    public LocalDateTime getEndDateTimeObj() {return TimeUtils.toLocalDateTime(endDateTime);}

    // Generates a unique ID based on the start and end date-times
    private static String generateId(long startDateTime, long endDateTime) {
        return "to_" + startDateTime + "_" + endDateTime;
    }

    // Creates a new TimeOff object with the given parameters
    public static TimeOff create(long startDateTime, long endDateTime, String reason) {
        if (startDateTime >= endDateTime) {
            throw new IllegalArgumentException("TimeOff invalid: endDateTime must be greater than startDateTime");
        }
        reason = (reason == null) ? "" : reason.trim();
        TimeOff timeOff = new TimeOff(TimeOff.generateId(startDateTime, endDateTime), startDateTime, endDateTime, reason);

        return timeOff;
    }
    // gets the duration of the time off in minutes
    public int getDurationMinutes() {
        LocalDateTime start = TimeUtils.toLocalDateTime(startDateTime);
        LocalDateTime end = TimeUtils.toLocalDateTime(endDateTime);
        return (int) java.time.Duration.between(start, end).toMinutes();
    }
    // checks if the time off is in the future
    public boolean isFuture() {
        return startDateTime > TimeUtils.now();
    }
    // checks if the time off has passed
    public boolean isPast() {
        return endDateTime <= TimeUtils.now();
    }



}
