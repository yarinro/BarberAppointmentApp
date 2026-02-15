package com.example.barberappointmentapp.logic;

import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.Slot;

public final class AppointmentFactory {
    private AppointmentFactory() {}

    // Factory method that creates an Appointment from a Slot
    public static Appointment createFromSlot(String clientUid, String clientName, String clientPhone, Service service, Slot slot) {
        if (clientUid == null || clientUid.trim().isEmpty()) return null;
        if (service == null || slot == null) return null;

        int duration = service.getDurationMinutes();
        if (duration <= 0) return null;

        String serviceId = service.getId();
        if (serviceId == null || serviceId.trim().isEmpty()) return null;

        long start = slot.getStartEpoch();
        if (start <= 0) return null;

        if (clientName == null) clientName = "";
        if (clientPhone == null) clientPhone = "";

        String serviceName = service.getName();
        if (serviceName == null) serviceName = "";

        // UPDATED: deterministic id (no barberId)
        String id = Appointment.generateId(start, clientUid);

        return new Appointment(id, clientUid, clientName, clientPhone, serviceName, serviceId, start);
    }


}
