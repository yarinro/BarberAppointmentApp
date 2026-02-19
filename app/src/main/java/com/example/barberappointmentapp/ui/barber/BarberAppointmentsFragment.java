package com.example.barberappointmentapp.ui.barber;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.adapters.BarberAppointmentsAdapter;
import com.example.barberappointmentapp.models.Appointment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberAppointmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberAppointmentsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Appointment> allAppointments = new ArrayList<>();
    private ArrayList<Appointment> filteredList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LocalDate selectedDate = null;
    private LinearLayoutManager layoutManager;
    private BarberAppointmentsAdapter adapter;

    private ProgressBar progress;
    private TextView tvEmpty;
    private Button btnShowAll;
    private Button btnPickDate;
    private TextView tvSelectedDate;
    private CheckBox cbShowCancelled;
    // UI elements


    public BarberAppointmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberAppointmentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberAppointmentsFragment newInstance(String param1, String param2) {
        BarberAppointmentsFragment fragment = new BarberAppointmentsFragment();
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
        View view = inflater.inflate(R.layout.fragment_barber_appointments, container, false);

        //----------------------------------BACK BUTTON-------------------------------------------
        ImageButton btnBack = view.findViewById(R.id.btn_back_barber_appointments);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });
        // ---------------- UI ----------------
        progress = view.findViewById(R.id.progress_barber_appointments);
        tvEmpty = view.findViewById(R.id.tv_empty_barber_appointments);
        btnShowAll = view.findViewById(R.id.btn_show_all_barber);
        btnPickDate = view.findViewById(R.id.btn_pick_date_barber);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date_barber);
        cbShowCancelled = view.findViewById(R.id.cb_show_cancelled_barber);

        // recyclerview + adapter
        recyclerView = view.findViewById(R.id.recycler_barber_appointments);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BarberAppointmentsAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = null; // all upcoming -> selectedDate=null
                tvSelectedDate.setText("All Upcoming");
                applyFiltersOnAppointmentsList();
            }
        });

        // Lisener for button pick date with date picker dialog
        // https://www.tutorialspoint.com/android/android_datepicker_control.htm
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear, mMonth, mDay;

                if (selectedDate != null) { // selectedDate==null if user did not pick a date yet
                    mYear = selectedDate.getYear();
                    mMonth = selectedDate.getMonthValue() - 1;
                    mDay = selectedDate.getDayOfMonth();
                } else {
                    // default - today
                    Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                                String dateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                tvSelectedDate.setText(dateString);

                                applyFiltersOnAppointmentsList();
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        // listener for show cancelled check box
        // https://developer.android.com/develop/ui/views/components/checkbox#HandlingEvents
        cbShowCancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyFiltersOnAppointmentsList();
            }
        });


        // Fetching appointments from Realtime DB
        progress.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE); // hide "no appointments"

        // get appointments from firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("appointments");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progress.setVisibility(View.GONE);
                    allAppointments.clear();

                    for (DataSnapshot data : snapshot.getChildren()) {
                        Appointment appointment = data.getValue(Appointment.class);
                        allAppointments.add(appointment);
                    }
                    allAppointments.sort((a, b) -> Long.compare(a.getStartDateTime(), b.getStartDateTime())); // sorting by appointment date and time
                        if (allAppointments.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                        }

                        applyFiltersOnAppointmentsList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progress.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        return view;
    }

    // applies filters on 'allAppointments' list and updates 'filteredList' list accordingly
    // important: run this function after finding all views by id in onCreateView()
    private void applyFiltersOnAppointmentsList() {
        filteredList.clear();
        boolean showCancelled = cbShowCancelled.isChecked();

        for (Appointment appointment : allAppointments) {
            LocalDate appointmentDate = appointment.getStartDateTimeObj().toLocalDate(); // converting LocalDateTime to LocalDate
            boolean isAppointmentCancelled = appointment.getCancelled();

            if (selectedDate == null && appointment.isPast()) continue;
            if (selectedDate != null && !appointmentDate.equals(selectedDate)) continue;
            if (!showCancelled && appointment.getCancelled()) continue;

            filteredList.add(appointment);
        }
        // if there are no appointments, show "no appointments"
        if (filteredList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

}
