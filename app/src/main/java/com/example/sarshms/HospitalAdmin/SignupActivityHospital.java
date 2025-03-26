package com.example.sarshms.HospitalAdmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivityHospital extends AppCompatActivity {

    private EditText etHospitalName, etRegNumber, etEmail, etUsername, etPassword;
    private FirebaseAuth mAuth;
    private CollectionReference hospitalsRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_hospital);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        hospitalsRef = db.collection("Hospitals");

        // UI Elements
        etHospitalName = findViewById(R.id.et_hospital_name);
        etRegNumber = findViewById(R.id.et_reg_number);
        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnSignup = findViewById(R.id.btn_signup);
        TextView tvLogin = findViewById(R.id.tv_login);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");

        // Button Click Listeners
        btnSignup.setOnClickListener(v -> registerHospitalAdmin());
        tvLogin.setOnClickListener(v -> startActivity(new Intent(SignupActivityHospital.this, LoginActivityHospital.class)));
    }

    private void registerHospitalAdmin() {
        final String hospitalName = etHospitalName.getText().toString().trim();
        final String regNumber = etRegNumber.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        // Input Validations
        if (hospitalName.isEmpty() || regNumber.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        progressDialog.show();

        // Check if username is already taken
        hospitalsRef.document(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    progressDialog.dismiss();
                    etUsername.setError("Username already exists! Choose another one.");
                    etUsername.requestFocus();
                } else {
                    createHospitalAdmin(hospitalName, regNumber, email, username, password);
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(SignupActivityHospital.this, "Error! Try Again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createHospitalAdmin(String hospitalName, String regNumber, String email, String username, String password) {
        // Firebase Authentication (Using Email & Password)
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Store hospital details in Firestore under "Hospitals" collection
                            Map<String, Object> hospitalData = new HashMap<>();
                            hospitalData.put("hospitalName", hospitalName);
                            hospitalData.put("registrationNumber", regNumber);
                            hospitalData.put("email", email);
                            hospitalData.put("username", username);
                            hospitalData.put("userId", firebaseUser.getUid());

                            hospitalsRef.document(username).set(hospitalData)
                                    .addOnCompleteListener(task1 -> {
                                        progressDialog.dismiss();
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(SignupActivityHospital.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignupActivityHospital.this, LoginActivityHospital.class));
                                            finish();
                                        } else {
                                            Toast.makeText(SignupActivityHospital.this, "Error! Try Again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivityHospital.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
