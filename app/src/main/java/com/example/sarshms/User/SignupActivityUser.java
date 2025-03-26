package com.example.sarshms.User;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignupActivityUser extends AppCompatActivity {

    private EditText etName, etEmail, etUsername, etMobile, etBirthdate, etPassword;
    private Spinner spBloodGroup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI Elements
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etMobile = findViewById(R.id.et_mobile);
        etBirthdate = findViewById(R.id.et_birthdate);
        spBloodGroup = findViewById(R.id.sp_bloodgroup);
        etPassword = findViewById(R.id.et_password);
        Button btnSignup = findViewById(R.id.btn_signup);
        TextView tvLogin = findViewById(R.id.tv_login);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");

        // Set up Blood Group Spinner
        String[] bloodGroups = {"Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups);
        spBloodGroup.setAdapter(adapter);

        // Birthdate Picker
        etBirthdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivityUser.this, (view, year1, month1, dayOfMonth) -> {
                String birthDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                etBirthdate.setText(birthDate);
            }, year, month, day);
            datePickerDialog.show();
        });

        // Button Click Listeners
        btnSignup.setOnClickListener(v -> checkUsernameAndRegister());
        tvLogin.setOnClickListener(v -> startActivity(new Intent(SignupActivityUser.this, LoginActivityUser.class)));
    }

    private void checkUsernameAndRegister() {
        final String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            etUsername.setError("Enter a username");
            etUsername.requestFocus();
            return;
        }

        db.collection("Users").document(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                etUsername.setError("Username already taken");
                etUsername.requestFocus();
            } else {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        final String mobile = etMobile.getText().toString().trim();
        final String birthdate = etBirthdate.getText().toString().trim();
        final String bloodGroup = spBloodGroup.getSelectedItem().toString();
        final String password = etPassword.getText().toString().trim();

        // Input Validations
        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || mobile.isEmpty() || birthdate.isEmpty() || bloodGroup.equals("Select Blood Group") || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }
        if (mobile.length() != 10 || !mobile.matches("\\d{10}")) {
            etMobile.setError("Enter a valid 10-digit mobile number");
            etMobile.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        progressDialog.show();

        // Firebase Authentication (Using Email)
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("username", username);
                            user.put("mobile", mobile);
                            user.put("birthdate", birthdate);
                            user.put("bloodGroup", bloodGroup);
                            user.put("userId", userId);

                            db.collection("Users").document(username).set(user)
                                    .addOnCompleteListener(task1 -> {
                                        progressDialog.dismiss();
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(SignupActivityUser.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignupActivityUser.this, LoginActivityUser.class));
                                            finish();
                                        } else {
                                            Toast.makeText(SignupActivityUser.this, "Error! Try Again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivityUser.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}