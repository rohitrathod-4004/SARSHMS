package com.example.sarshms.HeadStaff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sarshms.R;

public class HeadStaffDashboard extends AppCompatActivity {

    private Button btnSetInfo, btnAllotBeds, btnManualAdmit;
    private String hospitalUsername; // This should be passed from login or previous screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_staff_dashboard);

        // Get hospitalUsername passed from previous activity
        hospitalUsername = getIntent().getStringExtra("hospitalId");

        // Link UI components
        btnSetInfo = findViewById(R.id.btn_set_hospital_info);
        btnAllotBeds = findViewById(R.id.btn_allot_beds);
        btnManualAdmit = findViewById(R.id.btn_manual_admit);

        // Button: Set Hospital Info
        btnSetInfo.setOnClickListener(v -> {
            Intent intent = new Intent(HeadStaffDashboard.this, SetHospitalInfoActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            startActivity(intent);
        });

        // Button: Allot Beds
        btnAllotBeds.setOnClickListener(v -> {
            Intent intent = new Intent(HeadStaffDashboard.this, AllotBedsActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            startActivity(intent);
        });

        // Button: Manual Admission
        btnManualAdmit.setOnClickListener(v -> {
            Intent intent = new Intent(HeadStaffDashboard.this, ManualAdmitActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            startActivity(intent);
        });
    }
}
