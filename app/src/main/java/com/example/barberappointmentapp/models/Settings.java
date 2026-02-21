package com.example.barberappointmentapp.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Map;

public class Settings {
    private String barbershopName;
    private String address;
    private String phoneNumber;
    private String aboutUs; // A short bio or shop description
    private int maxDaysAheadToBookAppointment;
    private Map<String, WorkingDay> workingDays; // key: day of week, value: working day object
    private Map<String, TimeOff> timeOffs; // list of time offs (no recurrent time offs- one time only)
    private Map<String, Service> services; // key: service id, value: service object
    public Settings() {}

    public Settings(String barbershopName, String address, String phoneNumber, String aboutUs, int maxDaysAheadToBookAppointment, Map<String, WorkingDay> workingDays, Map<String, TimeOff> timeOffs, Map<String, Service> services) {
        this.barbershopName = barbershopName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.aboutUs = aboutUs;
        this.maxDaysAheadToBookAppointment = maxDaysAheadToBookAppointment;
        this.workingDays = workingDays;
        this.timeOffs = timeOffs;
        this.services = services;
    }

    public String getBarbershopName() {
        return barbershopName;
    }

    public void setBarbershopName(String barbershopName) {
        this.barbershopName = barbershopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public int getMaxDaysAheadToBookAppointment() {
        return maxDaysAheadToBookAppointment;
    }

    public void setMaxDaysAheadToBookAppointment(int maxDaysAheadToBookAppointment) {
        this.maxDaysAheadToBookAppointment = maxDaysAheadToBookAppointment;
    }

    public Map<String, WorkingDay> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Map<String, WorkingDay> workingDays) {
        this.workingDays = workingDays;
    }

    public Map<String, TimeOff> getTimeOffs() {
        return timeOffs;
    }

    public void setTimeOffs(Map<String, TimeOff> timeOffs) {
        this.timeOffs = timeOffs;
    }

    public Map<String, Service> getServices() {
        return services;
    }

    public void setServices(Map<String, Service> services) {
        this.services = services;
    }

    @Exclude
    public ArrayList<Service> getServicesAsList() {
        if (services == null) return new ArrayList<>();

        return new ArrayList<>(services.values());
    }

    @Exclude
    public ArrayList<TimeOff> getTimeOffsAsList() {
        if (timeOffs == null) return new ArrayList<>();
        return new ArrayList<>(timeOffs.values());
    }
}
