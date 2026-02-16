package com.example.barberappointmentapp.testings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TesterFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private TextView tvStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tester, container, false);
        tvStatus = v.findViewById(R.id.tv_tester_status);

        v.findViewById(R.id.btn_create_users).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seedData();
            }
        });
        return v;
    }

    private void seedData() {
        tvStatus.setText("Seeding User 1 (Michael)...");
        // User 1: Michael Scott
        createFullUser("michael@test.com", "123456", "Michael Scott", "0541112233", new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                tvStatus.append("\nSeeding User 2 (Dwight)...");
                // User 2: Dwight Schrute
                createFullUser("dwight@test.com", "123456", "Dwight Schrute", "0544445566", null);
            }
        });
    }

    private void createFullUser(String email, String pass, String name, String phone, final OnCompleteListener<Void> onDone) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();

                    // 1. Create User in Realtime DB
                    User newUser = new User(uid, name, email, phone);
                    db.child("users").child(uid).setValue(newUser);

                    // 2. Seed 3 Appointments for today (Feb 16, 2026)
                    seedApptsForUser(uid, name, phone);

                    tvStatus.append("\nCreated: " + name);
                    mAuth.signOut();
                    if (onDone != null) onDone.onComplete(null);
                } else {
                    tvStatus.setText("Error: " + task.getException().getMessage());
                }
            }
        });
    }

    private void seedApptsForUser(String uid, String name, String phone) {
        try {
            Service srv = Service.create("Standard Cut", 60, 30, true);

            // Base date for today: February 16, 2026
            long today = 202602160000L;

            // Current Time Context: 21:42
            // 1. PAST appointment: 09:00 - 09:30 (Fixed: no leading zero to avoid octal error)
            Appointment pastAppt = Appointment.create(uid, name, phone, srv, today + 900, today + 930);

            // 2. HAPPENING NOW appointment: 21:30 - 22:00 (Covers 21:42)
            Appointment nowAppt = Appointment.create(uid, name, phone, srv, today + 2130, today + 2200);

            // 3. FUTURE appointment: 23:00 - 23:30
            Appointment futureAppt = Appointment.create(uid, name, phone, srv, today + 2300, today + 2330);

            // Save to /appointments/{id}
            db.child("appointments").child(pastAppt.getId()).setValue(pastAppt);
            db.child("appointments").child(nowAppt.getId()).setValue(nowAppt);
            db.child("appointments").child(futureAppt.getId()).setValue(futureAppt);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Appt Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}