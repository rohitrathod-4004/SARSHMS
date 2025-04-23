package com.example.sarshms.doctors;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewPrescriptionActivity extends AppCompatActivity {
    private TextView textViewSymptoms, textViewDiagnosis, textViewMedicines, textViewNotes;
    private Button btnBack;
    private FirebaseFirestore db;
    private String hospitalUsername, patientUsername, prescriptionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_prescription);

        // Initialize UI components
        textViewSymptoms = findViewById(R.id.textViewSymptoms);
        textViewDiagnosis = findViewById(R.id.textViewDiagnosis);
        textViewMedicines = findViewById(R.id.textViewMedicines);
        textViewNotes = findViewById(R.id.textViewNotes);
        btnBack = findViewById(R.id.btnBack);
        db = FirebaseFirestore.getInstance();

        // Get data from Intent
        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        patientUsername = getIntent().getStringExtra("patientUsername");
        prescriptionId = getIntent().getStringExtra("prescriptionId");

        // Fetch prescription details
        fetchPrescriptionDetails();

        // Back Button
        btnBack.setOnClickListener(v -> {
            finish(); // Go back to the previous activity
        });
    }

    private void fetchPrescriptionDetails() {
        db.collection("Hospitals")
                .document(hospitalUsername)
                .collection("AdmittedPatients")
                .document(patientUsername)
                .collection("Prescriptions")
                .document(prescriptionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get prescription data
                        com.example.sarshms.doctors.Prescription prescription = documentSnapshot.toObject(com.example.sarshms.doctors.Prescription.class);

                        // Set values to the UI components
                        textViewSymptoms.setText("Symptoms: " + prescription.getSymptoms());
                        textViewDiagnosis.setText("Diagnosis: " + prescription.getDiagnosis());
                        textViewMedicines.setText("Medicines: " + prescription.getMedicines());
                        textViewNotes.setText("Additional Notes: " + prescription.getNotes());
                    }
                });
    }
}

