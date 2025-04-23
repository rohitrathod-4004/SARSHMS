package com.example.sarshms.HeadStaff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.sarshms.R;
import com.google.firebase.firestore.*;

import java.util.*;

public class AdmittedPatientsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdmittedPatientAdapter adapter;
    List<Map<String, Object>> admittedList = new ArrayList<>();
    String hospitalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admitted_patients);

        recyclerView = findViewById(R.id.recyclerViewAdmitted);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");

        adapter = new AdmittedPatientAdapter(admittedList, hospitalUsername, this);
        recyclerView.setAdapter(adapter);

        fetchAdmittedPatients();
    }

    private void fetchAdmittedPatients() {
        FirebaseFirestore.getInstance()
                .collection("Hospitals")
                .document(hospitalUsername)
                .collection("AdmittedPatients")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    admittedList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        admittedList.add(doc.getData());
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load patients", Toast.LENGTH_SHORT).show());
    }
}
