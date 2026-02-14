package com.example.barberappointmentapp.models;

public class Service {
    private String id;
    private String name;
    private int price;
    private int durationMinutes;
    private boolean isActive;

    public Service() {
    }

    public Service(String id, String name, int price, int durationMinutes, boolean isActive) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.isActive = isActive;
    }

    private static String normalizeName(String name) {
        if (name == null) return "service";
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]", "_").replaceAll("_+", "_");
    }
    // generate deterministic id
    public static String generateId(String name, int durationMinutes) {
        String normalized = normalizeName(name);
        long createdAt = System.currentTimeMillis();
        return "srv_" + normalized + "_" + durationMinutes + "_" + createdAt;
    }
    // Create ID in case of missing field (when pulling data from DB)
    public void ensureId() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = generateId(this.name, this.durationMinutes);
        }
    }

    public static Service create(String name, int price, int durationMinutes, boolean isActive) {
        Service srv = new Service(Service.generateId(name, durationMinutes),name, price, durationMinutes, isActive);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Service invalid: name is required");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Service invalid: price must be >= 0");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Service invalid: durationMinutes must be > 0");
        }
        return srv;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
