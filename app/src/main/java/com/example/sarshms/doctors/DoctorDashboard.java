package com.example.sarshms.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.HospitalStaff.LoginActivityStaff;
import com.example.sarshms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorDashboard extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String hospitalUsername, doctorEmail;
    private TextView tvWelcomeDoctor;

    private static final String TAG = "DoctorDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_doctor);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get data from Intent
        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        doctorEmail = getIntent().getStringExtra("doctorEmail");

        // UI Elements
        tvWelcomeDoctor = findViewById(R.id.tv_welcome_doctor);
        Button btnManagePatients = findViewById(R.id.btn_manage_patients);
        Button btnManageAppointments = findViewById(R.id.btn_manage_appointments);
        Button btnManagePrescriptions = findViewById(R.id.btn_manage_prescriptions);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Fetch doctor details
        fetchDoctorDetails();

        // Button Listeners
        btnManagePatients.setOnClickListener(v -> openManagePage("Patients"));
        btnManageAppointments.setOnClickListener(v -> openManagePage("Appointments"));
        btnManagePrescriptions.setOnClickListener(v -> openManagePage("Prescriptions"));

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(DoctorDashboard.this, LoginActivityStaff.class));
            finish();
        });
    }

    private void fetchDoctorDetails() {
        if (hospitalUsername == null || hospitalUsername.isEmpty() || doctorEmail == null || doctorEmail.isEmpty()) {
            Log.e(TAG, "fetchDoctorDetails: Invalid hospitalUsername or doctorEmail");
            return;
        }

        db.collection("Hospitals").document(hospitalUsername)
                .collection("Doctors").document(doctorEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String doctorName = documentSnapshot.getString("doctorName");
                        tvWelcomeDoctor.setText("Welcome, Dr. " + doctorName + "!");
                    } else {
                        Toast.makeText(this, "Doctor profile not found!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching doctor details", e);
                    Toast.makeText(this, "Error fetching doctor details", Toast.LENGTH_LONG).show();
                });
    }

    private void openManagePage(String type) {
//        Intent intent = new Intent(DoctorDashboard.this, ManageDoctorSectionActivity.class);
//        intent.putExtra("type", type);
//        intent.putExtra("hospitalUsername", hospitalUsername);
//        intent.putExtra("doctorEmail", doctorEmail);
//        startActivity(intent);
    }
}
