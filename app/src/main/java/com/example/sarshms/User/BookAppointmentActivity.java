package com.example.sarshms.User;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sarshms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity {

    private Spinner spinnerHospitals, spinnerDoctors;
    private Button btnSelectDate, btnSelectTime, btnBookAppointment;
    private TextView tvSelectedDate, tvSelectedTime;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String selectedHospital = "";
    private String selectedDoctor = "";
    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // UI Elements
        spinnerHospitals = findViewById(R.id.spinner_hospitals);
        spinnerDoctors = findViewById(R.id.spinner_doctors);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectTime = findViewById(R.id.btn_select_time);
        btnBookAppointment = findViewById(R.id.btn_book_appointment);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvSelectedTime = findViewById(R.id.tv_selected_time);

        loadHospitals();

        btnSelectDate.setOnClickListener(v -> selectDate());
        btnSelectTime.setOnClickListener(v -> selectTime());
        btnBookAppointment.setOnClickListener(v -> bookAppointment());
    }

    private void loadHospitals() {
        db.collection("Hospitals").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> hospitalList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    hospitalList.add(doc.getId());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hospitalList);
                spinnerHospitals.setAdapter(adapter);

                spinnerHospitals.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        selectedHospital = hospitalList.get(position);
                        loadDoctors(selectedHospital);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) { }
                });
            }
        });
    }

    private void loadDoctors(String hospital) {
        db.collection("Hospitals").document(hospital).collection("Doctors").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> doctorList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    doctorList.add(doc.getId());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, doctorList);
                spinnerDoctors.setAdapter(adapter);

                spinnerDoctors.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        selectedDoctor = doctorList.get(position);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) { }
                });
            }
        });
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = day + "/" + (month + 1) + "/" + year;
            tvSelectedDate.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hour, minute) -> {
            selectedTime = hour + ":" + (minute < 10 ? "0" + minute : minute);
            tvSelectedTime.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void bookAppointment() {
        if (selectedHospital.isEmpty() || selectedDoctor.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = mAuth.getCurrentUser().getEmail();

        // Step 1: Find the hospital document
        db.collection("Hospitals").document(selectedHospital).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Step 2: Create appointment data
                        Map<String, Object> appointment = new HashMap<>();
                        appointment.put("doctor", selectedDoctor);
                        appointment.put("date", selectedDate);
                        appointment.put("time", selectedTime);
                        appointment.put("userEmail", userEmail);

                        // Step 3: Store inside "Appointments" subcollection of the found hospital
                        db.collection("Hospitals")
                                .document(selectedHospital)
                                .collection("Appointments")
                                .add(appointment)
                                .addOnSuccessListener(documentReference ->
                                        Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to book appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    } else {
                        Toast.makeText(this, "Hospital not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error finding hospital: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

}
