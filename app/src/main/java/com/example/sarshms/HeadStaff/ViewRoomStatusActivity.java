package com.example.sarshms.HeadStaff;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sarshms.R;
import com.google.firebase.firestore.*;

import java.util.*;

public class ViewRoomStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomStatusAdapter adapter;
    private List<RoomModel> roomList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String hospitalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room_status);

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");

        recyclerView = findViewById(R.id.rv_room_status);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RoomStatusAdapter(this, roomList);
        recyclerView.setAdapter(adapter);

        loadRoomStatus();
    }

    private void loadRoomStatus() {
        db.collection("Hospitals").document(hospitalUsername)
                .collection("ROOMS")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        roomList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String roomId = doc.getId();
                            Map<String, Object> objectMap = doc.getData();
                            Map<String, String> bedMap = new HashMap<>();

                            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                                bedMap.put(entry.getKey(), String.valueOf(entry.getValue()));
                            }

                            // Using 0 as dummy room number (not used in status view)
                            roomList.add(new RoomModel(roomId, 0, bedMap));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error fetching room status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
