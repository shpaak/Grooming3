package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.Admin;
import com.example.myapplication.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private TextView joinButton;
    private Button loginButton;
    private ProgressDialog loadingBar;
    FirebaseAuth uAuth;
    private SharedPreferences loginPreferences;  // Declare SharedPreferences
    private SharedPreferences.Editor loginPrefsEditor;  // Declare SharedPreferences Editor
    private Boolean rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinButton = findViewById(R.id.main_join_button);
        loginButton = (Button) findViewById(R.id.main_login_button);
        loadingBar = new ProgressDialog(this);

        uAuth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        // Retrieve the rememberMe value
        rememberMe = loginPreferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            String email = loginPreferences.getString("email", "");
            String password = loginPreferences.getString("password", "");
            signInUser(email, password);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginintent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginintent);
            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerintent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerintent);
            }
        });
    }

    private void signInUser(String email, String password) {
        // Show a loading bar
        loadingBar.setTitle("Logging In");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // Sign in the user using FirebaseAuth
        uAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Save the authentication state in SharedPreferences
                            loginPrefsEditor.putBoolean("rememberMe", true);
                            loginPrefsEditor.putString("email", email);
                            loginPrefsEditor.putString("password", password);
                            loginPrefsEditor.apply();

                            // User logged in successfully, navigate to the desired activity
                            Intent homeIntent = new Intent(MainActivity.this, UserHomeActivity.class);
                            startActivity(homeIntent);
                            finish();  // Finish the current activity
                        } else {
                            // Hide the loading bar and display an error message
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


