package com.example.barberappointmentapp.models;

import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.firebase.database.Exclude;

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
    @Exclude
    public void setStartDateTimeObj(LocalDateTime dateTime) {this.startDateTime = TimeUtils.toLong(dateTime);}
    @Exclude
    public void setEndDateTimeObj(LocalDateTime dateTime) {this.endDateTime = TimeUtils.toLong(dateTime);}
    @Exclude
    public LocalDateTime getStartDateTimeObj() {return TimeUtils.toLocalDateTime(startDateTime);}
    @Exclude
    public LocalDateTime getEndDateTimeObj() {return TimeUtils.toLocalDateTime(endDateTime);}


    @Exclude
    // Generates a unique ID based on the start and end date-times
    private static String generateId(long startDateTime, long endDateTime) {
        return "to_" + startDateTime + "_" + endDateTime;
    }
    @Exclude
    // Creates a new TimeOff object with the given parameters
    public static TimeOff create(long startDateTime, long endDateTime, String reason) {
        if (startDateTime >= endDateTime) throw new IllegalArgumentException("TimeOff invalid: endDateTime must be greater than startDateTime");

        String processedReason = (reason == null) ? "" : reason.trim();

        TimeOff timeOff = new TimeOff(TimeOff.generateId(startDateTime, endDateTime), startDateTime, endDateTime, processedReason);

        return timeOff;
    }
    @Exclude
    // gets the duration of the time off in minutes
    public int getDurationMinutes() {
        LocalDateTime start = TimeUtils.toLocalDateTime(startDateTime);
        LocalDateTime end = TimeUtils.toLocalDateTime(endDateTime);
        return (int) java.time.Duration.between(start, end).toMinutes();
    }
    @Exclude
    // checks if the time off is in the future
    public boolean isFuture() {
        return startDateTime > TimeUtils.now();
    }
    @Exclude
    // checks if the time off has passed
    public boolean isPast() {
        return endDateTime <= TimeUtils.now();
    }

    @Exclude
    // Returns true if the timeoff is happening right now
    public boolean isHappeningNow() {
        long now = TimeUtils.now();
        return startDateTime <= now && now < endDateTime;
    }
}
