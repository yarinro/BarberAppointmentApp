package com.example.barberappointmentapp.models;

public class ScheduleSettings {
    public static final int DEFAULT_SLOT_MINUTES = 15;
    public static final int DEFAULT_MAX_DAYS = 30;
    public static final String DEFAULT_TZ = "Asia/Jerusalem";

    private int slotMinutes;
    private int maxDaysAhead;
    private String timezone;

    public ScheduleSettings() {
        this.slotMinutes = DEFAULT_SLOT_MINUTES;
        this.maxDaysAhead = DEFAULT_MAX_DAYS;
        this.timezone = DEFAULT_TZ;
    }

    public ScheduleSettings(int slotMinutes, int maxDaysAhead, String timezone) {
        this.slotMinutes = slotMinutes;
        this.maxDaysAhead = maxDaysAhead;
        this.timezone = timezone;
    }

    public int getSlotMinutes() {
        return slotMinutes;
    }
    public void setSlotMinutes(int slotMinutes) {
        this.slotMinutes = slotMinutes;
    }
    public int getMaxDaysAhead() {
        return maxDaysAhead;
    }
    public void setMaxDaysAhead(int maxDaysAhead) {
        this.maxDaysAhead = maxDaysAhead;
    }
    public String getTimezone() {
        return timezone;
    }
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isValid() {
        return slotMinutes >= 5
                && slotMinutes <= 60
                && 60 % slotMinutes == 0
                && maxDaysAhead >= 1
                && maxDaysAhead <= 90
                && timezone != null
                && !timezone.isBlank();
    }
}
