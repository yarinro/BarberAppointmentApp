package com.example.barberappointmentapp.testings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.barberappointmentapp.R;

public class TestSeedClientBookAppointmentFragment extends Fragment {

    public TestSeedClientBookAppointmentFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test_seed_client_book_appointment, container, false);

        Button btnSeed = view.findViewById(R.id.btnSeedData);

        btnSeed.setOnClickListener(v -> {
            SeedDataClientBookAppointment.seedAll();
            Toast.makeText(getContext(), "Seed inserted", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
