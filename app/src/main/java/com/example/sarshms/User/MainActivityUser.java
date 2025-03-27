package com.example.sarshms.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;


public class MainActivityUser extends AppCompatActivity {

    private ImageView bookAppointment, historyRecords, admissionStatus, emergencySOSImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard_user);

        // Initialize ImageViews
        bookAppointment = findViewById(R.id.bookAppointmentsImage);
        historyRecords = findViewById(R.id.medicalHistoryImage);
        admissionStatus = findViewById(R.id.admissionStatusImage);
        emergencySOSImage= findViewById(R.id.emergencySOSImage);

        // Click listeners for navigation
        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityUser.this, BookAppointmentActivity.class));
            }
        });

        historyRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityUser.this, HistoryRecordsActivity.class));
            }
        });

        admissionStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityUser.this, AdmissionStatusActivity.class));
            }
        });

        emergencySOSImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityUser.this, EmergencySOS.class));
            }
        });
    }
}

