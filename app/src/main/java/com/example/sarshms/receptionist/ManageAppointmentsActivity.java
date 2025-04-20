package com.example.sarshms.receptionist;

import android.os.Bundle;
import android.view.View;
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

public class ManageAppointmentsActivity extends AppCompatActivity {

    private Spinner spinnerDoctors;
    private RecyclerView recyclerAppointments;
    private FirebaseFirestore db;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private final List<String> doctorList = new ArrayList<>();
    private String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointments);

        db = FirebaseFirestore.getInstance();
        spinnerDoctors = findViewById(R.id.spinner_doctors);
        recyclerAppointments = findViewById(R.id.recycler_appointments);
        recyclerAppointments.setLayoutManager(new LinearLayoutManager(this));

        hospitalId = getIntent().getStringExtra("hospitalId");

        if (hospitalId == null || hospitalId.isEmpty()) {
            Toast.makeText(this, "Hospital ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDoctors();

        spinnerDoctors.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedDoctor = doctorList.get(position);
                loadAppointments(selectedDoctor);
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
                        spinnerDoctors.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAppointments(String doctorEmail) {
        db.collection("Hospitals")
                .document(hospitalId)
                .collection("Appointments")
                .whereEqualTo("doctor", doctorEmail)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointmentList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Appointment appointment = doc.toObject(Appointment.class);
                            appointmentList.add(appointment);
                        }
                        adapter = new AppointmentAdapter(ManageAppointmentsActivity.this, appointmentList, hospitalId);
                        recyclerAppointments.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
