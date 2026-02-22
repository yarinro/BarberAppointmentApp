package com.example.barberappointmentapp.ui.main;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.fragment.NavHostFragment;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.Settings;
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.models.User;
import com.example.barberappointmentapp.models.WorkingDay;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private final String ADMIN_ID = "2MDnTYrrupcbAv0bAZ1hm18CWA73";

    public String currentUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
    }

    public void logIn() {
        String email = ((EditText) findViewById(R.id.editText_login_email)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.editText_login_password)).getText().toString();
        TextView errorLogin = ((TextView) findViewById(R.id.errorLogin));
        ProgressBar progressBar = ((ProgressBar) findViewById(R.id.progress_log_in));
        errorLogin.setVisibility(View.INVISIBLE);


        if (email.isEmpty() || password.isEmpty()) {
            errorLogin.setText("Please fill all fields!");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                            // Getting the current user's ID - admin/regular user
                            String uid = mAuth.getCurrentUser().getUid();

                            NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navgraph);

                            // If user is an admin
                            if (uid.equals(ADMIN_ID)) {
                                navFragment.getNavController().navigate(R.id.action_loginFragment_to_barberHomeFragment);
                            // Regular user
                            } else {
                                navFragment.getNavController().navigate(R.id.action_loginFragment_to_clientHomeFragment);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            errorLogin.setText("Login Failed. Please try again");
                            errorLogin.setVisibility(View.VISIBLE);

                            Exception e = task.getException();
                            Toast.makeText(MainActivity.this, "Error: " + (e != null ? e.getMessage() : "unknown error"), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void signUp(){
        EditText etName = ((EditText) findViewById(R.id.editText_signup_name));
        EditText etPhone = ((EditText) findViewById(R.id.editText_signup_phone));
        EditText etEmail = ((EditText) findViewById(R.id.editText_signup_email));
        EditText etPassword = ((EditText) findViewById(R.id.editText_signup_password));

        TextView suErrorFullName = ((TextView) findViewById(R.id.suErrorFullName));
        TextView suErrorPhoneNumber = ((TextView) findViewById(R.id.suErrorPhoneNumber));
        TextView suErrorEmail = ((TextView) findViewById(R.id.suErrorEmail));
        TextView suErrorPassword = ((TextView) findViewById(R.id.suErrorPassword));

        ProgressBar progressBar = ((ProgressBar) findViewById(R.id.progressSignUp));

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        boolean toReturn = false;

        if (name.isEmpty()) {
            suErrorFullName.setText("* Required");
            suErrorFullName.setVisibility(View.VISIBLE);
            toReturn = true;
        }
        else{
            suErrorFullName.setVisibility(View.INVISIBLE);
        }
        if (phone.isEmpty()) {
            suErrorPhoneNumber.setText("* Required");
            suErrorPhoneNumber.setVisibility(View.VISIBLE);
            toReturn = true;
        }
        // https://developer.android.com/reference/android/util/Patterns#EMAIL_ADDRESS
        else if (!phone.matches("\\d+")){
            suErrorPhoneNumber.setText("Phone number must contain digits only");
            suErrorPhoneNumber.setVisibility(View.VISIBLE);
            toReturn = true;
        }
        else{
            suErrorPhoneNumber.setVisibility(View.INVISIBLE);
        }
        if(email.isEmpty()){
            suErrorEmail.setText("* Required");
            suErrorEmail.setVisibility(View.VISIBLE);
            toReturn = true;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            suErrorEmail.setText("Invalid email");
            suErrorEmail.setVisibility(View.VISIBLE);
            toReturn = true;
        }
        else{
            suErrorEmail.setVisibility(View.INVISIBLE);
        }
        if(password.isEmpty()){
            suErrorPassword.setText("* Required");
            suErrorPassword.setVisibility(View.VISIBLE);
            toReturn = true;
        }
        else if (password.length() < 6){
            suErrorPassword.setText("Password must be at least 6 characters");
            suErrorPassword.setVisibility(View.VISIBLE);
            toReturn = true;
        }
        else{
            suErrorPassword.setVisibility(View.INVISIBLE);
        }
        if (toReturn) return;


        progressBar.setVisibility(View.VISIBLE);

        suErrorFullName.setVisibility(View.INVISIBLE);
        suErrorPhoneNumber.setVisibility(View.INVISIBLE);
        suErrorEmail.setVisibility(View.INVISIBLE);
        suErrorPassword.setVisibility(View.INVISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            if (user == null) {
                                Toast.makeText(MainActivity.this, "Sign up failed (no user).", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String uid = user.getUid();
                            User newUser = new User(uid, name, email, phone);
                            // Write to Realtime DB
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference ref = db.getReference("users").child(uid);

                            ref.setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Signed up successfully.", Toast.LENGTH_SHORT).show();

                                    NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navgraph);
                                    navFragment.getNavController().navigate(R.id.action_clientSignUpFragment_to_welcomeFragment);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "DB write failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Exception e = task.getException();
                            Toast.makeText(MainActivity.this, "Sign up failed: " + (e != null ? e.getClass().getSimpleName() + " - " + e.getMessage() : "unknown"), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // https://firebase.google.com/docs/auth/android/password-auth#next_steps
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    // creates default settings in realtime db. if fails user is signed out and navigates back to welcome fragment
//    public void createDefaultSettingsInDB(){
//        NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navgraph);
//        Settings defaultSettings = new Settings();
//
//        defaultSettings.setBarbershopName("Add barbershop name in \"Settings\"");
//        defaultSettings.setAddress("\"Add address in \\\"Settings\\\"");
//        defaultSettings.setPhoneNumber("Add phone number in \"Settings\"");
//        defaultSettings.setAboutUs("Edit description in \"Settings\"");
//        defaultSettings.setMaxDaysAheadToBookAppointment(14);
//
//        Map<String, WorkingDay> workingDaysMap = new HashMap<>();
//        defaultSettings.setServices(new HashMap<>()); // default - no services yet
//        defaultSettings.setTimeOffs(new ArrayList<>()); // default - no time offs yet
//
//        // default working days -
//        for (int i = 1; i <= 7; i++) {
//            boolean isOpen = (i != 1 && i != 7); // closed on sunday and saturday
//            WorkingDay workingday = new WorkingDay(i, isOpen, 540, 1020, null); // 9:00 to 17:00
//
//            workingDaysMap.put(String.valueOf(i), workingday);
//        }
//        defaultSettings.setWorkingDays(workingDaysMap);
//
//        // adding default settings to realtime db
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference settingsRef = db.getReference("settings");
//
//        settingsRef.setValue(defaultSettings)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(MainActivity.this, "Initialized Default Settings successfully", Toast.LENGTH_SHORT).show();
//                        navFragment.getNavController().navigate(R.id.action_loginFragment_to_barberHomeFragment);                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MainActivity.this, "Failed to init settings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        navFragment.getNavController().navigate(R.id.);
//                    }
//                });
//    }

}