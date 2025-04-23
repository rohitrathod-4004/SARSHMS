package com.example.sarshms.doctors;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ManageAdmittedPatientsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewAdmittedPatients;
    private FirebaseFirestore db;
    private List<Patient> admittedPatientList;
    private PatientAdapter patientAdapter;
    private String hospitalUsername , doctorEmail ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admitted_patients);

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        doctorEmail = getIntent().getStringExtra("doctorEmail");

        Toast.makeText(this, doctorEmail,  Toast.LENGTH_LONG).show();

        recyclerViewAdmittedPatients = findViewById(R.id.recyclerViewAdmittedPatients);
        db = FirebaseFirestore.getInstance();

        admittedPatientList = new ArrayList<>();
        patientAdapter = new PatientAdapter(admittedPatientList ,hospitalUsername , doctorEmail);

        recyclerViewAdmittedPatients.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdmittedPatients.setAdapter(patientAdapter);

        // Fetch Patients Admitted under Current Doctor
        fetchAdmittedPatients(hospitalUsername , doctorEmail );
    }

    private void fetchAdmittedPatients(String hospitalUsername ,String doctorEmail ) {
        String currentDoctorEmail = doctorEmail;  // Replace with actual current doctor's email
        db.collection("Hospitals")
                .document(hospitalUsername)  // Replace with actual hospital username
                .collection("AdmittedPatients")
                .whereEqualTo("doctorEmail", currentDoctorEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    admittedPatientList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Patient patient = doc.toObject(Patient.class);
                        patient.setUsername(doc.getId());  // Set patient username
                        admittedPatientList.add(patient);
                    }
                    patientAdapter.notifyDataSetChanged();
                });
    }
}
