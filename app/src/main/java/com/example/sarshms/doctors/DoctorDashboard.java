package com.example.sarshms.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.HospitalStaff.LoginActivityStaff;
import com.example.sarshms.R;
import com.example.sarshms.doctors.ManagePatientsActivity; // 👈 Make sure this is the correct import
import com.google.firebase.auth.FirebaseAuth;
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
        hospitalUsername = getIntent().getStringExtra("hospitalId");
        doctorEmail = getIntent().getStringExtra("doctorEmail");


        // UI Elements
        tvWelcomeDoctor = findViewById(R.id.tv_welcome_doctor);
        Button btnManagePatients = findViewById(R.id.btn_manage_patients);
        Button btnManageAppointments = findViewById(R.id.btn_manage_appointments);
        Button btnManagePrescriptions = findViewById(R.id.btn_manage_prescriptions);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Fetch doctor details
        fetchDoctorDetails(hospitalUsername , doctorEmail);

        // Button Listeners
        btnManagePatients.setOnClickListener(v -> openManagePage("Patients",hospitalUsername , doctorEmail));
        btnManageAppointments.setOnClickListener(v -> openManagePage("Appointments",hospitalUsername , doctorEmail));
        btnManagePrescriptions.setOnClickListener(v -> openManagePage("Prescriptions",hospitalUsername , doctorEmail));
        Button btnManageAdmittedPatients = findViewById(R.id.btnManageAdmittedPatients);

        btnManageAdmittedPatients.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorDashboard.this, ManageAdmittedPatientsActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            intent.putExtra("doctorEmail", doctorEmail);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(DoctorDashboard.this, LoginActivityStaff.class));
            finish();
        });
    }

    private void fetchDoctorDetails(String hospitalUsername , String doctorEmail ) {
        if (hospitalUsername == null || doctorEmail == null ||
                hospitalUsername.isEmpty() || doctorEmail.isEmpty()) {
            Log.e(TAG, "fetchDoctorDetails: Invalid hospitalUsername or doctorEmail");
            return;
        }

        db.collection("Hospitals").document(hospitalUsername)
                .collection("Doctors").document(doctorEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String doctorName = documentSnapshot.getString("email");
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

    private void openManagePage(String type , String hospitalUsername , String doctorEmail) {
        Intent intent;
        Toast.makeText(this, hospitalUsername,  Toast.LENGTH_LONG).show();
        switch (type) {
            case "Patients":
                intent = new Intent(DoctorDashboard.this, ManagePatientsActivity.class);

                break;

            case "Appointments":
                // intent = new Intent(DoctorDashboard.this, ManageAppointmentsActivity.class);
                Toast.makeText(this, "Coming Soon: Manage Appointments", Toast.LENGTH_SHORT).show();
                return;

            case "Prescriptions":
                // intent = new Intent(DoctorDashboard.this, ManagePrescriptionsActivity.class);
                Toast.makeText(this, "Coming Soon: Manage Prescriptions", Toast.LENGTH_SHORT).show();
                return;

            default:
                Toast.makeText(this, "Invalid section!", Toast.LENGTH_SHORT).show();
                return;
        }

        intent.putExtra("hospitalUsername", hospitalUsername);
        intent.putExtra("doctorEmail", doctorEmail);
        startActivity(intent);
    }
}
