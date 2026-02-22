package com.example.barberappointmentapp.ui.barber;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.Settings;
import com.example.barberappointmentapp.models.WorkingDay;
import com.example.barberappointmentapp.ui.main.MainActivity;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BarberHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberHomeFragment newInstance(String param1, String param2) {
        BarberHomeFragment fragment = new BarberHomeFragment();
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
        View view =  inflater.inflate(R.layout.fragment_barber_home, container, false);

        //----------------------------------------------BUTTONS----------------------------------------------------------------------------
        // Sign out button
        ImageButton btnSignOut = view.findViewById(R.id.btn_barber_sign_out);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Alert dialog: "are you sure you want to sign out"
                // // https://developer.android.com/develop/ui/views/components/dialogs#AddButtons
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Sign out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // call sign out from MainActivity
                                MainActivity mainActivity =(MainActivity) getActivity();
                                mainActivity.signOut();

                                Navigation.findNavController(view).navigate(R.id.action_barberHomeFragment_to_welcomeFragment);
                            }
                        })
                        .setNegativeButton("No", null) // click no -> listener=null -> close the dialog
                        .show();
            }
        });

        Button btnViewAppointments = view.findViewById(R.id.btn_barber_view_appointments);
        btnViewAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_barberHomeFragment_to_barberAppointmentsFragment);
            }
        });

        Button btnSettings = view.findViewById(R.id.btn_barber_home_tosettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_barberHomeFragment_to_barberSettingsFragment);
            }
        });

        TextView tvClientName = view.findViewById(R.id.tvClientName);
        TextView tvClientPhone = view.findViewById(R.id.tvClientPhone);
        TextView tvServiceName = view.findViewById(R.id.tvServiceName);
        TextView tvDateTime = view.findViewById(R.id.tvDateTime);
        //--------------------------------------------------------------------------------------------------------------------------
        TextView tvAppointmentsToday = view.findViewById(R.id.tvAppointmentsToday);
        ProgressBar progressBar = view.findViewById(R.id.barberHomeProgress);
        LinearLayout barberHomeLayout = view.findViewById(R.id.barberHomeLayout);

        progressBar.setVisibility(View.VISIBLE);
        barberHomeLayout.setVisibility(View.INVISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // get appointments from firebase
        DatabaseReference appointmentsRef = database.getReference("appointments");

        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Appointment> appointments = new ArrayList<>();
                progressBar.setVisibility(View.GONE);
                barberHomeLayout.setVisibility(View.VISIBLE);

                ArrayList<Appointment> todayAppointments = new ArrayList<>();
                ArrayList<Appointment> futureAppointments = new ArrayList<>();
                LocalDate todayDate = LocalDate.now();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Appointment appointment = data.getValue(Appointment.class);
                    if (appointment != null) appointments.add(appointment);
                }

                for (Appointment appointment : appointments){
                    LocalDate appointmentDate = appointment.getStartDateTimeObj().toLocalDate();
                    if (appointmentDate.equals(todayDate) && !appointment.getCancelled()){
                        todayAppointments.add(appointment);
                    }
                    if (appointment.isFuture()){
                        futureAppointments.add(appointment);
                    }
                }
                // updating appointments today
                int appointmentsToday = todayAppointments.size();
                tvAppointmentsToday.setText(String.valueOf(appointmentsToday));

                futureAppointments.sort((a, b) -> Long.compare(a.getStartDateTime(), b.getStartDateTime()));
                if (futureAppointments.isEmpty()){
                    tvClientName.setText("No booked appointments\nyet for today...");
                    tvClientPhone.setText("");
                    tvServiceName.setText("");
                    tvDateTime.setText("");
                }
                else{
                    Appointment nextAppointment = futureAppointments.get(0);
                    tvClientName.setText(nextAppointment.getClientName());
                    tvClientPhone.setText(nextAppointment.getClientPhone());
                    tvServiceName.setText(nextAppointment.getServiceName());
                    tvDateTime.setText(TimeUtils.formatDateAndTimeRange(nextAppointment.getStartDateTime(), nextAppointment.getEndDateTime()));
                }

                barberHomeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                tvAppointmentsToday.setText("");
                tvClientName.setText("Error loading appointments...");
                tvClientPhone.setText("");
                tvServiceName.setText("");
                tvDateTime.setText("");
                barberHomeLayout.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), "error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }
}