package com.example.sarshms.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManagePatientsActivity extends AppCompatActivity {

    private TextView tvUserEmail, tvAppointmentDate;
    private EditText etPrescription, etCustomTest;
    private Spinner spinnerLabTests;
    private Button btnSubmit;
    private CheckBox checkboxAdmit;

    private String hospitalUsername, doctorEmail, userEmail, appointmentDate, userUsername;

    private final String[] labTests = {
            "Blood Test", "X-Ray", "MRI", "CT Scan", "Urine Test",
            "ECG", "Thyroid Test", "Liver Function Test", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_patients);

        // Get passed intent values
        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        doctorEmail = getIntent().getStringExtra("doctorEmail");

        // Initialize Views
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvAppointmentDate = findViewById(R.id.tv_appointment_date);
        etPrescription = findViewById(R.id.et_prescription);
        etCustomTest = findViewById(R.id.et_custom_test);
        spinnerLabTests = findViewById(R.id.spinner_lab_tests);
        btnSubmit = findViewById(R.id.btn_submit);
        checkboxAdmit = findViewById(R.id.checkbox_admit);

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labTests);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLabTests.setAdapter(adapter);

        spinnerLabTests.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (labTests[position].equals("Other")) {
                    etCustomTest.setVisibility(View.VISIBLE);
                } else {
                    etCustomTest.setVisibility(View.GONE);
                    etCustomTest.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                etCustomTest.setVisibility(View.GONE);
            }
        });

        // Fetch appointment data
        fetchTempAppointmentData();

        // Submit button click
        btnSubmit.setOnClickListener(v -> {
            if (userEmail != null && !userEmail.isEmpty()) {
                savePrescriptionAndLabTest();
            } else {
                Toast.makeText(this, "No patient data found", Toast.LENGTH_SHORT).show();
            }
        });

        // Admit checkbox logic
        checkboxAdmit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (userUsername == null || userUsername.isEmpty()) {
                Toast.makeText(this, "User data missing. Cannot update admission status.", Toast.LENGTH_SHORT).show();
                checkboxAdmit.setChecked(false);
                return;
            }

            String status = isChecked ? "Admitted" : "Not Admitted";

            // 1. Update in Users collection
            FirebaseFirestore.getInstance()
                    .collection("Users").document(userUsername)
                    .update("statusOfAdmission", status)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Status updated: " + status, Toast.LENGTH_SHORT).show();

                        // 2. If Admitted, create Admission Request
                        if (isChecked) {
                            Map<String, Object> admitRequest = new HashMap<>();
                            admitRequest.put("userEmail", userEmail);
                            admitRequest.put("username", userUsername);
                            admitRequest.put("doctorEmail", doctorEmail);
                            admitRequest.put("admissionStatus", "Requested");
                            admitRequest.put("timestamp", System.currentTimeMillis());

                            FirebaseFirestore.getInstance()
                                    .collection("Hospitals").document(hospitalUsername)
                                    .collection("AdmissionRequests")
                                    .document(userUsername)
                                    .set(admitRequest)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Admission request sent", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to send admission request", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Optionally delete any existing admission request if unchecked
                            FirebaseFirestore.getInstance()
                                    .collection("Hospitals").document(hospitalUsername)
                                    .collection("AdmissionRequests")
                                    .document(userUsername)
                                    .delete();
                        }

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update admission status", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    // Fetch appointment details
    private void fetchTempAppointmentData() {
        FirebaseFirestore.getInstance()
                .collection("Hospitals").document(hospitalUsername)
                .collection("Doctors").document(doctorEmail)
                .collection("Appointments").document("TempAppointment")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userEmail = documentSnapshot.getString("userEmail");
                        userUsername = documentSnapshot.getString("username");
                        appointmentDate = documentSnapshot.getString("date");

                        tvUserEmail.setText("User Email: " + userEmail);
                        tvAppointmentDate.setText("Appointment Date: " + appointmentDate);
                    } else {
                        Toast.makeText(this, "No active patient appointment", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch appointment data", Toast.LENGTH_SHORT).show());
    }

    // Save prescription & lab test
    private void savePrescriptionAndLabTest() {
        String prescriptionText = etPrescription.getText().toString().trim();
        String selectedTest = spinnerLabTests.getSelectedItem().toString();
        String customTest = etCustomTest.getText().toString().trim();
        String finalLabTest = selectedTest.equals("Other") ? customTest : selectedTest;

        if (prescriptionText.isEmpty()) {
            Toast.makeText(this, "Please enter a prescription", Toast.LENGTH_SHORT).show();
            return;
        }

        if (finalLabTest.isEmpty()) {
            Toast.makeText(this, "Please enter or select a lab test", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userUsername == null || userUsername.isEmpty()) {
            Toast.makeText(this, "Missing user data", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("prescription", prescriptionText);
        data.put("labTest", finalLabTest);
        data.put("doctorEmail", doctorEmail);
        data.put("appointmentDate", appointmentDate);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userUsername)
                .collection("Prescriptions")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    // Clear temp appointment
                    db.collection("Hospitals").document(hospitalUsername)
                            .collection("Doctors").document(doctorEmail)
                            .collection("Appointments").document("TempAppointment")
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Prescription submitted", Toast.LENGTH_SHORT).show();
                                // Redirect to doctor dashboard
                                Intent intent = new Intent(ManagePatientsActivity.this, DoctorDashboard.class);
                                intent.putExtra("hospitalId", hospitalUsername);
                                intent.putExtra("doctorEmail", doctorEmail);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Prescription saved, but failed to clear temp appointment", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit prescription", Toast.LENGTH_SHORT).show());
    }
}
