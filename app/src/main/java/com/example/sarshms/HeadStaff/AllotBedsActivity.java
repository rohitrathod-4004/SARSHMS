package com.example.sarshms.HeadStaff;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllotBedsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BedAllotmentAdapter adapter;
    private List<Map<String, Object>> admittedUsers;
    private String hospitalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allot_beds);

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");

        recyclerView = findViewById(R.id.recycler_view_admitted);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        admittedUsers = new ArrayList<>();
        adapter = new BedAllotmentAdapter(admittedUsers, hospitalUsername, this);
        recyclerView.setAdapter(adapter);

        fetchAdmittedUsers();
    }

    private void fetchAdmittedUsers() {
        FirebaseFirestore.getInstance().collection("Hospitals")
                .document(hospitalUsername)
                .collection("AdmittedPatients")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    admittedUsers.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> userMap = doc.getData();
                        userMap.put("docId", doc.getId());
                        admittedUsers.add(userMap);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
    }
}
