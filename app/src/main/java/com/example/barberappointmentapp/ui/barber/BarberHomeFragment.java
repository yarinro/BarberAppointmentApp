package com.example.barberappointmentapp.ui.barber;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.barberappointmentapp.R;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_barber_home, container, false);

        //----------------------------------------------BUTTONS----------------------------------------------------------------------------
        Button btnViewAppointments = view.findViewById(R.id.btn_barber_view_appointments);
        btnViewAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_barberHomeFragment_to_barberAppointmentsFragment);
            }
        });

        Button btnGallery = view.findViewById(R.id.btn_barber_gallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_barberHomeFragment_to_galleryFragment);
            }
        });

        Button btnUploadToGallery = view.findViewById(R.id.btn_barber_upload_to_gallery);
        btnUploadToGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_barberHomeFragment_to_barberUploadToGalleryFragment);
            }
        });

        Button btnReviews = view.findViewById(R.id.btn_barber_reviews);
        btnReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_barberHomeFragment_to_reviewsFragment);
            }
        });

        Button scheduleManagement = view.findViewById(R.id.btn_barber_schedule_management);
        scheduleManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_barberHomeFragment_to_barberScheduleManagementFragment);
            }
        });

        Button servicesAndPrices = view.findViewById(R.id.btn_barber_services_and_prices);
        servicesAndPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_barberHomeFragment_to_barberServicesAndPricesFragment);
            }
        });
        //--------------------------------------------------------------------------------------------------------------------------

        return view;
    }
}