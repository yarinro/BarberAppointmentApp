package com.example.barberappointmentapp.ui.client;

import android.app.DatePickerDialog;
import android.health.connect.datatypes.AppInfo;
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
import com.example.barberappointmentapp.models.User;
import com.example.barberappointmentapp.models.WorkingDay;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    private LocalDateTime selectedDateTimeMidnight = null;

    private int selectedDayOfWeek = -1;
    private int selectedBookingMinute = -1; // selected booking minute


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
    private boolean toReturn = false;

    private static class TimeWindow{
        private int start;
        private int end;

        public TimeWindow(int start, int end){
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }
        public void setStart(int start) {
            this.start = start;
        }
        public int getEnd() {
            return end;
        }
        public void setEnd(int end) {
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
                    if (settings.getWorkingDays() != null) workingDaysMap = settings.getWorkingDays();
                    if (settings.getTimeOffs() != null)  timeOffsList = settings.getTimeOffsAsList();
                    if (settings.getServicesAsList() != null)  servicesList = settings.getServicesAsList();
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
                // services spinner
                // https://developer.android.com/develop/ui/views/components/spinner
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Choose a Service")
                        .setItems(servicesNames, new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                selectedService = servicesList.get(which);
                                tvService.setText(selectedService.getName());

                                selectedBookingMinute = -1;
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
                tvTime.setText(R.string.time_hint);

                final Calendar c = Calendar.getInstance();
                long nowMillis = System.currentTimeMillis();

                if (selectedDateTimeMidnight != null) { // seletedDateTime==null if user did not pick a date yet
                    c.set(Calendar.YEAR, selectedDateTimeMidnight.getYear());
                    c.set(Calendar.MONTH, selectedDateTimeMidnight.getMonthValue() - 1);
                    c.set(Calendar.DAY_OF_MONTH, selectedDateTimeMidnight.getDayOfMonth());
                } else {
                    c.setTimeInMillis(nowMillis);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                selectedDateTimeMidnight = LocalDateTime.of(year, monthOfYear + 1, dayOfMonth, 0, 0);
                                selectedBookingMinute = -1;
                                // getting the day of week of selectedDateTimeMidnight
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
                if (selectedService == null || selectedDateTimeMidnight == null){
                    Toast.makeText(getContext(), "Please select a service and date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (workingDaysMap == null || workingDaysMap.isEmpty()) {
                    Toast.makeText(getContext(), "Working hours are not loaded yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                // setting the relevant working day object
                WorkingDay selectedWorkingDay = workingDaysMap.get("d" + selectedDayOfWeek);

                if(selectedWorkingDay == null) {
                    Toast.makeText(getContext(), "No working days were found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!selectedWorkingDay.getWorkDay()){ // if workDay=false
                    Toast.makeText(getContext(), "Barbershop is closed on this day", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<Integer> availableBookingMinutes = getAvailableBookingMinutes();
                if (availableBookingMinutes.isEmpty()){
                    tvNoAvailability.setVisibility(View.VISIBLE);
                    return;
                }

                String[] bookingTimesStrings = new String[availableBookingMinutes.size()];
                for (int i = 0; i < availableBookingMinutes.size(); i++) {
                    bookingTimesStrings[i] = TimeUtils.minutesToTimeString(availableBookingMinutes.get(i));
                }
                // available appointment booking times spinner
                // https://developer.android.com/develop/ui/views/components/spinner
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Choose time")
                        .setItems(bookingTimesStrings, new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                selectedBookingMinute = availableBookingMinutes.get(which);
                                tvTime.setText(bookingTimesStrings[which]);
                            }
                        })
                        .show();
            }
        });

        // on click listener for confirm appointment button
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNoAvailability.setVisibility(View.INVISIBLE);
                toReturn = false;
                if (selectedService == null || selectedDateTimeMidnight == null || selectedBookingMinute == -1){
                    Toast.makeText(getContext(), "Please select a service, date and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!getAvailableBookingMinutes().contains(selectedBookingMinute)){ //  validating that time is still available
                    Toast.makeText(getContext(), "This time is not available anymore", Toast.LENGTH_SHORT).show();
                    selectedBookingMinute = -1;
                    tvTime.setText(R.string.time_hint);
                    return;
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String userId = user.getUid();
                    DatabaseReference ref = db.getReference("users").child(userId);;
                    // getting user details from DB
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressBar.setVisibility(View.GONE);
                            User user = snapshot.getValue(User.class);
                            String clientUid = user.getUid();
                            String clientName = user.getName();
                            String clientPhone = user.getPhone();
                            String serviceId = selectedService.getId();
                            String serviceName = selectedService.getName();

                            LocalDateTime dtStart = selectedDateTimeMidnight.plusMinutes(selectedBookingMinute);
                            LocalDateTime dtEnd = dtStart.plusMinutes(selectedService.getDurationMinutes());
                            long startDateTime = TimeUtils.toLong(dtStart);
                            long endDateTime = TimeUtils.toLong(dtEnd);

                            // write appointment to DB
                            progressBar.setVisibility(View.VISIBLE);
                            Appointment newAppointment = Appointment.create(clientUid, clientName, clientPhone, serviceName, serviceId, startDateTime, endDateTime);
                            appointmentsRef.child(newAppointment.getId()).setValue(newAppointment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.GONE);
                                            tvNoAvailability.setVisibility(View.INVISIBLE);
                                            tvService.setText(R.string.service_hint);
                                            tvDate.setText(R.string.date_hint);
                                            tvTime.setText(R.string.time_hint);
                                            selectedService = null;
                                            selectedDateTimeMidnight = null;
                                            selectedBookingMinute = -1;
                                            selectedDayOfWeek = -1;
                                            Toast.makeText(v.getContext(), "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            tvNoAvailability.setVisibility(View.INVISIBLE);
                                            Toast.makeText(v.getContext(), "Failed booking appointment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Failed booking appointment: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
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

    private ArrayList<Integer> getAvailableBookingMinutes() {
        ArrayList<TimeWindow> blockedTimeWindows = new ArrayList<>(); // list of blocked time windows in current selected date       blocked = appointments/timeoffs/breaks
        // setting the relevant working day object
        WorkingDay selectedWorkingDay = workingDaysMap.get("d" + selectedDayOfWeek);

        if (selectedWorkingDay == null || selectedService == null)
            return new ArrayList<>();

        // blocked time windows from other appointments booked at selectedDate
        for (Appointment appointment : appointmentsList) {
            if (!appointment.getCancelled() && appointment.getStartDateTimeObj().toLocalDate().equals(selectedDateTimeMidnight.toLocalDate())) {
                int startMinute = appointment.getStartDateTimeObj().getHour() * 60 + appointment.getStartDateTimeObj().getMinute();
                int endMinute = startMinute + appointment.getDurationMinutes();
                blockedTimeWindows.add(new TimeWindow(startMinute, endMinute));
            }
        }
        // blocked time windows from breaks
        for (Break br : selectedWorkingDay.getBreaks()) {
            blockedTimeWindows.add(new TimeWindow(br.getStartMinute(), br.getEndMinute()));
        }

        // blocked time windows from time offs
        LocalDateTime dayStart = LocalDateTime.of(selectedDateTimeMidnight.toLocalDate(), selectedDateTimeMidnight.toLocalTime()); // selectedDate at 00:00
        LocalDateTime dayEnd = selectedDateTimeMidnight.plusHours(23).plusMinutes(59); // selectedDate at 23:59
        for (TimeOff timeOff : timeOffsList) {
            LocalDateTime timeOffStart = timeOff.getStartDateTimeObj();
            LocalDateTime timeOffEnd = timeOff.getEndDateTimeObj();
            if (!(timeOffEnd.isBefore(dayStart) || timeOffStart.isAfter(dayEnd))) {
                // intersection between timeOff and current date
                LocalDateTime intersectionStart = timeOffStart.isBefore(dayStart) ? dayStart : timeOffStart;
                LocalDateTime intersectionEnd = timeOffEnd.isAfter(dayEnd) ? dayEnd : timeOffEnd;

                if (intersectionStart.isBefore(intersectionEnd)) {
                    int startMinute = intersectionStart.getHour() * 60 + intersectionStart.getMinute();
                    int endMinute = intersectionEnd.getHour() * 60 + intersectionEnd.getMinute();
                    blockedTimeWindows.add(new TimeWindow(startMinute, endMinute));
                }
            }
        }
        // calculating available booking minutes
        ArrayList<Integer> availableBookingMinutes = new ArrayList<>();
        int workingDayStartMinute = selectedWorkingDay.getStartMinute();
        int workingDayEndMinute = selectedWorkingDay.getEndMinute();
        int serviceDuration = selectedService.getDurationMinutes();
        int currTime = workingDayStartMinute;

        while (currTime + serviceDuration <= workingDayEndMinute) {
            boolean isBlocked = false;
            for (TimeWindow blockedTimeWindow : blockedTimeWindows) {
                if (!(currTime + serviceDuration <= blockedTimeWindow.start || currTime >= blockedTimeWindow.end)) {
                    isBlocked = true;
                    break;
                }
            }
            if (!isBlocked) {
                availableBookingMinutes.add(currTime);
            }
            currTime += 15;
        }

        Collections.sort(availableBookingMinutes);
        return availableBookingMinutes;
    }

}