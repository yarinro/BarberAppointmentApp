package com.example.barberappointmentapp.ui.barber;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.ui.main.MainActivity;

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
        // Sign out button
        Button btnSignOut = view.findViewById(R.id.btn_barber_sign_out);
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


        return view;
    }
}