package com.example.sarshms.HeadStaff;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManualAdmitActivity extends AppCompatActivity {

    private EditText etUsername, etNotes;
    private Button btnAdmit;
    private String hospitalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_admit);

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");

        etUsername = findViewById(R.id.et_username);
        etNotes = findViewById(R.id.et_notes);
        btnAdmit = findViewById(R.id.btn_manual_admit);

        btnAdmit.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("username", username);
            data.put("notes", notes);
            data.put("status", "Admitted");

            FirebaseFirestore.getInstance()
                    .collection("Hospitals")
                    .document(hospitalUsername)
                    .collection("AdmittedPatients")
                    .document(username)
                    .set(data)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Patient manually admitted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Admission failed", Toast.LENGTH_SHORT).show());
        });
    }
}
