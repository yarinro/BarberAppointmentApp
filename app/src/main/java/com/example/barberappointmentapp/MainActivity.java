package com.example.barberappointmentapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.fragment.NavHostFragment;

import com.example.barberappointmentapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private final String ADMIN_ID = "2MDnTYrrupcbAv0bAZ1hm18CWA73";
    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");


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

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                            // Getting the current user's ID - admin/regular user
                            String uid = mAuth.getCurrentUser().getUid();

                            NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navgraph);
                            // Erase back stack in order to stay on
                            //NavOptions options = new NavOptions.Builder().setPopUpTo(R.id.welcomeFragment, true).build();

                            // If user is an admin
                            if (uid.equals(ADMIN_ID)) {
                                navFragment.getNavController().navigate(R.id.action_loginFragment_to_barberHomeFragment);
                            // Regular user
                            } else {
                                navFragment.getNavController().navigate(R.id.action_loginFragment_to_clientHomeFragment);
                            }
                        } else {
                            Exception e = task.getException();
                            Toast.makeText(MainActivity.this, "Login failed: " + (e != null ? e.getMessage() : "unknown error"), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateSignUpInput(EditText etName, EditText etPhone, EditText etEmail, EditText etPassword){
        etName.setError(null);
        etPhone.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        boolean valid = true;
        View firstInvalid = null;

        if (name.isEmpty()) {
            etName.setError("*");
            valid = false;
            firstInvalid = etName;
        }

        if (phone.isEmpty()) {
            etPhone.setError("*");
            valid = false;
            if (firstInvalid == null) firstInvalid = etPhone;
        } else if (!phone.matches("\\d+")) {
            etPhone.setError("Phone number must contain digits only");
            valid = false;
            if (firstInvalid == null) firstInvalid = etPhone;
        }

        if (email.isEmpty()) {
            etEmail.setError("*");
            valid = false;
            if (firstInvalid == null) firstInvalid = etEmail;
        } else if (!email.matches(".*\\.[A-Za-z]{2,}$")) {
                etEmail.setError("Please enter a valid email address");
                valid = false;
                if (firstInvalid == null) firstInvalid = etEmail;
            }

        if (password.isEmpty()) {
            etPassword.setError("*");
            valid = false;
            if (firstInvalid == null) firstInvalid = etPassword;
        } else if (password.length() < 6) {
            etPassword.setError("Password should be at least 6 characters long");
            valid = false;
            if (firstInvalid == null) firstInvalid = etPassword;
        }

        if (!valid && firstInvalid != null) {
            firstInvalid.requestFocus();
        }

        return valid;
    }

    public void signUp(){
        EditText etName = ((EditText) findViewById(R.id.editText_signup_name));
        EditText etPhone = ((EditText) findViewById(R.id.editText_signup_phone));
        EditText etEmail = ((EditText) findViewById(R.id.editText_signup_email));
        EditText etPassword = ((EditText) findViewById(R.id.editText_signup_password));

        if (!validateSignUpInput(etName, etPhone, etEmail, etPassword)) return;

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();


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

                            User newUser = new User(uid, name, email, phone, System.currentTimeMillis());

                            // Write to Realtime DB
                            usersRef.child(uid).setValue(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(MainActivity.this, "Signed up successfully.", Toast.LENGTH_SHORT).show();
                                        NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navgraph);
                                        navFragment.getNavController().navigate(R.id.action_clientSignUpFragment_to_welcomeFragment);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(MainActivity.this, "DB write failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            Exception e = task.getException();
                            Toast.makeText(MainActivity.this, "Sign up failed: " + (e != null ? e.getClass().getSimpleName() + " - " + e.getMessage() : "unknown"), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}