package com.example.barberappointmentapp.logic;

import androidx.annotation.NonNull;

import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.Slot;

public final class AppointmentFactory {
    private AppointmentFactory() {}

    // Factory method that creates an Appointment from a Slot
    public static Appointment createFromSlot(String clientUid, Service service, Slot slot) {
        if (clientUid == null || clientUid.trim().isEmpty()) return null;
        if (service == null || slot == null) return null;

        int duration = service.getDurationMinutes();
        if (duration <= 0) return null;

        String serviceId = service.getId();
        if (serviceId == null || serviceId.trim().isEmpty()) return null;

        long start = slot.getStartEpoch();
        if (start <= 0) return null;

        // IMPORTANT: do not forget to get the id from Realtime DB when pulling data
        return new Appointment(null, clientUid, serviceId, start, duration);
    }
}
