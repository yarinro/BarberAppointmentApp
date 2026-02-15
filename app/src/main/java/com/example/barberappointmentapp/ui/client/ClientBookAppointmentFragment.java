package com.example.barberappointmentapp.ui.client;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.logic.AppointmentFactory;
import com.example.barberappointmentapp.logic.OverlapUtils;
import com.example.barberappointmentapp.logic.SlotsCalculator;
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.ScheduleSettings;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.Slot;
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.models.WorkingHours;
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
import java.util.List;

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
    // ======================== CALLBACK INTERFACE ========================

    interface DoneCallback {
        void onDone(boolean ok); // true if callback was successful
    }
    // fetch schedule settings
    void loadScheduleSettings(DoneCallback cb) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("scheduleSettings");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                scheduleSettings = snap.getValue(ScheduleSettings.class);
                if (cb != null) cb.onDone(scheduleSettings != null && scheduleSettings.isValid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (cb != null) cb.onDone(false);
            }
        });
    }

    // fetch work windows for day of week chosen by the user
    void loadWorkWindowsForDay(int dayOfWeek, DoneCallback cb) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("workWindows")
                .child(String.valueOf(dayOfWeek));

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                workWindowsAll.clear();

                for (DataSnapshot child : snap.getChildren()) {
                    WorkingHours w = child.getValue(WorkingHours.class);
                    if (w == null) continue;
                    w.ensureId();
                    if (w.isValid()) workWindowsAll.add(w);
                }
                if (cb != null) cb.onDone(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (cb != null) cb.onDone(false);
            }
        });
    }


    // fetch time offs
    void loadTimeOffsForDay(long dayStartEpoch, DoneCallback cb) {
        long oneDay = 24L * 60L * 60L * 1000L;

        // Calculate the day end epoch by matching the timezone (in case of changing to summer/winter time)
        Calendar endCal = Calendar.getInstance(AppConfig.APP_TIMEZONE);
        endCal.setTimeInMillis(dayStartEpoch);
        endCal.add(Calendar.DAY_OF_MONTH, 1);
        long dayEndEpoch = endCal.getTimeInMillis();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("timeOffs");
        //gets time off's
        ref.orderByChild("startEpoch")
                .startAt(dayStartEpoch - oneDay) // >= day before start epoch. in order to not miss cross midnight time offs (i.e. time off started the day before and continues to the current day)
                .endAt(dayEndEpoch - 1) // < day end epoch
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        timeOffsAll.clear();
                        // Fetch
                        for (DataSnapshot child : snap.getChildren()) {
                            TimeOff timeoff = child.getValue(TimeOff.class);
                            if (timeoff == null) continue;
                            timeoff.ensureId();
                            if (!timeoff.isValid()) continue;

                            // timeoff.start < dayEnd && dayStart < timeoff.end
                            boolean overlaps = OverlapUtils.overlaps(timeoff.getStartEpoch(), timeoff.getEndEpoch(), dayStartEpoch, dayEndEpoch);
                            if (!overlaps) continue;

                            timeOffsAll.add(timeoff);
                        }

                        if (cb != null) cb.onDone(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (cb != null) cb.onDone(false);
                    }
                });
    }


    // fetch appointments
    void loadAppointmentsForDay(long dayStartEpoch, DoneCallback cb) {
        // Calculate the day end epoch by matching the timezone (in case of changing to summer/winter time)
        Calendar endCal = Calendar.getInstance(AppConfig.APP_TIMEZONE);
        endCal.setTimeInMillis(dayStartEpoch);
        endCal.add(Calendar.DAY_OF_MONTH, 1);
        long dayEndEpoch = endCal.getTimeInMillis();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("appointments");

        ref.orderByChild("startEpoch")
                .startAt(dayStartEpoch)
                .endAt(dayEndEpoch - 1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        appointmentsAll.clear();

                        for (DataSnapshot child : snap.getChildren()) {
                            Appointment ap = child.getValue(Appointment.class);
                            if (ap == null) continue;

                            String uid = ap.getClientUid();
                            if (uid == null || uid.trim().isEmpty()) {
                                continue; // data corrupted or legacy -> ignore
                            }
                            ap.ensureId(uid);
                            if (ap.getCancelled()) continue;
                            appointmentsAll.add(ap);

                        }
                        if (cb != null) cb.onDone(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (cb != null) cb.onDone(false);
                    }
                });
    }

    // fetch all time slot relevant data for a specific day
    void loadBookingDataForDay(long dayStartEpoch, int dayOfWeek, DoneCallback cb) {

        loadScheduleSettings(ok -> {
            if (!ok) { if (cb != null) cb.onDone(false); return; }

            loadWorkWindowsForDay(dayOfWeek, ok2 -> {
                if (!ok2) { if (cb != null) cb.onDone(false); return; }

                loadTimeOffsForDay(dayStartEpoch, ok3 -> {
                    if (!ok3) { if (cb != null) cb.onDone(false); return; }

                    loadAppointmentsForDay(dayStartEpoch, ok4 -> {if (cb != null) cb.onDone(ok4);});
                });
            });
        });
    }



    // ======================== END_CALLBACK_INTERFACE ========================
    // Date parameters
    long selectedDayStartEpoch = -1; // Start value to inspect whether chosen or not
    String selectedServiceId = null;
    // Service parameters
    int selectedServiceDurationMin = -1; // Start value to inspect whether chosen or not
    String selectedServiceName = null;
    Service selectedService = null;
    ScheduleSettings scheduleSettings = null;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_book_appointment, container, false);

        //----------------------------------BACK BUTTON-------------------------------------------
        ImageButton btnBack = view.findViewById(R.id.btn_back_client_book_appointment);

        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed()
        );
        //----------------------------------Getting all TextViews----------------------------------
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvService = view.findViewById(R.id.tvService);
        TextView tvSlot = view.findViewById(R.id.tvSlot);
        TextView tvNoAvailability = view.findViewById(R.id.tvNoAvailability);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        loadScheduleSettings(ok -> {if (!ok) Toast.makeText(getContext(), "Failed loading settings", Toast.LENGTH_SHORT).show();});
        //---------------------------------- DATE PICKING ----------------------------------
        // When user clicks on it -> show a date picker
        tvDate.setOnClickListener(v -> {
            // in case loading scheduleSettings failed
            if (scheduleSettings == null) {
                Toast.makeText(getContext(), "Loading settings, try again in a moment", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar c = Calendar.getInstance(AppConfig.APP_TIMEZONE);
            // Create the date picker dialog
            DatePickerDialog dlg = new DatePickerDialog(requireContext(),
                    (picker, year, month, day) -> {
                        // Getting the selected date
                        Calendar chosen = Calendar.getInstance(AppConfig.APP_TIMEZONE);
                        chosen.set(Calendar.YEAR, year);
                        chosen.set(Calendar.MONTH, month);
                        chosen.set(Calendar.DAY_OF_MONTH, day);
                        chosen.set(Calendar.HOUR_OF_DAY, 0);
                        chosen.set(Calendar.MINUTE, 0);
                        chosen.set(Calendar.SECOND, 0);
                        chosen.set(Calendar.MILLISECOND, 0);
                        // Convert selected day start to epoch time
                        selectedDayStartEpoch = chosen.getTimeInMillis();
                        tvDate.setText(TimeUtils.formatDate(selectedDayStartEpoch));
                        resetSelectedSlot(tvSlot);
                        hideNoAvailability(tvNoAvailability);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );
            // Set the minimum date to today
            // Min date = today (Israel timezone)
            Calendar minCal = Calendar.getInstance(AppConfig.APP_TIMEZONE);
            minCal.set(Calendar.HOUR_OF_DAY, 0);
            minCal.set(Calendar.MINUTE, 0);
            minCal.set(Calendar.SECOND, 0);
            minCal.set(Calendar.MILLISECOND, 0);
            dlg.getDatePicker().setMinDate(minCal.getTimeInMillis());

            // Max date = today + maxDaysAhead (Israel timezone)
            Calendar maxCal = Calendar.getInstance(AppConfig.APP_TIMEZONE);
            maxCal.set(Calendar.HOUR_OF_DAY, 0);
            maxCal.set(Calendar.MINUTE, 0);
            maxCal.set(Calendar.SECOND, 0);
            maxCal.set(Calendar.MILLISECOND, 0);
            maxCal.add(Calendar.DAY_OF_MONTH, scheduleSettings.getMaxDaysAhead());
            dlg.getDatePicker().setMaxDate(maxCal.getTimeInMillis());

            dlg.show();
        });
        //----------------------------------SERVICE PICKING----------------------------------
        ProgressBar progress = view.findViewById(R.id.progress);

        tvService.setOnClickListener(v -> {
            progress.setVisibility(View.VISIBLE);
            DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services");
            servicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<Service> servicesList = new ArrayList<>();
                    // Building the list of services from DB
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Service s = child.getValue(Service.class);
                        if (s == null) continue;
                        // if no ID in DB - get it from Firebase
                        s.ensureId();
                        // fetch only active services
                        if (!s.getActive()) continue;

                        servicesList.add(s);
                    }

                    progress.setVisibility(View.GONE);

                    if (servicesList.isEmpty()) {
                        Toast.makeText(getContext(), "No active services found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Dialog items
                    String[] items = new String[servicesList.size()];
                    for (int i = 0; i < servicesList.size(); i++) {
                        Service s = servicesList.get(i);
                        items[i] = s.getName() + " (" + s.getDurationMinutes() + "m) - ₪" + s.getPrice();
                    }

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Choose service")
                            .setItems(items, (dialog, which) -> {
                                Service chosen = servicesList.get(which);
                                // Writing the chosen service to parameters
                                selectedService = chosen; // The object to pass to SlotsCalculator
                                selectedServiceId = chosen.getId();
                                selectedServiceDurationMin = chosen.getDurationMinutes();
                                selectedServiceName = chosen.getName();

                                tvService.setText(chosen.getName() + " (" + chosen.getDurationMinutes() + "m)");
                                resetSelectedSlot(tvSlot);
                                hideNoAvailability(tvNoAvailability);
                            })
                            .show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        //----------------------------------SLOT PICKER - TIME PICKING - SHOWING AVAILABLE TIME SLOTS----------------------------------
        tvSlot.setOnClickListener(v -> {

            //validation
            if (selectedDayStartEpoch == -1) {
                Toast.makeText(getContext(), "Pick a date first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedService == null) {
                Toast.makeText(getContext(), "Pick a service first", Toast.LENGTH_SHORT).show();
                return;
            }

            // compute dayOfWeek (convert from epoch to day of week)
            Calendar cal = Calendar.getInstance(AppConfig.APP_TIMEZONE);
            cal.setTimeInMillis(selectedDayStartEpoch);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            //  load booking data
            progress.setVisibility(View.VISIBLE);
            loadBookingDataForDay(selectedDayStartEpoch, dayOfWeek, ok -> {
                progress.setVisibility(View.GONE);
                // if loading data failed
                if (!ok) {
                    hideNoAvailability(tvNoAvailability);
                    resetSelectedSlot(tvSlot);
                    Toast.makeText(getContext(), "Failed loading schedule data", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (workWindowsAll.isEmpty()) {
                    showNoAvailability(tvNoAvailability);
                    resetSelectedSlot(tvSlot);
                    return;
                }

                // =============================================== SlotsCalculator ============================================================
                // showing available slots = available times to set an appointment on chosen date
                List<Slot> slots = SlotsCalculator.getAvailableSlotsForDay(appointmentsAll, workWindowsAll, timeOffsAll, scheduleSettings, selectedService, selectedDayStartEpoch);

                if (slots.isEmpty()) {
                    showNoAvailability(tvNoAvailability);
                    resetSelectedSlot(tvSlot);
                    return;
                }

                // Filter past slots if selected date is today (Israel timezone)
                long now = System.currentTimeMillis(); // now in Epoch milliseconds

                // constructing a calendar that represents now in Israel timezone
                // client always sees current day in Israel timezone
                Calendar cNow = Calendar.getInstance(AppConfig.APP_TIMEZONE); // now in Israel timezone
                cNow.setTimeInMillis(now);

                Calendar cSel = Calendar.getInstance(AppConfig.APP_TIMEZONE);
                cSel.setTimeInMillis(selectedDayStartEpoch);

                boolean isToday = cNow.get(Calendar.YEAR) == cSel.get(Calendar.YEAR) && cNow.get(Calendar.DAY_OF_YEAR) == cSel.get(Calendar.DAY_OF_YEAR);

                List<Slot> slotsFiltered = slots;

                if (isToday) {
                    long grace = TimeUtils.minutesToMillis(1);
                    List<Slot> filtered = new ArrayList<>();
                    for (Slot s : slots) {
                        if (s.getStartEpoch() >= now + grace) {
                            filtered.add(s);
                        }
                    }
                    slotsFiltered = filtered;
                }

                final List<Slot> finalSlots = slotsFiltered;

                if (finalSlots.isEmpty()) {
                    showNoAvailability(tvNoAvailability);
                    resetSelectedSlot(tvSlot);
                    return;
                }

                hideNoAvailability(tvNoAvailability); // hide "no available appointments..."
                // build display strings
                String[] items = new String[finalSlots.size()];
                for (int i = 0; i < finalSlots.size(); i++) {
                    Slot s = finalSlots.get(i);


                    String start = TimeUtils.formatHHmm(s.getStartEpoch());
                    String end = TimeUtils.formatHHmm(s.getEndEpoch());

                    items[i] = start + " - " + end;
                }

                // show dialog
                new AlertDialog.Builder(requireContext())
                        .setTitle("Choose time")
                        .setItems(items, (dialog, which) -> {

                            Slot chosen = finalSlots.get(which);

                            selectedSlotStartEpoch = chosen.getStartEpoch();

                            tvSlot.setText(items[which]);
                        })
                        .show();
            });

        });

        // =========================CONFIRM APPOINTMENT===================================
        btnConfirm.setOnClickListener(v -> {

            // Basic validation
            if (selectedDayStartEpoch == -1) {
                Toast.makeText(getContext(), "Pick a date first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedService == null) {
                Toast.makeText(getContext(), "Pick a service first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedSlotStartEpoch == -1) {
                Toast.makeText(getContext(), "Pick a time first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Checking if selected time is already passed -> cannot set an appointment in the past -> return
            long now = System.currentTimeMillis();
            if (selectedSlotStartEpoch < now + TimeUtils.minutesToMillis(1)) {
                Toast.makeText(getContext(), "Selected time already passed. Please choose another time.", Toast.LENGTH_LONG).show();
                resetSelectedSlot(tvSlot);
                return;
            }
            // dayOfWeek from selected day
            Calendar cal = Calendar.getInstance(AppConfig.APP_TIMEZONE);
            cal.setTimeInMillis(selectedDayStartEpoch);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            progress.setVisibility(View.VISIBLE);

            // Reload latest data to avoid race conditions
            loadBookingDataForDay(selectedDayStartEpoch, dayOfWeek, ok -> {
                progress.setVisibility(View.GONE);

                if (!ok) {
                    hideNoAvailability(tvNoAvailability);
                    resetSelectedSlot(tvSlot);
                    Toast.makeText(getContext(), "Failed loading schedule data", Toast.LENGTH_SHORT).show();
                    return;
                }

                // IMPORTANT STEP: validating that the slot is still available when clicking on "confirm appointment" to avoid setting an appointment that is no longer available
                // prevents race conditions
                boolean stillAvailable = SlotsCalculator.isSlotStillAvailable(appointmentsAll, workWindowsAll, timeOffsAll, scheduleSettings, selectedService, selectedDayStartEpoch, selectedSlotStartEpoch);

                if (!stillAvailable) {
                    Toast.makeText(getContext(), "This appointment time is no longer available. Please choose another time.", Toast.LENGTH_LONG).show();
                    resetSelectedSlot(tvSlot);
                    hideNoAvailability(tvNoAvailability);
                    return;
                }

                // Build Slot object from chosen time
                long start = selectedSlotStartEpoch;
                long end = start + TimeUtils.minutesToMillis(selectedService.getDurationMinutes());
                Slot chosenSlot = new Slot(start, end);
                // Client info
                String clientUid = FirebaseAuth.getInstance().getUid();

                // Client uid
                if (clientUid == null) {
                    Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1) Load client details from DB (users/{uid})
                progress.setVisibility(View.VISIBLE);
                DatabaseReference userRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(clientUid);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        // read fields (adjust keys if needed)
                        String clientName = snap.child("name").getValue(String.class);
                        String clientPhone = snap.child("phone").getValue(String.class);

                        if (clientName == null) clientName = "";
                        if (clientPhone == null) clientPhone = "";

                        // 2) Create appointment via Factory
                        Appointment ap = AppointmentFactory.createFromSlot(clientUid, clientName, clientPhone, selectedService, chosenSlot);

                        if (ap == null) {
                            progress.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Failed creating appointment", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 3) Save to Firebase
                        ap.ensureId(clientUid);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("appointments");
                        // =======================DEBUG LOG==========================
                        Log.d("BOOK", "selectedDayStartEpoch=" + selectedDayStartEpoch + " selectedSlotStartEpoch=" + selectedSlotStartEpoch + " start=" + ap.getStartDateTime() + " end=" + ap.calcEndEpoch()); // אם יש לך, או start + duration);
                        Log.d("BOOK", "date=" + TimeUtils.formatDate(ap.getStartDateTime()) + " time=" + TimeUtils.formatHHmm(ap.getStartDateTime()));
                        // =======================DEBUG LOG==========================
                        ref.child(ap.getId()).setValue(ap)
                                .addOnSuccessListener(unused -> {
                                    progress.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                })
                                .addOnFailureListener(e -> {
                                    progress.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Failed saving appointment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed loading user: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });
        });


        return view;
    }
}