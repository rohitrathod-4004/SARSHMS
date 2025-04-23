package com.example.sarshms.doctors;

import static android.content.Intent.getIntent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GeneratePrescriptionActivity extends AppCompatActivity {
    private EditText editTextSymptoms, editTextDiagnosis, editTextMedicines, editTextNotes;
    private Button btnSubmitPrescription;
    private FirebaseFirestore db;
    private String patientUsername ,hospitalUsername , doctorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_prescription);

        editTextSymptoms = findViewById(R.id.editTextSymptoms);
        editTextDiagnosis = findViewById(R.id.editTextDiagnosis);
        editTextMedicines = findViewById(R.id.editTextMedicines);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnSubmitPrescription = findViewById(R.id.btnSubmitPrescription);
        db = FirebaseFirestore.getInstance();

        patientUsername = getIntent().getStringExtra("patientUsername");
        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        doctorEmail = getIntent().getStringExtra("doctorEmail");


        btnSubmitPrescription.setOnClickListener(v -> {
            String symptoms = editTextSymptoms.getText().toString();
            String diagnosis = editTextDiagnosis.getText().toString();
            String medicines = editTextMedicines.getText().toString();
            String notes = editTextNotes.getText().toString();

            // Submit new prescription
            submitPrescription(symptoms, diagnosis, medicines, notes);
        });
    }

    private void submitPrescription(String symptoms, String diagnosis, String medicines, String notes) {
        Map<String, Object> prescription = new HashMap<>();
        prescription.put("symptoms", symptoms);
        prescription.put("diagnosis", diagnosis);
        prescription.put("medicines", medicines);
        prescription.put("notes", notes);
        prescription.put("timestamp", FieldValue.serverTimestamp());

        // 1. Save to hospital side
        db.collection("Hospitals")
                .document(hospitalUsername)
                .collection("AdmittedPatients")
                .document(patientUsername)
                .collection("Prescriptions")
                .add(prescription)
                .addOnSuccessListener(documentReference -> {
                    // 2. Save to user side
                    db.collection("Users")
                            .document(patientUsername)
                            .collection("Hospitals")
                            .document(hospitalUsername)
                            .collection("Prescriptions")
                            .add(prescription)
                            .addOnSuccessListener(ref -> {
                                // Both saved successfully
                                finish(); // Close the activity
                            });
                });
    }

}

