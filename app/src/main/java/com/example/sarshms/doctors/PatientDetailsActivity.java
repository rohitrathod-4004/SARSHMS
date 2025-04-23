package com.example.sarshms.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PatientDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPrescriptions;
    private FirebaseFirestore db;
    private List<com.example.sarshms.doctors.Prescription> prescriptionList;
    private PrescriptionAdapter prescriptionAdapter;
    private String patientUsername , hospitalUsername , doctorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        recyclerViewPrescriptions = findViewById(R.id.recyclerViewPrescriptions);
        db = FirebaseFirestore.getInstance();


        prescriptionList = new ArrayList<>();
        prescriptionAdapter = new PrescriptionAdapter(prescriptionList);

        recyclerViewPrescriptions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPrescriptions.setAdapter(prescriptionAdapter);

        patientUsername = getIntent().getStringExtra("patientUsername");
        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        doctorEmail = getIntent().getStringExtra("doctorEmail");

        // Fetch prescriptions for the patient
        fetchPrescriptionsForPatient();

        // Floating action button to generate a new prescription
        findViewById(R.id.fabGeneratePrescription).setOnClickListener(v -> {
            Intent intent = new Intent(PatientDetailsActivity.this, GeneratePrescriptionActivity.class);
            intent.putExtra("patientUsername", patientUsername);
            intent.putExtra("hospitalUsername",hospitalUsername);
            intent.putExtra("doctorEmail" ,doctorEmail);
            startActivity(intent);
        });
    }

    private void fetchPrescriptionsForPatient() {
        db.collection("Hospitals")
                .document(hospitalUsername)
                .collection("AdmittedPatients")
                .document(patientUsername)
                .collection("Prescriptions")
                .orderBy("timestamp", Query.Direction.DESCENDING)

                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    prescriptionList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Prescription prescription = doc.toObject(Prescription.class);
                            if (prescription != null) {
                                prescriptionList.add(prescription);
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Error parsing prescription: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    prescriptionAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to fetch prescriptions: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

}

