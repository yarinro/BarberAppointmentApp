package com.example.barberappointmentapp.ui.barber;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
import com.example.barberappointmentapp.adapters.ServicesAdapter;
import com.example.barberappointmentapp.adapters.TimeOffsAdapter;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.TimeOff;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberHolidaysAndTimeOffsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberHolidaysAndTimeOffsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TimeOffsAdapter adapter;
    private ArrayList<TimeOff> timeOffsList = new ArrayList<>();

    public BarberHolidaysAndTimeOffsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberHolidaysAndTimeOffsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberHolidaysAndTimeOffsFragment newInstance(String param1, String param2) {
        BarberHolidaysAndTimeOffsFragment fragment = new BarberHolidaysAndTimeOffsFragment();
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
        View view =  inflater.inflate(R.layout.fragment_barber_holidays_and_time_offs, container, false);
        //----------------------------------BACK BUTTON-------------------------------------------
        ImageButton btnBack = view.findViewById(R.id.btn_back_barber_time_offs);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });
        //----------------------------------------------------------------------------------------
        TextView tvTitle = view.findViewById(R.id.tv_time_offs_title);
        TextView tvNoTimeOffsYet = view.findViewById(R.id.tv_no_time_offs_yet);
        MaterialButton btnAddTimeOff = view.findViewById(R.id.btn_add_time_off);
        ProgressBar progress = view.findViewById(R.id.progress_bar_time_offs);

        // recyclerview + adapter
        recyclerView = view.findViewById(R.id.recycler_time_offs);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new TimeOffsAdapter(timeOffsList);
        recyclerView.setAdapter(adapter);

        progress.setVisibility(View.VISIBLE);
        tvNoTimeOffsYet.setVisibility(View.GONE);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("settings").child("timeoffs");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progress.setVisibility(View.GONE);
                timeOffsList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    TimeOff timeoff = data.getValue(TimeOff.class);
                    if (timeoff.isHappeningNow() || timeoff.isFuture()){ // adding time offs that are happening now or in the future
                        timeOffsList.add(timeoff);
                    }
                }
                timeOffsList.sort((t1, t2) -> Long.compare(t1.getStartDateTime(), t2.getStartDateTime()));

                if (timeOffsList.isEmpty()){
                    tvNoTimeOffsYet.setVisibility(View.VISIBLE);
                } else {
                    tvNoTimeOffsYet.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progress.setVisibility(View.GONE);
                tvNoTimeOffsYet.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btnAddTimeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_barberHolidaysAndTimeOffsFragment_to_barberAddTimeOffFragment);
            }
        });

        return view;
    }
}