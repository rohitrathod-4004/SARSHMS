package com.example.sarshms.HospitalStaff;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.HeadStaff.HeadStaffDashboard;
import com.example.sarshms.Inventory.InventoryDashboard;
import com.example.sarshms.LabTechnician.LabTechnicianDashboard;
import com.example.sarshms.R;
import com.example.sarshms.doctors.DoctorDashboard;
import com.example.sarshms.finance.FinanceDashboard;
import com.example.sarshms.receptionist.ReceptionistDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivityStaff extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivityStaff.this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    checkUserRole(email);
                                } else {
                                    Toast.makeText(LoginActivityStaff.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void checkUserRole(String email) {

        db.collection("Hospitals").document("Rohit_Hospital").collection("Doctors").document(email).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String role = document.getString("role");

                                if (role != null) {
                                    switch (role) {
                                        case "Doctors":
                                            startActivity(new Intent(LoginActivityStaff.this, DoctorDashboard.class));
                                            break;
                                        case "Receptionists":
                                            startActivity(new Intent(LoginActivityStaff.this, ReceptionistDashboard.class));
                                            break;
                                        case "Head Staff":
                                            startActivity(new Intent(LoginActivityStaff.this, HeadStaffDashboard.class));
                                            break;
                                        case "Finance Dept":
                                            startActivity(new Intent(LoginActivityStaff.this, FinanceDashboard.class));
                                            break;
                                        case "Lab Technician":
                                            startActivity(new Intent(LoginActivityStaff.this, LabTechnicianDashboard.class));
                                            break;
                                        case "Inventory Manager":
                                            startActivity(new Intent(LoginActivityStaff.this, InventoryDashboard.class));
                                            break;
                                        default:
                                            Toast.makeText(LoginActivityStaff.this, "Role not recognized", Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                }
                            } else {
                                Toast.makeText(LoginActivityStaff.this, "User role not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivityStaff.this, "Error fetching role", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
