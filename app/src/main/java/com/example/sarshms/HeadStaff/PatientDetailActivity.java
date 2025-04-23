package com.example.sarshms.HeadStaff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sarshms.R;
import com.google.firebase.firestore.*;

public class PatientDetailActivity extends AppCompatActivity {

    private TextView tvUsername, tvDoctor, tvRoomBed;
    private EditText etDoctor;
    private Button btnChangeDoctor, btnViewReports;
    private String hospitalUsername, patientUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail_headstaff);  // Updated XML file name

        tvUsername = findViewById(R.id.tv_username);
        tvDoctor = findViewById(R.id.tv_doctor);
        tvRoomBed = findViewById(R.id.tv_room_bed);
        etDoctor = findViewById(R.id.et_doctor);
        btnChangeDoctor = findViewById(R.id.btn_change_doctor);
        btnViewReports = findViewById(R.id.btn_view_reports);

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        patientUsername = getIntent().getStringExtra("patientUsername");

        fetchPatientDetails();

        btnChangeDoctor.setOnClickListener(v -> {
            if (etDoctor.getVisibility() == View.GONE) {
                etDoctor.setVisibility(View.VISIBLE);
            } else {
                updateDoctor();
            }
        });

        btnViewReports.setOnClickListener(v -> {
            // Handle view reports, can be a new Activity or Dialog
            Intent intent = new Intent(PatientDetailActivity.this, PatientReportsActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            intent.putExtra("patientUsername", patientUsername);
            startActivity(intent);
        });
    }

    private void fetchPatientDetails() {
        FirebaseFirestore.getInstance()
                .collection("Hospitals")
                .document(hospitalUsername)
                .collection("AdmittedPatients")
                .document(patientUsername)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String doctor = documentSnapshot.getString("doctorEmail");
                        String room = documentSnapshot.getString("room");
                        String bed = documentSnapshot.getString("bed");

                        tvUsername.setText("Patient: " + username);
                        tvDoctor.setText("Doctor: " + doctor);
                        tvRoomBed.setText("Room: " + room + ", Bed: " + bed);

                        etDoctor.setText(doctor);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PatientDetailActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateDoctor() {
        String newDoctor = etDoctor.getText().toString();
        if (!newDoctor.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("Hospitals")
                    .document(hospitalUsername)
                    .collection("AdmittedPatients")
                    .document(patientUsername)
                    .update("doctorEmail", newDoctor)
                    .addOnSuccessListener(aVoid -> {
                        tvDoctor.setText("Doctor: " + newDoctor);
                        etDoctor.setVisibility(View.GONE);
                        Toast.makeText(PatientDetailActivity.this, "Doctor updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PatientDetailActivity.this, "Failed to update doctor", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(PatientDetailActivity.this, "Doctor name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
