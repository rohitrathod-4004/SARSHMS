package com.example.sarshms.HospitalAdmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivityHospital extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_hospital);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI Elements
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvSignup = findViewById(R.id.tv_signup);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        // Button Listeners
        btnLogin.setOnClickListener(v -> loginHospitalAdmin());
        tvSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivityHospital.this, SignupActivityHospital.class)));
    }

    private void loginHospitalAdmin() {
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        // Input Validations
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Check if username exists in Firestore
        db.collection("Hospitals").document(username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        String email = document.getString("email");

                        if (email != null) {
                            // Authenticate with FirebaseAuth
                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(authTask -> {
                                        progressDialog.dismiss();
                                        if (authTask.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                Toast.makeText(LoginActivityHospital.this, "Login Successful of Login ACtivity!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginActivityHospital.this, DashboardActivityHospital.class));
                                                finish();

                                            }
                                        } else {
                                            Toast.makeText(LoginActivityHospital.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivityHospital.this, "Error fetching user data!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivityHospital.this, "Username does not exist!of Login", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
