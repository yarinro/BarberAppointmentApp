package com.example.barberappointmentapp.ui.barber;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.barberappointmentapp.R;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberSettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BarberSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberSettingsFragment newInstance(String param1, String param2) {
        BarberSettingsFragment fragment = new BarberSettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_barber_settings, container, false);

        //----------------------------------BACK BUTTON-------------------------------------------
        ImageButton btnBack = view.findViewById(R.id.btn_back_barber_settings);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });
        //---------------------------------------------------------------------------

        MaterialButton btnNavGeneralSettings = view.findViewById(R.id.btnNavGeneralSettings);
        MaterialButton btnNavWorkingHours = view.findViewById(R.id.btnNavWorkingHours);
        MaterialButton btnNavServices = view.findViewById(R.id.btnNavServices);
        MaterialButton btnNavTimeOff = view.findViewById(R.id.btnNavTimeOff);

        // button shop settings
        btnNavGeneralSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_barberSettingsFragment_to_barberShopSettingsFragment);
            }
        });
        //button schedule management
        btnNavWorkingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_barberSettingsFragment_to_barberScheduleManagementFragment);
            }
        });
        // button services
        btnNavServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_barberSettingsFragment_to_barberServicesAndPricesFragment);
            }
        });
        // button time offs
        btnNavTimeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_barberSettingsFragment_to_barberHolidaysAndTimeOffsFragment);
            }
        });

        return view;
    }
}