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
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.ScheduleSettings;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.models.WorkingHours;
import java.util.ArrayList;


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

        //---------------------------------- DATE PICKING ----------------------------------

        //----------------------------------SERVICE PICKING----------------------------------
        ProgressBar progress = view.findViewById(R.id.progress);
        return view;
    }
}