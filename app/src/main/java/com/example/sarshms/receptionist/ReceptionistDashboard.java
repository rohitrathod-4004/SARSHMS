package com.example.sarshms.receptionist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;

public class ReceptionistDashboard extends AppCompatActivity {

    private String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_receptionist);

        hospitalId = getIntent().getStringExtra("hospitalId");

        Button btnManageAppointments = findViewById(R.id.btn_manage_appointments);
        Button btnAppointmentHistory = findViewById(R.id.btn_appointment_history); // Placeholder

        btnManageAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(ReceptionistDashboard.this, ManageAppointmentsActivity.class);
            intent.putExtra("hospitalId", hospitalId);
            startActivity(intent);
        });

        btnAppointmentHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ReceptionistDashboard.this, ManageAppointmentsHistoryActivity.class);
            intent.putExtra("hospitalId", hospitalId);
            startActivity(intent);
        });
    }
}
