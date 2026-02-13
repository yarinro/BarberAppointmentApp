package com.example.barberappointmentapp.testings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DebugFirebaseFragment extends Fragment {

    private static final String TAG = "APPOINTMENTS_DEBUG";

    private static final String TEST_EMAIL = "test_client_1@debug.com";
    private static final String TEST_PASS  = "123456";

    private static final String TEST_CLIENT_NAME  = "TEST USER";
    private static final String TEST_CLIENT_PHONE = "0500000000";

    private static final String TEST_SERVICE_ID   = "TEST_SERVICE";
    private static final String TEST_SERVICE_NAME = "Test Service";

    private TextView tvStatus;
    private FirebaseAuth mAuth;

    private DatabaseReference usersRef() { return FirebaseDatabase.getInstance().getReference("users"); }
    private DatabaseReference apRef() { return FirebaseDatabase.getInstance().getReference("appointments"); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_debug_firebase, container, false);

        mAuth = FirebaseAuth.getInstance();
        tvStatus = view.findViewById(R.id.tv_status);

        Button btnEnsure = view.findViewById(R.id.btn_ensure_test_user);
        Button btnSeed = view.findViewById(R.id.btn_seed_for_current);
        Button btnPrint = view.findViewById(R.id.btn_print);
        Button btnClear = view.findViewById(R.id.btn_clear_appointments);
        Button btnOpenClient = view.findViewById(R.id.btn_open_client_list);
        Button btnOpenBarber = view.findViewById(R.id.btn_open_barber_list);

        btnEnsure.setOnClickListener(v -> ensureTestUser());
        btnSeed.setOnClickListener(v -> seedAppointmentsForCurrentUser());
        btnPrint.setOnClickListener(v -> printAllAppointments());
        btnClear.setOnClickListener(v -> clearAllAppointments());

        btnOpenClient.setOnClickListener(v -> {
            try { NavHostFragment.findNavController(this).navigate(R.id.clientMyAppointmentsFragment); }
            catch (Exception e) { toast("Nav error (client): " + e.getMessage()); }
        });

        btnOpenBarber.setOnClickListener(v -> {
            try { NavHostFragment.findNavController(this).navigate(R.id.barberAppointmentsFragment); }
            catch (Exception e) { toast("Nav error (barber): " + e.getMessage()); }
        });

        updateStatusWithAuth();
        return view;
    }

    private void ensureTestUser() {
        setStatus("Ensuring TEST user...");

        mAuth.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASS)
                .addOnSuccessListener(res -> {
                    FirebaseUser u = mAuth.getCurrentUser();
                    if (u == null) { setStatus("Login OK but user=null"); return; }
                    setStatus("Logged in as: " + u.getUid());
                    ensureUserNodeExists(u);
                })
                .addOnFailureListener(e -> {
                    setStatus("Login failed -> creating user...");
                    mAuth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASS)
                            .addOnSuccessListener(res -> {
                                FirebaseUser u = mAuth.getCurrentUser();
                                if (u == null) { setStatus("Create OK but user=null"); return; }
                                setStatus("Created & logged in as: " + u.getUid());
                                ensureUserNodeExists(u);
                            })
                            .addOnFailureListener(e2 -> {
                                setStatus("Create failed: " + msg(e2));
                                toast("Create failed: " + msg(e2));
                            });
                });
    }

    private void ensureUserNodeExists(@NonNull FirebaseUser u) {
        String uid = u.getUid();
        User newUser = new User(uid, TEST_CLIENT_NAME, TEST_EMAIL, TEST_CLIENT_PHONE, System.currentTimeMillis());

        usersRef().child(uid).setValue(newUser)
                .addOnSuccessListener(unused -> { setStatus("User node written: /users/" + uid); toast("TEST user ready"); })
                .addOnFailureListener(e -> { setStatus("User node write failed: " + msg(e)); toast("DB write failed: " + msg(e)); });
    }

    private void seedAppointmentsForCurrentUser() {
        FirebaseUser u = mAuth.getCurrentUser();
        if (u == null) { toast("No auth user. Click 'Ensure TEST user' first."); setStatus("No current user"); return; }

        String uid = u.getUid();
        setStatus("Seeding appointments for uid=" + uid);

        AppointmentsTestSeeder.seedBasicSetForClient(
                uid,
                TEST_CLIENT_NAME,
                TEST_CLIENT_PHONE,
                TEST_SERVICE_ID,
                TEST_SERVICE_NAME,
                new AppointmentsTestSeeder.Callback() {
                    @Override public void onSuccess() { setStatus("Seed done (4). Open Client/Barber list."); toast("Seed done"); }
                    @Override public void onError(@NonNull String message) { setStatus("Seed failed: " + message); toast("Seed failed: " + message); }
                }
        );
    }

    private void printAllAppointments() {
        setStatus("Printing to Logcat...");
        Log.d(TAG, "printAllAppointments() ...");

        apRef().orderByChild("startEpoch")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "Total children = " + snapshot.getChildrenCount());
                        int i = 0;

                        for (DataSnapshot s : snapshot.getChildren()) {
                            String key = s.getKey();

                            Object clientUid = s.child("clientUid").getValue();
                            Object serviceId = s.child("serviceId").getValue();
                            Object start = s.child("startEpoch").getValue();
                            Object dur = s.child("durationMinutes").getValue();

                            // ✅ aligned to model/map: "cancelled"
                            Object cancelled = s.child("cancelled").getValue();

                            Log.d(TAG, "#" + i + " key=" + key
                                    + " clientUid=" + clientUid
                                    + " serviceId=" + serviceId
                                    + " startEpoch=" + start
                                    + " durationMinutes=" + dur
                                    + " cancelled=" + cancelled);
                            i++;
                        }

                        setStatus("Printed. Search Logcat: " + TAG);
                        toast("Printed to Logcat");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        setStatus("Print cancelled: " + error.getMessage());
                        toast("Print cancelled: " + error.getMessage());
                    }
                });
    }

    private void clearAllAppointments() {
        setStatus("Clearing /appointments ...");
        apRef().removeValue()
                .addOnSuccessListener(unused -> { setStatus("Cleared ALL appointments"); toast("Cleared"); })
                .addOnFailureListener(e -> { setStatus("Clear failed: " + msg(e)); toast("Clear failed: " + msg(e)); });
    }

    private void updateStatusWithAuth() {
        FirebaseUser u = mAuth.getCurrentUser();
        if (u == null) setStatus("Auth: none");
        else setStatus("Auth: " + u.getEmail() + " uid=" + u.getUid());
    }

    private void setStatus(String s) {
        if (tvStatus != null) tvStatus.setText("Status: " + s);
        Log.d(TAG, s);
    }

    private void toast(String s) {
        if (getContext() != null) Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private static String msg(Exception e) {
        return (e == null || e.getMessage() == null) ? "Unknown error" : e.getMessage();
    }
}
