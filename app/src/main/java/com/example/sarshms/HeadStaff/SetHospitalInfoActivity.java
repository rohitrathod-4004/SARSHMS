package com.example.sarshms.HeadStaff;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SetHospitalInfoActivity extends AppCompatActivity {

    private EditText etTotalBeds, etFacilities;
    private Button btnSaveInfo;
    private String hospitalUsername; // Pass via Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_hospital_info);

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");

        etTotalBeds = findViewById(R.id.et_total_beds);
        etFacilities = findViewById(R.id.et_facilities);
        btnSaveInfo = findViewById(R.id.btn_save_info);

        btnSaveInfo.setOnClickListener(v -> {
            String totalBeds = etTotalBeds.getText().toString().trim();
            String facilities = etFacilities.getText().toString().trim();

            if (totalBeds.isEmpty()) {
                Toast.makeText(this, "Enter number of beds", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("totalBeds", Integer.parseInt(totalBeds));
            data.put("facilities", facilities);

            FirebaseFirestore.getInstance().collection("Hospitals")
                    .document(hospitalUsername)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Hospital Info Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        });
    }
}
