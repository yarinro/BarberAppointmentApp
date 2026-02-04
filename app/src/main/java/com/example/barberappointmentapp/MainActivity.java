package com.example.barberappointmentapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private final String ADMIN_ID = "2MDnTYrrupcbAv0bAZ1hm18CWA73";
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

    public void login() {
        String email = ((EditText) findViewById(R.id.editText_login_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.editText_login_password)).getText().toString();

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
                            }
                            else {
                                navFragment.getNavController().navigate(R.id.action_loginFragment_to_clientHomeFragment);
                            }
                        }

                        else {
                            Toast.makeText(MainActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}