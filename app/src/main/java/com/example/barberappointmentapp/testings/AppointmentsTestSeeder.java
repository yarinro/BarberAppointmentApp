package com.example.barberappointmentapp.testings;

import androidx.annotation.NonNull;

import com.example.barberappointmentapp.logic.AppointmentFactory;
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.Slot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public final class AppointmentsTestSeeder {

    private AppointmentsTestSeeder() {}

    public interface Callback {
        void onSuccess();
        void onError(@NonNull String message);
    }

    private static DatabaseReference apRef() {
        return FirebaseDatabase.getInstance().getReference("appointments");
    }

    public static void clearAll(@NonNull Callback cb) {
        apRef().removeValue()
                .addOnSuccessListener(unused -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onError(msg(e)));
    }

    public static void seedBasicSetForClient(@NonNull String clientUid,
                                             @NonNull String clientName,
                                             @NonNull String clientPhone,
                                             @NonNull String baseServiceId,
                                             @NonNull String baseServiceName,
                                             @NonNull Callback cb) {

        long now = System.currentTimeMillis();

        Appointment a1 = makeAppointmentViaFactory(clientUid, clientName, clientPhone,
                baseServiceId, baseServiceName,
                now + TimeUnit.HOURS.toMillis(1), 30, false);

        Appointment a2 = makeAppointmentViaFactory(clientUid, clientName, clientPhone,
                baseServiceId, baseServiceName,
                now + TimeUnit.DAYS.toMillis(1) + TimeUnit.HOURS.toMillis(2), 45, false);

        Appointment a3 = makeAppointmentViaFactory(clientUid, clientName, clientPhone,
                baseServiceId, baseServiceName,
                now - TimeUnit.DAYS.toMillis(1) + TimeUnit.HOURS.toMillis(3), 60, false);

        Appointment a4 = makeAppointmentViaFactory(clientUid, clientName, clientPhone,
                baseServiceId, baseServiceName,
                now + TimeUnit.HOURS.toMillis(4), 20, true);

        if (a1 == null || a2 == null || a3 == null || a4 == null) {
            cb.onError("Seeder failed: AppointmentFactory returned null (check inputs / factory validation)");
            return;
        }

        DatabaseReference ref = apRef();

        ref.child(a1.getId()).setValue(a1);
        ref.child(a2.getId()).setValue(a2);
        ref.child(a3.getId()).setValue(a3);
        ref.child(a4.getId()).setValue(a4)
                .addOnSuccessListener(unused -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onError(msg(e)));
    }

    public static void addOneInMinutes(@NonNull String clientUid,
                                       @NonNull String clientName,
                                       @NonNull String clientPhone,
                                       int startInMinutes,
                                       int durationMinutes,
                                       @NonNull String serviceId,
                                       @NonNull String serviceName,
                                       boolean cancelled,
                                       @NonNull Callback cb) {

        long now = System.currentTimeMillis();
        long start = now + TimeUnit.MINUTES.toMillis(startInMinutes);

        Appointment ap = makeAppointmentViaFactory(
                clientUid, clientName, clientPhone,
                serviceId, serviceName,
                start, durationMinutes, cancelled
        );

        if (ap == null) {
            cb.onError("Seeder failed: AppointmentFactory returned null (check inputs / factory validation)");
            return;
        }

        apRef().child(ap.getId()).setValue(ap)
                .addOnSuccessListener(unused -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onError(msg(e)));
    }

    /**
     * Builds Service + Slot, then uses AppointmentFactory.createFromSlot(...)
     */
    private static Appointment makeAppointmentViaFactory(@NonNull String clientUid,
                                                         @NonNull String clientName,
                                                         @NonNull String clientPhone,
                                                         @NonNull String serviceId,
                                                         @NonNull String serviceName,
                                                         long startEpoch,
                                                         int durationMinutes,
                                                         boolean cancelled) {

        // create a Service instance that matches the desired duration
        Service service = new Service(serviceId, serviceName, 0, durationMinutes, true);

        long endEpoch = startEpoch + TimeUnit.MINUTES.toMillis(durationMinutes);
        Slot slot = new Slot(startEpoch, endEpoch);

        Appointment ap = AppointmentFactory.createFromSlot(clientUid, clientName, clientPhone, service, slot);
        if (ap == null) return null;

        ap.setCancelled(cancelled);
        return ap;
    }

    private static String msg(Exception e) {
        return (e == null || e.getMessage() == null) ? "Unknown error" : e.getMessage();
    }
}
