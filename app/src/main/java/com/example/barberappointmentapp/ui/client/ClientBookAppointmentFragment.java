package com.example.barberappointmentapp.ui.client;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.Settings;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClientBookAppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientBookAppointmentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // ======================== END_CALLBACK_INTERFACE ========================
    // Date parameters
    private LocalDateTime selectedDateTime = null;
    private Service selectedService = null;
    private Settings settings = null;
    private TextView tvDate, tvService, tvTime, tvNoAvailability, errorFailedDb;
    private Button btnConfirm;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private LinearLayout layout;
    int maxDaysAheadToBookAppointment;


    ArrayList<WorkingHours> workWindowsAll = new ArrayList<>();
    ArrayList<TimeOff> timeOffsAll = new ArrayList<>();
    ArrayList<Appointment> appointmentsAll = new ArrayList<>();
    // Time slot parameters
    long selectedSlotStartEpoch = -1; // Start value to inspect whether chosen or not

    // Resets the time slot and the text view in case of changing date or service
    private void resetSelectedSlot(TextView tvSlot) {
        selectedSlotStartEpoch = -1;
        tvSlot.setText(getString(R.string.time_hint));
    }
    // Shows the no available appointments text view
    private void showNoAvailability(TextView tvNoAvailability) {
        tvNoAvailability.setVisibility(View.VISIBLE);
    }
    // Hides the no available appointments text view
    private void hideNoAvailability(TextView tvNoAvailability) {
        tvNoAvailability.setVisibility(View.GONE);
    }

    public ClientBookAppointmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientBookAppointmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientBookAppointmentFragment newInstance(String param1, String param2) {
        ClientBookAppointmentFragment fragment = new ClientBookAppointmentFragment();
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
        View view = inflater.inflate(R.layout.fragment_client_book_appointment, container, false);

        //----------------------------------BACK BUTTON-------------------------------------------
        ImageButton btnBack = view.findViewById(R.id.btn_back_client_book_appointment);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });

        tvDate = view.findViewById(R.id.tvDate);
        tvService = view.findViewById(R.id.tvService);
        tvTime = view.findViewById(R.id.tvTime);
        tvNoAvailability = view.findViewById(R.id.tvNoAvailability);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        progressBar = view.findViewById(R.id.progress);
        layout = view.findViewById(R.id.layoutBookAppointment);
        errorFailedDb = view.findViewById(R.id.errorFailedDb);

        // getting settings data from DB
        hideUI();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference settingsRef = db.getReference("settings");

        settingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                showUI();
                settings = snapshot.getValue(Settings.class);

                if (settings != null){
                    maxDaysAheadToBookAppointment = maxDaysAheadToBookAppointment == 0 ? 14 : settings.getMaxDaysAheadToBookAppointment();
                }
                else{
                    hideUI();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                hideUI();

                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Listener for service
        tvService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // Listener for date picker
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNoAvailability.setVisibility(View.INVISIBLE);
                tvTime.setText("");

                final Calendar c = Calendar.getInstance();
                long nowMillis = System.currentTimeMillis();

                if (selectedDateTime != null) { // seletedDateTime==null if user did not pick a date yet
                    c.set(Calendar.YEAR, selectedDateTime.getYear());
                    c.set(Calendar.MONTH, selectedDateTime.getMonthValue() - 1);
                    c.set(Calendar.DAY_OF_MONTH, selectedDateTime.getDayOfMonth());
                } else {
                    c.setTimeInMillis(nowMillis);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                selectedDateTime = LocalDateTime.of(year, monthOfYear + 1, dayOfMonth, 0, 0);
                                String dateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + " \uD83D\uDCC5";
                                tvDate.setText(dateString);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(nowMillis); // set min date to today

                // set max date for the user to pick
                long maxDateMillis = nowMillis + TimeUtils.daysToMilliseconds(maxDaysAheadToBookAppointment);
                datePickerDialog.getDatePicker().setMaxDate(maxDateMillis);

                datePickerDialog.show();
            }
        });



        return view;
    }

    private void showUI(){
        layout.setVisibility(View.VISIBLE);
    }

    private void hideUI(){
        layout.setVisibility(View.GONE);
    }
}