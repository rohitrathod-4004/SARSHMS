package com.example.sarshms.doctors;

import java.util.HashMap;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManagePatientsActivity extends AppCompatActivity {

    private TextView tvUserEmail, tvAppointmentDate;
    private EditText etPrescription, etCustomTest;
    private Spinner spinnerLabTests;
    private Button btnSubmit;
    private CheckBox checkboxAdmit;

    private String hospitalUsername, doctorEmail, userEmail, appointmentDate, userUsername;

    private final String[] labTests = {
            "Blood Test", "X-Ray", "MRI", "CT Scan", "Urine Test", "ECG", "Thyroid Test", "Liver Function Test", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_patients);

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        doctorEmail = getIntent().getStringExtra("doctorEmail");

        tvUserEmail = findViewById(R.id.tv_user_email);
        tvAppointmentDate = findViewById(R.id.tv_appointment_date);
        etPrescription = findViewById(R.id.et_prescription);
        spinnerLabTests = findViewById(R.id.spinner_lab_tests);
        etCustomTest = findViewById(R.id.et_custom_test);
        btnSubmit = findViewById(R.id.btn_submit);
        checkboxAdmit = findViewById(R.id.checkbox_admit);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labTests);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLabTests.setAdapter(adapter);

        spinnerLabTests.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (labTests[pos].equals("Other")) {
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

        fetchTempAppointmentData();

        btnSubmit.setOnClickListener(v -> {
            if (userEmail != null && !userEmail.isEmpty()) {
                savePrescriptionAndLabTest();
            } else {
                Toast.makeText(this, "No patient data found", Toast.LENGTH_SHORT).show();
            }
        });

        checkboxAdmit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (userUsername == null || userUsername.isEmpty()) {
                Toast.makeText(this, "User data missing. Cannot update admission status.", Toast.LENGTH_SHORT).show();
                checkboxAdmit.setChecked(false); // Reset
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("Users").document(userUsername)
                    .update("statusOfAdmission", isChecked ? "Admitted" : "Not Admitted")
                    .addOnSuccessListener(unused -> {
                        String msg = isChecked ? "Patient marked as admitted." : "Admission status removed.";
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update admission status", Toast.LENGTH_SHORT).show();
                    });
        });
    }

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
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch temp appointment", Toast.LENGTH_SHORT).show());
    }

    private void savePrescriptionAndLabTest() {
        String prescriptionText = etPrescription.getText().toString().trim();
        String selectedTest = spinnerLabTests.getSelectedItem().toString();
        String customTest = etCustomTest.getText().toString().trim();

        if (prescriptionText.isEmpty()) {
            Toast.makeText(this, "Please write a prescription", Toast.LENGTH_SHORT).show();
            return;
        }

        String finalLabTest = selectedTest.equals("Other") ? customTest : selectedTest;

        if (finalLabTest.isEmpty()) {
            Toast.makeText(this, "Please enter a custom lab test name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userUsername == null || userUsername.isEmpty()) {
            Toast.makeText(this, "User data missing. Cannot save prescription.", Toast.LENGTH_SHORT).show();
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
                    db.collection("Hospitals").document(hospitalUsername)
                            .collection("Doctors").document(doctorEmail)
                            .collection("Appointments").document("TempAppointment")
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Data submitted and appointment cleared", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ManagePatientsActivity.this, DoctorDashboard.class);
                                intent.putExtra("hospitalId", hospitalUsername);
                                intent.putExtra("doctorEmail", doctorEmail);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Prescription saved, but failed to clear TempAppointment", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit data", Toast.LENGTH_SHORT).show());
    }
}
