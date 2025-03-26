package com.example.sarshms.HospitalAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivityHospital extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String hospitalUsername;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_hospital);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get hospital username passed from login/signup
        hospitalUsername = getIntent().getStringExtra("hospitalUsername");

        // UI Elements
        tvWelcome = findViewById(R.id.tv_welcome);
        Button btnManageDoctors = findViewById(R.id.btn_manage_doctors);
        Button btnManageReceptionists = findViewById(R.id.btn_manage_receptionists);
        Button btnManageHeadStaff = findViewById(R.id.btn_manage_headstaff);
        Button btnManageFinance = findViewById(R.id.btn_manage_finance);
        Button btnManageLabTech = findViewById(R.id.btn_manage_labtech);
        Button btnManageInventory = findViewById(R.id.btn_manage_inventory);
        Button btnViewPatients = findViewById(R.id.btn_view_patients);
        Button btnViewBeds = findViewById(R.id.btn_view_beds);
        Button btnHospitalRevenue = findViewById(R.id.btn_hospital_revenue);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Fetch Hospital Details
        fetchHospitalDetails();

        // Button Listeners
        btnManageDoctors.setOnClickListener(v -> openManagePage("Doctors"));
        btnManageReceptionists.setOnClickListener(v -> openManagePage("Receptionists"));
        btnManageHeadStaff.setOnClickListener(v -> openManagePage("HeadStaff"));
        btnManageFinance.setOnClickListener(v -> openManagePage("Finance"));
        btnManageLabTech.setOnClickListener(v -> openManagePage("LabTechnicians"));
        btnManageInventory.setOnClickListener(v -> openManagePage("Inventory"));
        btnViewPatients.setOnClickListener(v -> openManagePage("Patients"));
        btnViewBeds.setOnClickListener(v -> openManagePage("Beds"));
        btnHospitalRevenue.setOnClickListener(v -> openManagePage("Revenue"));

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(DashboardActivityHospital.this, LoginActivityHospital.class));
            finish();
        });
    }

    private void fetchHospitalDetails() {
        DocumentReference docRef = db.collection("Hospitals").document(hospitalUsername);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String hospitalName = documentSnapshot.getString("hospitalName");
                tvWelcome.setText("Welcome, " + hospitalName + " Admin!");
            }
        });
    }

    private void openManagePage(String type) {
        Intent intent = new Intent(DashboardActivityHospital.this, ManageStaffActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("hospitalUsername", hospitalUsername);
        startActivity(intent);
    }
}
