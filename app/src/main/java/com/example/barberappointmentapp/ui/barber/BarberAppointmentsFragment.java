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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.adapters.BarberAppointmentsAdapter;
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.utils.AppConfig;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed()
        );
        //----------------------------------------------------------------------------
        // ---------------- UI ----------------
        ProgressBar progress = view.findViewById(R.id.progress_barber_appointments);
        TextView tvEmpty = view.findViewById(R.id.tv_empty_barber_appointments);
        Button btnShowAll = view.findViewById(R.id.btn_show_all_barber);
        Button btnPickDate = view.findViewById(R.id.btn_pick_date_barber);
        TextView tvSelectedDate = view.findViewById(R.id.tv_selected_date_barber);
        CheckBox cbShowCancelled = view.findViewById(R.id.cb_show_cancelled_barber);

        // recyclerview
        RecyclerView recyclerView = view.findViewById(R.id.recycler_barber_appointments);
        ArrayList<Appointment> dataSet = new ArrayList<>(); // what shown in recycler view
        ArrayList<Appointment> allAppointments = new ArrayList<>(); // all appointments
        final long[] selectedDayStart = { -1L }; // -1 = All upcoming, else: start of selected date (epoch milliseconds)
        final boolean[] showCancelled = { false }; // showing cancelled appointments or not

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // adapter
        final BarberAppointmentsAdapter[] adapter = new BarberAppointmentsAdapter[1];
        adapter[0] = new BarberAppointmentsAdapter(dataSet, ap -> {
            if (ap == null || ap.getId() == null) return;

            new androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("Cancel appointment").setMessage("Are you sure you want to cancel this appointment?").setPositiveButton("Yes", (dialog, which) -> {
                        DatabaseReference r = FirebaseDatabase.getInstance().getReference("appointments").child(ap.getId()).child("cancelled");
                        r.setValue(true).addOnSuccessListener(unused -> {
                                    for (int i = 0; i < dataSet.size(); i++) {
                                        Appointment a = dataSet.get(i);
                                        if (a != null && ap.getId().equals(a.getId())) {
                                            a.setCancelled(true);
                                            adapter[0].notifyItemChanged(i);
                                            break;
                                        }
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Cancel failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                    }).setNegativeButton("No", null).show();
        });
        recyclerView.setAdapter(adapter[0]);

        // =================== RUNNABLE applyFilter() ===================
        // applying filter by date
        // Using Runnable to run applyFilter() inside onCreateView
        final Runnable applyFilter = () -> {
            dataSet.clear();

            if (selectedDayStart[0] == -1L) { // all upcoming
                long now = System.currentTimeMillis();
                for (Appointment ap : allAppointments) {
                    if (!showCancelled[0] && ap.getCancelled()) continue; // if user did not checked the checkbox -> skip cancelled appointments
                    if (ap.getStartEpoch() < now) continue; // show only future
                    dataSet.add(ap);
                }
            } else { // mode = filter by date
                long dayStart = selectedDayStart[0];
                // Calculate the day end epoch by matching the timezone (in case of changing to summer/winter time)
                Calendar endCal = Calendar.getInstance(AppConfig.APP_TIMEZONE);
                endCal.setTimeInMillis(dayStart);
                endCal.add(Calendar.DAY_OF_MONTH, 1);
                long dayEnd = endCal.getTimeInMillis(); // day end in Israel timezone

                long now = System.currentTimeMillis();

                for (Appointment ap : allAppointments) {
                    if (!showCancelled[0] && ap.getCancelled()) continue; // check whether to show cancelled or not

                    long s = ap.getStartEpoch();
                    if (s < now) continue; // show only future, even in date mode

                    if (s >= dayStart && s < dayEnd) {
                        dataSet.add(ap);
                    }
                }
            }

        //============================================================
            dataSet.sort((a, b) -> Long.compare(a.getStartEpoch(), b.getStartEpoch()));
            adapter[0].notifyDataSetChanged();
            tvEmpty.setVisibility(dataSet.isEmpty() ? View.VISIBLE : View.GONE);
        };
        // Clicking on button "All upcoming" -> show all upcoming appointments
        btnShowAll.setOnClickListener(v -> {
            selectedDayStart[0] = -1L; // mode = all upcoming. -1 = all
            tvSelectedDate.setText(getString(R.string.barber_appointments_selected_all)); // update the text of mode so user knows what mode is selected
            applyFilter.run(); // running the filter
        });

        // Date picker
        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance(AppConfig.APP_TIMEZONE);

            new DatePickerDialog(requireContext(), (dp, year, month, day) -> {
                Calendar chosen = Calendar.getInstance(AppConfig.APP_TIMEZONE);
                chosen.set(Calendar.YEAR, year);
                chosen.set(Calendar.MONTH, month);
                chosen.set(Calendar.DAY_OF_MONTH, day);
                chosen.set(Calendar.HOUR_OF_DAY, 0);
                chosen.set(Calendar.MINUTE, 0);
                chosen.set(Calendar.SECOND, 0);
                chosen.set(Calendar.MILLISECOND, 0);

                selectedDayStart[0] = chosen.getTimeInMillis();

                tvSelectedDate.setText(getString(R.string.barber_appointments_selected_date_prefix) + " " + TimeUtils.formatDate(selectedDayStart[0]));


                applyFilter.run();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Listener for show cancelled checkbox
        cbShowCancelled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showCancelled[0] = isChecked;
            applyFilter.run();
        });


        // loading appointments- progress bar
        progress.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE); // hide "no appointments"

        // ---------------- Firebase logic ----------------
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            progress.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE); // make "no appointments" visible
            return view;
        }
        // get appointments from firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("appointments");

        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allAppointments.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            Appointment ap = s.getValue(Appointment.class);
                            if (ap != null) {
                                ap.setId(s.getKey());
                                allAppointments.add(ap); // keep ALL appointments (past + future)
                            }
                        }

                        progress.setVisibility(View.GONE);

                        // Default mode - if nothing is selected yet - showing "all upcoming"
                        applyFilter.run();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progress.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        btnShowAll.performClick();

        return view;
    }
}