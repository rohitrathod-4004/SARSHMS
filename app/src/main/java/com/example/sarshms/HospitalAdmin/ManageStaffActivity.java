package com.example.sarshms.HospitalAdmin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageStaffActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String hospitalUsername, staffType;
    private EditText etStaffEmail;
    private StaffAdapter staffAdapter;
    private ArrayList<String> staffList = new ArrayList<>();
    private String selectedStaffEmail = null;  // Track selected staff for removal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_admin_manage_staff);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get data from Intent
        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        staffType = getIntent().getStringExtra("type");

        // UI Elements
        TextView tvManageTitle = findViewById(R.id.tv_manage_title);
        etStaffEmail = findViewById(R.id.et_staff_email);
        Button btnAddStaff = findViewById(R.id.btn_add_staff);
        Button btnRemoveStaff = findViewById(R.id.btn_remove_staff);
        RecyclerView recyclerStaff = findViewById(R.id.recycler_staff);

        // Set title
        tvManageTitle.setText("Manage " + staffType);

        // Setup RecyclerView
        recyclerStaff.setLayoutManager(new LinearLayoutManager(this));
        staffAdapter = new StaffAdapter(staffList, email -> selectedStaffEmail = email);
        recyclerStaff.setAdapter(staffAdapter);

        // Load current staff
        loadStaff();

        // Add staff
        btnAddStaff.setOnClickListener(v -> addStaff());

        // Remove staff
        btnRemoveStaff.setOnClickListener(v -> removeStaff());
    }

    private void loadStaff() {
        db.collection("Hospitals")
                .document(hospitalUsername)
                .collection(staffType)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        staffList.clear();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            staffList.add(doc.getId());
                        }
                        staffAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void addStaff() {
        String email = etStaffEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter staff email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Firebase Auth User
        mAuth.createUserWithEmailAndPassword(email, "default123")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save in Firestore
                        Map<String, Object> staffData = new HashMap<>();
                        staffData.put("email", email);
                        db.collection("Hospitals")
                                .document(hospitalUsername)
                                .collection(staffType)
                                .document(email)
                                .set(staffData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Staff added successfully", Toast.LENGTH_SHORT).show();
                                    staffList.add(email);
                                    staffAdapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add staff", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Failed to create staff account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeStaff() {
        if (selectedStaffEmail == null) {
            Toast.makeText(this, "Select a staff member to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete from Firebase Auth
        mAuth.getCurrentUser().delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Delete from Firestore
                        db.collection("Hospitals")
                                .document(hospitalUsername)
                                .collection(staffType)
                                .document(selectedStaffEmail)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Staff removed successfully", Toast.LENGTH_SHORT).show();
                                    staffList.remove(selectedStaffEmail);
                                    staffAdapter.notifyDataSetChanged();
                                    selectedStaffEmail = null;
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove staff", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Failed to delete staff account", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
