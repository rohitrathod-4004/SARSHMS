package com.example.sarshms.receptionist;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageAppointmentsHistoryActivity extends AppCompatActivity {

    private Spinner spinnerDoctorsHistory;
    private RecyclerView recyclerHistory;
    private FirebaseFirestore db;
    private HistoryAppointmentAdapter adapter;
    private List<HistoryAppointment> historyList = new ArrayList<>();
    private List<String> doctorList = new ArrayList<>();
    private String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointments_history);

        spinnerDoctorsHistory = findViewById(R.id.spinner_doctors_history);
        recyclerHistory = findViewById(R.id.recycler_appointment_history);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        hospitalId = getIntent().getStringExtra("hospitalId");

        if (hospitalId == null || hospitalId.isEmpty()) {
            Toast.makeText(this, "Hospital ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDoctors();

        spinnerDoctorsHistory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedDoctor = doctorList.get(position);
                loadAppointmentHistory(selectedDoctor);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void loadDoctors() {
        db.collection("Hospitals")
                .document(hospitalId)
                .collection("Doctors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        doctorList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            if (!doctorList.contains(doc.getId())) {
                                doctorList.add(doc.getId());
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, doctorList);
                        spinnerDoctorsHistory.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAppointmentHistory(String doctorEmail) {
        db.collection("Hospitals")
                .document(hospitalId)
                .collection("Appointments")
                .whereEqualTo("doctor", doctorEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        historyList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String status = doc.getString("status");
                            if (status != null && (status.equals("approved") || status.equals("rejected"))) {
                                HistoryAppointment appointment = doc.toObject(HistoryAppointment.class);
                                historyList.add(appointment);
                            }
                        }
                        adapter = new HistoryAppointmentAdapter(this, historyList);
                        recyclerHistory.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to load appointment history", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
