package com.example.barberappointmentapp.ui.client;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.adapters.ClientAppointmentsAdapter;
import com.example.barberappointmentapp.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClientMyAppointmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientMyAppointmentsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Appointment> clientAppointments = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClientAppointmentsAdapter adapter;
    private ProgressBar progress;
    private TextView tvEmpty;
    private LinearLayoutManager layoutManager;


    public ClientMyAppointmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientMyAppointmentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientMyAppointmentsFragment newInstance(String param1, String param2) {
        ClientMyAppointmentsFragment fragment = new ClientMyAppointmentsFragment();
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
        View view = inflater.inflate(R.layout.fragment_client_my_appointments, container, false);

        //----------------------------------BACK BUTTON-------------------------------------------
        ImageButton btnBack = view.findViewById(R.id.btn_back_client_my_appointments);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });
        progress = view.findViewById(R.id.progress_appointments); // Progress bar
        tvEmpty = view.findViewById(R.id.tv_empty_appointments); // Text that shows when there are no appointments
        // Recycler view
        recyclerView  = view.findViewById(R.id.recycler_appointments);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ClientAppointmentsAdapter(clientAppointments);
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // get current logged in user
        if (user != null){
            progress.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);

            String clientUid = user.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("appointments");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progress.setVisibility(View.GONE);
                    clientAppointments.clear();

                    for (DataSnapshot data : snapshot.getChildren()) {
                        Appointment appointment = data.getValue(Appointment.class);
                        // filtering only appointments that belong to current user
                        if (appointment.getClientUid().equals(clientUid)) {
                            if(!appointment.isPast()){
                                clientAppointments.add(appointment); // do not show past appointments to client
                            }
                            }
                    }

                    // sort appointments by date and time
                    clientAppointments.sort((a, b) -> Long.compare(a.getStartDateTime(), b.getStartDateTime()));

                    // if there are no appointments for current user - show "no appointments" text view
                    if (clientAppointments.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{ // if user == null
            progress.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        return view;
    }
}