package com.example.barberappointmentapp.testings;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
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
                                             @NonNull String serviceId,
                                             @NonNull String serviceName,
                                             @NonNull Callback cb) {

        long now = System.currentTimeMillis();

        Map<String, Object> a1 = makeApMap(clientUid, clientName, clientPhone, serviceId, serviceName,
                now + TimeUnit.HOURS.toMillis(1), 30, false);

        Map<String, Object> a2 = makeApMap(clientUid, clientName, clientPhone, serviceId, serviceName,
                now + TimeUnit.DAYS.toMillis(1) + TimeUnit.HOURS.toMillis(2), 45, false);

        Map<String, Object> a3 = makeApMap(clientUid, clientName, clientPhone, serviceId, serviceName,
                now - TimeUnit.DAYS.toMillis(1) + TimeUnit.HOURS.toMillis(3), 60, false);

        Map<String, Object> a4 = makeApMap(clientUid, clientName, clientPhone, serviceId, serviceName,
                now + TimeUnit.HOURS.toMillis(4), 20, true);

        DatabaseReference ref = apRef();
        ref.push().setValue(a1);
        ref.push().setValue(a2);
        ref.push().setValue(a3);
        ref.push().setValue(a4)
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

        Map<String, Object> ap = makeApMap(clientUid, clientName, clientPhone, serviceId, serviceName,
                start, durationMinutes, cancelled);

        apRef().push().setValue(ap)
                .addOnSuccessListener(unused -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onError(msg(e)));
    }

    /**
     * Keys are aligned to Appointment model:
     * clientUid, clientName, clientPhone, serviceId, serviceName, startEpoch, durationMinutes, cancelled
     */
    private static Map<String, Object> makeApMap(@NonNull String clientUid,
                                                 @NonNull String clientName,
                                                 @NonNull String clientPhone,
                                                 @NonNull String serviceId,
                                                 @NonNull String serviceName,
                                                 long startEpoch,
                                                 int durationMinutes,
                                                 boolean cancelled) {

        Map<String, Object> m = new HashMap<>();
        m.put("clientUid", clientUid);
        m.put("clientName", clientName);
        m.put("clientPhone", clientPhone);
        m.put("serviceId", serviceId);
        m.put("serviceName", serviceName);
        m.put("startEpoch", startEpoch);
        m.put("durationMinutes", durationMinutes);
        m.put("cancelled", cancelled); // ✅ matches model field name
        return m;
    }

    private static String msg(Exception e) {
        return (e == null || e.getMessage() == null) ? "Unknown error" : e.getMessage();
    }
}
