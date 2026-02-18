package com.example.barberappointmentapp.ui.barber;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberAddServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberAddServiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BarberAddServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberAddServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberAddServiceFragment newInstance(String param1, String param2) {
        BarberAddServiceFragment fragment = new BarberAddServiceFragment();
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
        View view =  inflater.inflate(R.layout.fragment_barber_add_service, container, false);

        //----------------------------------BACK BUTTON-------------------------------------------
        ImageButton btnBack = view.findViewById(R.id.btn_back_add_service);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });
        //----------------------------------------------------------------------------------------

        EditText etServiceName = view.findViewById(R.id.etServiceName);
        EditText etServicePrice = view.findViewById(R.id.etServicePrice);
        EditText etServiceDuration = view.findViewById(R.id.etServiceDuration);
        MaterialButton btnAddService = view.findViewById(R.id.btn_add_service);
        ProgressBar progress = view.findViewById(R.id.progress_add_service);
        TextView tvError = view.findViewById(R.id.tvErrorAddService);
        TextView tvSuccess = view.findViewById(R.id.tvSuccessAddService);


        etServiceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSuccess.setVisibility(View.INVISIBLE);
            }
        });
        etServicePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSuccess.setVisibility(View.INVISIBLE);
            }
        });
        etServiceDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSuccess.setVisibility(View.INVISIBLE);
            }
        });

        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvError.setVisibility(View.INVISIBLE);
                tvSuccess.setVisibility(View.INVISIBLE);

                String name = etServiceName.getText().toString().trim();
                String priceStr = etServicePrice.getText().toString().trim();
                String durationStr = etServiceDuration.getText().toString().trim();

                if (name.isEmpty() || priceStr.isEmpty() || durationStr.isEmpty()) { // fill all fields message
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }
                progress.setVisibility(View.VISIBLE);

                try{
                    int price = Integer.parseInt(priceStr);
                    int duration = Integer.parseInt(durationStr);

                    Service newService = Service.create(name, price, duration);
                    // saving to realtime db
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference("settings").child("services").child(newService.getId());

                    ref.setValue(newService).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progress.setVisibility(View.GONE);
                                    tvError.setVisibility(View.INVISIBLE);
                                    tvSuccess.setVisibility(View.VISIBLE);

                                    etServiceName.setText("");
                                    etServicePrice.setText("");
                                    etServiceDuration.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progress.setVisibility(View.GONE);
                                    tvSuccess.setVisibility(View.INVISIBLE);
                                    Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
                catch (Exception e){
                    progress.setVisibility(View.GONE);
                    tvError.setVisibility(View.VISIBLE);
                    Toast.makeText(v.getContext(), "please enter a valid price and duration", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }
}