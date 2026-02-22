package com.example.barberappointmentapp.ui.client;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
import com.example.barberappointmentapp.models.Settings;
import com.example.barberappointmentapp.models.User;
import com.example.barberappointmentapp.ui.main.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClientHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean userNameLoaded = false, settingsLoaded = false;


    public ClientHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientHomeFragment newInstance(String param1, String param2) {
        ClientHomeFragment fragment = new ClientHomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_client_home, container, false);

        TextView tvDisplayShopName = view.findViewById(R.id.tv_display_shop_name);
        TextView tvDisplayAddress = view.findViewById(R.id.tv_display_address);
        TextView tvDisplayPhone = view.findViewById(R.id.tv_display_phone);
        TextView tvDisplayAboutUs = view.findViewById(R.id.tv_display_about_us);
        TextView tvOpeningHours = view.findViewById(R.id.tv_opening_hours);
        LinearLayout clientHomeLayout = view.findViewById(R.id.clientHomeLayout);
        ProgressBar progressBar = view.findViewById(R.id.clientHomeProgress);
        Button btnMyAppointments = view.findViewById(R.id.btn_client_my_appointments);
        ImageButton btnSignOut = view.findViewById(R.id.btn_client_sign_out);


        progressBar.setVisibility(View.VISIBLE);
        clientHomeLayout.setVisibility(View.INVISIBLE);

        //--------------------------title-------------------------------------
        TextView tvTitle = view.findViewById(R.id.client_home_title);
        tvTitle.setText("");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String clientUid;
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (user != null) {
            clientUid = user.getUid();
            DatabaseReference ref = database.getReference("users").child(clientUid);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    String clientName = user.getName();
                    tvTitle.setText("Hello, " + clientName);
                    userNameLoaded = true;
                    checkDataLoaded(progressBar, clientHomeLayout);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    clientHomeLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Failed to get user name: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            progressBar.setVisibility(View.GONE);
            clientHomeLayout.setVisibility(View.VISIBLE);
        }

        //----------------------------------------------BUTTONS----------------------------------------------------------------------------
        // Sign out button
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
                                Navigation.findNavController(view).navigate(R.id.action_clientHomeFragment_to_welcomeFragment);
                            }
                        })
                        .setNegativeButton("No", null) // click no -> listener=null -> close the dialog
                        .show();
            }
        });


        Button btnBookAppointment = view.findViewById(R.id.btn_client_book_appointment);
        btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_clientHomeFragment_to_clientBookAppointmentFragment);
            }
        });

        btnMyAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_clientHomeFragment_to_clientMyAppointmentsFragment);
            }
        });
        //--------------------------------------------------------------------------------------------------------------------------



        DatabaseReference settingsRef = database.getReference("settings");
        settingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Settings settings = snapshot.getValue(Settings.class);
                if (settings != null) {
                    if (settings.getBarbershopName() != null) tvDisplayShopName.setText(settings.getBarbershopName());
                    if (settings.getAddress() != null) tvDisplayAddress.setText("\uD83D\uDCCD" + settings.getAddress());
                    if (settings.getPhoneNumber() != null) tvDisplayPhone.setText("\uD83D\uDCDE" + settings.getPhoneNumber());
                    if (settings.getAboutUs() != null) tvDisplayAboutUs.setText(settings.getAboutUs());
                    if (settings.getWorkingDays() != null) tvOpeningHours.setText(settings.getOpeningHours());

                    settingsLoaded = true;
                    checkDataLoaded(progressBar, clientHomeLayout);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    clientHomeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                clientHomeLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Failed to load settings from db" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (userNameLoaded && settingsLoaded) {
            progressBar.setVisibility(View.GONE);
            clientHomeLayout.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void checkDataLoaded(ProgressBar pb, View layout) {
        if (userNameLoaded && settingsLoaded) {
            pb.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        }
    }
}

