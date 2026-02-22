package com.example.barberappointmentapp.ui.barber;

import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.adapters.BreaksAdapter;
import com.example.barberappointmentapp.models.Break;
import com.example.barberappointmentapp.models.WorkingDay;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberScheduleManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberScheduleManagementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout mainLayout;
    private ImageButton btnBack;
    private MaterialButton[] dayButtons = new MaterialButton[7]; // array for the buttons sun-sat
    private MaterialButton btnStartTime, btnEndTime;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SwitchMaterial switchIsWorkDay;
    private TextView tvNoBreaks;
    private BreaksAdapter adapter;
    private ArrayList<Break> breaksList = new ArrayList<>();
    private MaterialButton btnAddBreak, btnSaveSchedule;
    private ProgressBar progressBar;

    private int selectedDay=0; // sunday

    public BarberScheduleManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberScheduleSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberScheduleManagementFragment newInstance(String param1, String param2) {
        BarberScheduleManagementFragment fragment = new BarberScheduleManagementFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barber_schedule_management, container, false);

        //----------------------------------BACK BUTTON-------------------------------------------
        btnBack = view.findViewById(R.id.btn_back_barber_schedule_management);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });
        //----------------------------------BACK BUTTON-------------------------------------------

        mainLayout = view.findViewById(R.id.ScheduleManagementLayout);

        dayButtons[0] = view.findViewById(R.id.btnDaySun);
        dayButtons[1] = view.findViewById(R.id.btnDayMon);
        dayButtons[2] = view.findViewById(R.id.btnDayTue);
        dayButtons[3] = view.findViewById(R.id.btnDayWed);
        dayButtons[4] = view.findViewById(R.id.btnDayThu);
        dayButtons[5] = view.findViewById(R.id.btnDayFri);
        dayButtons[6] = view.findViewById(R.id.btnDaySat);
        // working hours
        btnStartTime = view.findViewById(R.id.btnStartTime);
        btnEndTime = view.findViewById(R.id.btnEndTime);

        recyclerView = view.findViewById(R.id.recyclerViewBreaksScheduleManagement);
        tvNoBreaks = view.findViewById(R.id.tvNoBreaksForDay);
        btnAddBreak = view.findViewById(R.id.btnAddBreak);
        btnSaveSchedule = view.findViewById(R.id.btnSaveSchedule);
        progressBar = view.findViewById(R.id.progressBarSchedule);
        switchIsWorkDay = view.findViewById(R.id.switchIsWorkDay);

        // recyclerview + adapter
        recyclerView = view.findViewById(R.id.recyclerViewBreaksScheduleManagement);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BreaksAdapter(breaksList);
        recyclerView.setAdapter(adapter);


        // setting on click listener for each day button
        for (int i = 0; i < dayButtons.length; i++) {
            final int clickedDay = i;
            dayButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedDay = clickedDay;

                    // change the color of the clicked day
                    for (int i = 0; i < dayButtons.length; i++) {
                        if (i == selectedDay) {
                            dayButtons[i].setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                            dayButtons[i].setTextColor(Color.WHITE);
                        } else {
                            dayButtons[i].setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                            dayButtons[i].setTextColor(Color.BLACK);
                        }
                    }
                    // getting data from firebase
                    String currentDayString = "d" + (selectedDay + 1);
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference("settings").child("workingDays").child(currentDayString);

                    progressBar.setVisibility(View.VISIBLE);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressBar.setVisibility(View.GONE);
                            WorkingDay workingDay = snapshot.getValue(WorkingDay.class);

                            if (workingDay == null) {
                                    workingDay = new WorkingDay(1, true, 540, 1080, new ArrayList<>());
                                }

                            boolean isWorkDay = workingDay.getWorkDay();
                            int startMinute = workingDay.getStartMinute();
                            int endMinute = workingDay.getEndMinute();

                            switchIsWorkDay.setChecked(isWorkDay);
                            btnStartTime.setText(TimeUtils.formatMinuteOfDay(workingDay.getStartMinute()));
                            btnEndTime.setText(TimeUtils.formatMinuteOfDay(workingDay.getEndMinute()));

                            // update adapter
                            breaksList.clear(); // clear the adapter's list before loading new data
                            if (workingDay.getBreaks() != null){
                                breaksList.addAll(workingDay.getBreaks()); // add the new data to the adapter's list
                                breaksList.sort((b1, b2) -> Integer.compare(b1.getStartMinute(), b2.getStartMinute()));

                            }
                            adapter.notifyDataSetChanged();
                            // if there are no breaks on work day- show "no breaks" text
                            if (breaksList.isEmpty()) {
                                tvNoBreaks.setVisibility(View.VISIBLE);
                            } else {
                                tvNoBreaks.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }

        dayButtons[0].performClick(); // clicks on sunday to show it first when user opens the fragment

        // save button listener
        btnSaveSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String selectedDayString = "d" + (selectedDay + 1);
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("settings").child("workingDays").child(selectedDayString);

                int startMin = TimeUtils.timeStringToMinutes(btnStartTime.getText().toString());
                int endMin = TimeUtils.timeStringToMinutes(btnEndTime.getText().toString());
                boolean isWorkDay = switchIsWorkDay.isChecked();

                // validation - before writing to DB
                if (endMin <= startMin){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "End time must be > start time!", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Break b : breaksList) {
                    if (b.getStartMinute() < startMin || b.getEndMinute() > endMin) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "All breaks must be within working hours!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // validation - checking for overlapping breaks
                if (!breaksList.isEmpty()){
                    ArrayList<Break> sorted = new ArrayList<>(breaksList);
                    sorted.sort((a, b) -> Integer.compare(a.getStartMinute(), b.getStartMinute()));
                    Break previous = sorted.get(0);

                    for (int i = 1; i < sorted.size(); i++) {
                        Break current = sorted.get(i);
                        // Overlap condition
                        if (current.getStartMinute() < previous.getEndMinute()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Breaks must not overlap!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        previous = current;
                    }
                }
                // writing updated WorkingDay object to firebase
                WorkingDay workingDayToDB = new WorkingDay(selectedDay + 1, isWorkDay, startMin, endMin, breaksList);
                ref.setValue(workingDayToDB).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(v.getContext(), "Schedule for " + workingDayToDB.getDayName() + "saved successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // on click listener for end hour with time picker
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time from button to start the picker at the right spot
                int currentMinutes = TimeUtils.timeStringToMinutes(btnStartTime.getText().toString());
                int hour = currentMinutes / 60;
                int minute = currentMinutes % 60;

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Use your minutesToTimeString to ensure HH:mm format
                                String timeString = TimeUtils.minutesToTimeString(hourOfDay * 60 + minute);
                                btnStartTime.setText(timeString);
                            }
                        }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        // on click listener for start hour with time picker
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time from button
                int currentMinutes = TimeUtils.timeStringToMinutes(btnEndTime.getText().toString());
                int hour = currentMinutes/60;
                int minute = currentMinutes%60;

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Use your minutesToTimeString to ensure HH:mm format
                                String timeString = TimeUtils.minutesToTimeString(hourOfDay * 60 + minute);
                                btnEndTime.setText(timeString);
                            }
                        }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        // Add break button listener
        btnAddBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog startPicker = new TimePickerDialog(getContext(), (view, startH, startM) -> {
                    int startTotal = (startH * 60) + startM;

                    TimePickerDialog endPicker = new TimePickerDialog(getContext(), (viewEnd, endH, endM) -> {
                        int endTotal = (endH * 60) + endM;

                        // validation
                        if (endTotal <= startTotal) {
                            Toast.makeText(getContext(), "End time must be > start time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // creating the new break to add to adapter's list
                        Break newBreak = Break.create(selectedDay + 1, startTotal, endTotal);
                        breaksList.add(newBreak);
                        breaksList.sort((b1, b2) -> Integer.compare(b1.getStartMinute(), b2.getStartMinute()));

                        adapter.notifyDataSetChanged();
                        tvNoBreaks.setVisibility(View.GONE);

                    }, (startTotal + 30) / 60, (startTotal + 30) % 60, true); // Default to 30 mins later

                    endPicker.setTitle("Select Break End Time");
                    endPicker.show();

                }, 12, 0, true); // Default start time for new breaks: 12:00

                startPicker.setTitle("Select Break Start Time");
                startPicker.show();
            }
        });


        return view;

    }

}