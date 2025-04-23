package com.example.sarshms.HeadStaff;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sarshms.R;
import com.google.firebase.firestore.*;
import java.util.*;

public class AdmissionRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Map<String, Object>> requestsList;
    private AdmissionRequestAdapter adapter;
    private String hospitalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admission_requests);

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");
        recyclerView = findViewById(R.id.recycler_view_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestsList = new ArrayList<>();
        adapter = new AdmissionRequestAdapter(requestsList, hospitalUsername, this);
        recyclerView.setAdapter(adapter);

        fetchRequests();
    }

    private void fetchRequests() {
        FirebaseFirestore.getInstance()
                .collection("Hospitals")
                .document(hospitalUsername)
                .collection("AdmissionRequests")
                .whereEqualTo("admissionStatus", "Requested")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestsList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> request = doc.getData();
                        request.put("docId", doc.getId());
                        requestsList.add(request);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load requests", Toast.LENGTH_SHORT).show());
    }
}
