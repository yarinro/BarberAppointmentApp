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
import com.example.barberappointmentapp.models.Break;
import com.example.barberappointmentapp.models.Settings;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.models.WorkingDay;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


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
    private Service selectedService = null;
    private LocalDateTime selectedDateTime = null;
    private int selectedDayOfWeek = -1;
    private long selectedDateLong = -1L, selectedTimeLong = -1L; // selected date and time in long YYYYMMddHHmm


    private Settings settings = null;
    private TextView tvDate, tvService, tvTime, tvNoAvailability, errorFailedDb;
    private Button btnConfirm;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private LinearLayout layout;
    int maxDaysAheadToBookAppointment;
    private Map<String, WorkingDay> workingDaysMap = new HashMap<>(); // key: day of week, value: working day object
    private ArrayList<TimeOff> timeOffsList = new ArrayList<>(); // list of time offs
    private ArrayList<Service> servicesList = new ArrayList<>(); // list of services;
    ArrayList<Appointment> appointmentsList = new ArrayList<>(); // list of appointments

    private static class TimeWindow{
        private long start;
        private long end;

        public TimeWindow(long start, long end){
            this.start = start;
            this.end = end;
        }

        public long getStart() {
            return start;
        }
        public void setStart(long start) {
            this.start = start;
        }
        public long getEnd() {
            return end;
        }
        public void setEnd(long end) {
            this.end = end;
        }
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
                errorFailedDb.setVisibility(View.GONE);
                showUI();
                settings = snapshot.getValue(Settings.class);

                if (settings != null){
                    maxDaysAheadToBookAppointment = maxDaysAheadToBookAppointment == 0 ? 14 : settings.getMaxDaysAheadToBookAppointment(); // default value
                    if (workingDaysMap != null) workingDaysMap = settings.getWorkingDays();
                    if (timeOffsList != null)  timeOffsList = settings.getTimeOffs();
                    if (servicesList != null)  servicesList = settings.getServicesAsList();
                }
                else{ // if no settings in DB -> show error and hide UI
                    hideUI();
                    errorFailedDb.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                hideUI();
                errorFailedDb.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // getting all appointments from DB
        DatabaseReference appointmentsRef = db.getReference("appointments");
        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showUI();
                appointmentsList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Appointment appointment = data.getValue(Appointment.class);
                    appointmentsList.add(appointment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideUI();
                errorFailedDb.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Listener for select service
        tvService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Check if the list has loaded from Firebase yet
                if (servicesList.isEmpty()) {
                    Toast.makeText(getContext(), "No services were found", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] servicesNames = new String[servicesList.size()];
                for (int i = 0; i < servicesList.size(); i++) {
                    servicesNames[i] = servicesList.get(i).toString();
                }

                // https://developer.android.com/develop/ui/views/components/spinner
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Choose a Service")
                        .setItems(servicesNames, new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                selectedService = servicesList.get(which);
                                tvService.setText(selectedService.getName());

                                selectedTimeLong = -1L;
                                tvTime.setText(R.string.time_hint);
                            }
                        })
                        .show();
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
                                selectedDateLong = TimeUtils.toLong(selectedDateTime);
                                selectedTimeLong = -1L;
                                // getting the day of week of selectedDateTime
                                Calendar onDateSetCalendar = Calendar.getInstance();
                                onDateSetCalendar.set(year, monthOfYear, dayOfMonth);
                                selectedDayOfWeek = onDateSetCalendar.get(Calendar.DAY_OF_WEEK);

                                tvDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + " \uD83D\uDCC5");
                                tvTime.setText(R.string.time_hint);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(nowMillis); // set min date to today

                // set max date for the user to pick
                long maxDateMillis = nowMillis + TimeUtils.daysToMilliseconds(maxDaysAheadToBookAppointment);
                datePickerDialog.getDatePicker().setMaxDate(maxDateMillis);

                datePickerDialog.show();
            }
        });

        // on click listener for time
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking if user selected service and date
                if (selectedService == null || selectedDateTime == null){
                    Toast.makeText(getContext(), "Please select a service and date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // setting the relevant working day object
                WorkingDay selectedWorkingDay = workingDaysMap.get(String.valueOf(selectedDayOfWeek));

                if(selectedWorkingDay == null) {
                    Toast.makeText(getContext(), "No working days were found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!selectedWorkingDay.getWorkDay()){ // if workDay=false
                    Toast.makeText(getContext(), "Barbershop is closed on this day", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        // on click listener for confirm appointment button
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    private ArrayList<TimeWindow> getAvailableTimeWindowsForAppointment(){
//        ArrayList<TimeWindow> blockedTimeWindows = new ArrayList<>();
//
//        for (Appointment appointment : appointmentsList){
//            // filtering non cancelled appointments that are on the same date as selected date
//            if (!appointment.getCancelled() && appointment.getStartDateTimeObj().toLocalDate().equals(selectedDateTime.toLocalDate())){
//                long start = appointment.getStartDateTime();
//                long end = appointment.getEndDateTime();
//
//                blockedTimeWindows.add(new TimeWindow(start, end));
//            }
//        }
//
//        WorkingDay selectedWorkingDay = workingDaysMap.get(String.valueOf(selectedDayOfWeek));
//        ArrayList<Break> breaksForSelectedDay = new ArrayList<>();
//        int workingDayStartMinute = selectedWorkingDay.getStartMinute();
//        int workingDayEndMinute = selectedWorkingDay.getEndMinute();
//
//        for (Break br : selectedWorkingDay.getBreaks()){
//            long start = TimeUtils.addMinutesToDate(workingDayStartMinute, br.getStartMinute());
//            long end = TimeUtils.addMinutesToDate(workingDayStartMinute, br.getEndMinute());
//
//            blockedTimeWindows.add(new TimeWindow(start, end));
//        }
//
//        for (TimeOff timeOff: timeOffsList){
//            long startTimeOff = timeOff.getStartDateTime();
//            long endTimeOff = timeOff.getEndDateTime();
//            long start = Math.max(startTimeOff, workingDayStartMinute);
//            long end = Math.min(endTimeOff, workingDayEndMinute);
//
//            blockedTimeWindows.add(new TimeWindow(start, end));
//        }
//
//
//
//        // setting the relevant working day object
//        WorkingDay selectedWorkingDay = workingDaysMap.get(String.valueOf(selectedDayOfWeek));
//
//
//        ArrayList<Break> breaksForSelectedDay = new ArrayList<>();
//        int selectedServiceDuration = selectedService.getDurationMinutes();
//
//        // sorting the lists
//        appointmentsSelectedDate.sort((a1, a2) -> Long.compare(a1.getStartDateTime(),a2.getStartDateTime()));
//        relevantTimeOffs.sort((t1, t2) -> Long.compare(t1.getStartDateTime(),t2.getStartDateTime()));
//        breaksForSelectedDay.sort((b1, b2) -> Integer.compare(b1.getStartMinute(), b2.getStartMinute()));
//
//
//
//
//
//
//
//
//
//        return availableTimeWindows;

        ArrayList<TimeWindow> blockedMinutes = new ArrayList<>(); // list of blocked time windows in current selected date       blocked = appointments/timeoffs/breaks
        // setting the relevant working day object
        WorkingDay selectedWorkingDay = workingDaysMap.get(String.valueOf(selectedDayOfWeek));

        int dayStart = selectedWorkingDay.getStartMinute();
        int dayEnd = selectedWorkingDay.getEndMinute();
        int duration = selectedService.getDurationMinutes();

        for (Appointment appointment : appointmentsList) {
            if (!appointment.getCancelled() && appointment.getStartDateTimeObj().toLocalDate().equals(selectedDateTime.toLocalDate())){
                int start = appointment.getStartDateTimeObj().getHour() * 60 + appointment.getStartDateTimeObj().getMinute();
                int end = start + appointment.getDurationMinutes();
                blockedMinutes.add(new TimeWindow(start, end));
            }
        }

        for (Break br : selectedWorkingDay.getBreaks()) {
            blockedMinutes.add(new TimeWindow(br.getStartMinute(), br.getEndMinute()));
        }
    }

}