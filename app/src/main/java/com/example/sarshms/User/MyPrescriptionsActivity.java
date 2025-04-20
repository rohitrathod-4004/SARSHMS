package com.example.sarshms.User;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class MyPrescriptionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PrescriptionAdapter adapter;
    private List<PrescriptionModel> prescriptionList;
    private FirebaseFirestore db;
    private String username; // fetch from login or saved session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_prescriptions);

        recyclerView = findViewById(R.id.recyclerViewPrescriptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        prescriptionList = new ArrayList<>();
        adapter = new PrescriptionAdapter(prescriptionList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Replace this with the actual logged-in username
        username = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("username", null);

        if (username != null) {
            fetchPrescriptions();
        } else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPrescriptions() {
        db.collection("Users").document(username)
                .collection("Prescriptions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    prescriptionList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String prescription = doc.getString("prescription");
                        String labTest = doc.getString("labTest");
                        String date = doc.getString("appointmentDate");

                        prescriptionList.add(new PrescriptionModel(prescription, labTest, date));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to fetch prescriptions", Toast.LENGTH_SHORT).show()
                );
    }
}
