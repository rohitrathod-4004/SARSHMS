// Updated HeadStaffDashboard.java
package com.example.sarshms.HeadStaff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sarshms.R;

public class HeadStaffDashboard extends AppCompatActivity {

    private Button btnSetInfo, btnAllotBeds, btnManualAdmit, btnViewRoomStatus;
    private String hospitalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_staff_dashboard);

        hospitalUsername = getIntent().getStringExtra("hospitalId");

        btnSetInfo = findViewById(R.id.btn_set_hospital_info);
        btnManualAdmit = findViewById(R.id.btn_manual_admit);
        btnViewRoomStatus = findViewById(R.id.btn_view_room_status);

        btnSetInfo.setOnClickListener(v -> {
            Intent intent = new Intent(HeadStaffDashboard.this, SetHospitalInfoActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            startActivity(intent);
        });


        btnManualAdmit.setOnClickListener(v -> {
            Intent intent = new Intent(HeadStaffDashboard.this, ManualAdmitActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            startActivity(intent);
        });

        btnViewRoomStatus.setOnClickListener(v -> {
            Intent intent = new Intent(HeadStaffDashboard.this, ViewRoomStatusActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            startActivity(intent);
        });

        Button btnManageBeds = findViewById(R.id.btn_manage_beds);
        btnManageBeds.setOnClickListener(v -> {
            Intent intent = new Intent(HeadStaffDashboard.this, ManageBedsActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            startActivity(intent);
        });

        Button btnAdmissionRequests = findViewById(R.id.btn_admission_requests);
        btnAdmissionRequests.setOnClickListener(v -> {
            Intent intent = new Intent(HeadStaffDashboard.this, AdmissionRequestsActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            startActivity(intent);
        });

        Button btnViewAdmittedPatients = findViewById(R.id.btn_view_admitted_patients);
        btnViewAdmittedPatients.setOnClickListener(v -> {
            Intent intent = new Intent(HeadStaffDashboard.this, AdmittedPatientsActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            startActivity(intent);
        });



    }
}
