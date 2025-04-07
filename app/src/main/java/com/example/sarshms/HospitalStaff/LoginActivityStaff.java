package com.example.sarshms.HospitalStaff;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.HeadStaff.HeadStaffDashboard;
import com.example.sarshms.Inventory.InventoryDashboard;
import com.example.sarshms.LabTechnician.LabTechnicianDashboard;
import com.example.sarshms.R;
import com.example.sarshms.doctors.DoctorDashboard;
import com.example.sarshms.finance.FinanceDashboard;
import com.example.sarshms.receptionist.ReceptionistDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivityStaff extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Spinner roleSpinner;
    private final String[] roles = {"Doctor", "Receptionist", "Lab Technician", "Finance Dept", "Head Staff", "Inventory Manager"};
    private boolean userFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        roleSpinner = findViewById(R.id.role_spinner);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Populate spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        roleSpinner.setAdapter(adapter);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            final String selectedRole = roleSpinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivityStaff.this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            checkUserRole(email, selectedRole);
                        } else {
                            Toast.makeText(LoginActivityStaff.this, "Login Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void checkUserRole(String email, String roleCollection) {
        switch (roleCollection) {
            case "Doctor":
                roleCollection = "Doctors";
                break;
            case "Receptionist":
                roleCollection = "Receptionists";
                break;
            case "Lab Technician":
                roleCollection = "LabTechnician";
                break;
            case "Finance Dept":
                roleCollection = "FinanceDept";
                break;
            case "Head Staff":
                roleCollection = "HeadStaff";
                break;
            case "Inventory Manager":
                roleCollection = "InventoryManager";
                break;
        }

        final String dbRole = roleCollection;
        final boolean[] userFound = {false}; // mutable wrapper for boolean

        db.collection("Hospitals").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot hospitalDoc : task.getResult()) {
                    String hospitalId = hospitalDoc.getId();
                    db.collection("Hospitals")
                            .document(hospitalId)
                            .collection(dbRole)
                            .document(email)
                            .get()
                            .addOnCompleteListener(innerTask -> {
                                if (innerTask.isSuccessful() && innerTask.getResult().exists() && !userFound[0]) {
                                    userFound[0] = true;
                                    navigateToDashboard(dbRole, hospitalId);
                                }
                            });
                }

                // Timeout fallback in case no match is found
                new Handler().postDelayed(() -> {
                    if (!userFound[0]) {
                        Toast.makeText(LoginActivityStaff.this, "User not found under selected role in any hospital", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
            } else {
                Toast.makeText(LoginActivityStaff.this, "Error fetching hospital data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateToDashboard(String role, String hospitalId) {
        Toast.makeText(this, "Welcome to " + hospitalId, Toast.LENGTH_SHORT).show();

        Intent intent;
        switch (role) {
            case "Doctors":
                intent = new Intent(LoginActivityStaff.this, DoctorDashboard.class);
                break;
            case "Receptionists":
                intent = new Intent(LoginActivityStaff.this, ReceptionistDashboard.class);
                break;
            case "Head Staff":
                intent = new Intent(LoginActivityStaff.this, HeadStaffDashboard.class);
                break;
            case "Finance Dept":
                intent = new Intent(LoginActivityStaff.this, FinanceDashboard.class);
                break;
            case "Lab Technician":
                intent = new Intent(LoginActivityStaff.this, LabTechnicianDashboard.class);
                break;
            case "Inventory Manager":
                intent = new Intent(LoginActivityStaff.this, InventoryDashboard.class);
                break;
            default:
                Toast.makeText(LoginActivityStaff.this, "Role not recognized", Toast.LENGTH_SHORT).show();
                return;
        }

        intent.putExtra("hospitalId", hospitalId); // ✅ Send hospital ID to dashboard
        startActivity(intent);
        finish();
    }

}
