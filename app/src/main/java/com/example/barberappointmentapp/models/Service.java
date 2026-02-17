package com.example.barberappointmentapp.models;

import com.google.firebase.database.Exclude;

public class Service {
    private String id;
    private String name;
    private int price;
    private int durationMinutes;
    private boolean active;

    public Service() {
    }

    public Service(String id, String name, int price, int durationMinutes, boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.active = active;
    }

    @Exclude
    // IMPORTANT: there should be no option for barber to edit service name - only add/delete
    // generates id for the service
    public static String generateId(String name, int durationMinutes) {
        String processedName;
        processedName = name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_"); // trims, lowercases, and replaces non-alphanumeric characters with underscores
        return "srv_" + processedName + "_" + durationMinutes;
    }

    @Exclude
    // creates a new service with validation and generates unique ID
    public static Service create(String name, int price, int durationMinutes, boolean isActive) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Service invalid: name should be non-empty string");
        if (price < 0) throw new IllegalArgumentException("Service invalid: price must be >= 0");
        if (durationMinutes <= 0) throw new IllegalArgumentException("Service invalid: durationMinutes must be > 0");
        // generating unique ID
        String id = generateId(name, durationMinutes);
        return new Service(id, name, price, durationMinutes, isActive);
    }

    // getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public boolean getActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    // A string that represents how the service should be displayed in the UI
    @Exclude
    @Override
    public String toString() {
        return name + " (" + durationMinutes + " min) - " + price + "₪";
    }
}
