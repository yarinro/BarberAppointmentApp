package com.example.barberappointmentapp.models;

public class ScheduleSettings {
    public static final int DEFAULT_SLOT_MINUTES = 15;
    public static final int DEFAULT_MAX_DAYS = 30;
    public static final String DEFAULT_TZ = "Asia/Jerusalem";

    private int slotMinutes;
    private int maxDaysAhead;

    public ScheduleSettings() {
        this.slotMinutes = DEFAULT_SLOT_MINUTES;
        this.maxDaysAhead = DEFAULT_MAX_DAYS;
    }

    public ScheduleSettings(int slotMinutes, int maxDaysAhead) {
        this.slotMinutes = slotMinutes;
        this.maxDaysAhead = maxDaysAhead;
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

    public boolean isValid() {
        return slotMinutes >= 5
                && slotMinutes <= 60
                && 60 % slotMinutes == 0
                && maxDaysAhead >= 1
                && maxDaysAhead <= 90;
    }
}
