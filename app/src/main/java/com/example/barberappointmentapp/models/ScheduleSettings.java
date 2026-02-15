package com.example.barberappointmentapp.models;

import java.util.ArrayList;
import java.util.Map;

public class ScheduleSettings {
    private Map<String, ArrayList<WorkingHours>> workingHours; // weekly working hours keys = "1"-"7"
    private Map<String, ArrayList<Break>> breaks; // weekly breaks - keys: "1"-"7"
    // https://firebase.google.com/docs/database/android/read-and-write?#updating_or_deleting_data
    private ArrayList<TimeOff> timeOffs; // time offs are one time only. they are not recurring -> hence use a list

    public ScheduleSettings() {}

    public ScheduleSettings(Map<String, ArrayList<WorkingHours>> workingHours, Map<String, ArrayList<Break>> breaks, ArrayList<TimeOff> timeOffs) {
        this.workingHours = workingHours;
        this.breaks = breaks;
        this.timeOffs = timeOffs;
    }
}
