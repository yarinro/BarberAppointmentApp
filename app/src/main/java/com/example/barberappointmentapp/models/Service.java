package com.example.barberappointmentapp.models;

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
    //
    // IMPORTANT: there should be no option for barber to edit service name
    private static String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) return "invalid";
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_"); // trims, lowercases, and replaces non-alphanumeric characters with underscores
    }
    // generates id for the service
    public static String generateId(String name, int durationMinutes) {
        return "srv_" + normalizeName(name) + "_" + durationMinutes;
    }

    // creates a new service
    public static Service create(String name, int price, int durationMinutes, boolean isActive) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Service invalid: name should be non-empty string");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Service invalid: price must be >= 0");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Service invalid: durationMinutes must be > 0");
        }
        String id = generateId(name, durationMinutes);
        return new Service(id, name, price, durationMinutes, isActive);
    }

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
    public String formatDisplayName() {
        return name + " (" + durationMinutes + " min)";
    }
}
