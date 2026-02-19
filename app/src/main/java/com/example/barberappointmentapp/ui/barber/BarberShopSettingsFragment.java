package com.example.barberappointmentapp.ui.barber;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberShopSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberShopSettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton btnBack;
    private EditText etShopName, etAddress, etPhone, etAboutUs, etMaxDays;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private LinearLayout fragmentLayout;

    public BarberShopSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberShopSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberShopSettingsFragment newInstance(String param1, String param2) {
        BarberShopSettingsFragment fragment = new BarberShopSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_barber_shop_settings, container, false);


        btnBack = view.findViewById(R.id.btn_back_shop_settings);
        etShopName = view.findViewById(R.id.etSettingsShopName);
        etAddress = view.findViewById(R.id.etSettingsAddress);
        etPhone = view.findViewById(R.id.etSettingsPhone);
        etAboutUs = view.findViewById(R.id.etSettingsAboutUs);
        etMaxDays = view.findViewById(R.id.etSettingsMaxDays);
        btnSave = view.findViewById(R.id.btnSaveShopSettings);
        progressBar = view.findViewById(R.id.progressShopSettings);
        fragmentLayout = view.findViewById(R.id.barberShopSettingsLayout);

        hideUI();
        progressBar.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference settingsRef = database.getReference("settings");

        // listener for loading the data from firebase
        settingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                    Settings settings = snapshot.getValue(Settings.class);
                    if (settings != null) {
                        etShopName.setText(settings.getBarbershopName());
                        etAddress.setText(settings.getAddress());
                        etPhone.setText(settings.getPhoneNumber());
                        etAboutUs.setText(settings.getAboutUs());
                        etMaxDays.setText(String.valueOf(settings.getMaxDaysAheadToBookAppointment()));
                    }
                    else{
                        etShopName.setText("");
                        etAddress.setText("");
                        etPhone.setText("");
                        etAboutUs.setText("");
                        etMaxDays.setText("");
                    }
                showUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                showUI();
                Toast.makeText(getContext(), "Failed to load: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // listener for save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etShopName.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String about = etAboutUs.getText().toString().trim();
                String maxDaysStr = etMaxDays.getText().toString().trim();

                // validation before saving into db
                if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || about.isEmpty() || maxDaysStr.isEmpty()) {
                    Toast.makeText(v.getContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!phone.matches("\\d+")) {
                    etPhone.setError("Please enter a valid phone number");
                    return;
                }

                int maxDays;
                try{
                    maxDays = Integer.parseInt(maxDaysStr);
                    if (maxDays <= 0 || maxDays > 365) {
                        etMaxDays.setError("max days ahead should be between 1 and 365");
                        return;
                    }
                }
                catch (Exception e){
                    etMaxDays.setError("Please enter a valid number for max days ahead");
                    return;
                }

                // saving data into DB
                hideUI();
                progressBar.setVisibility(View.VISIBLE);
                // https://firebase.google.com/docs/database/android/read-and-write#updating_or_deleting_data
                Map<String, Object> newValues = new HashMap<>();
                newValues.put("barbershopName", name);
                newValues.put("address", address);
                newValues.put("phoneNumber", phone);
                newValues.put("aboutUs", about);

                newValues.put("maxDaysAheadToBookAppointment", Integer.parseInt(maxDaysStr));
                settingsRef.updateChildren(newValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                showUI();
                                Toast.makeText(v.getContext(), "Settings updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                showUI();
                                Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        return view;
    }

    private void showUI() {
        fragmentLayout.setVisibility(View.VISIBLE);
    }

    private void hideUI() {
        fragmentLayout.setVisibility(View.GONE);
    }

}