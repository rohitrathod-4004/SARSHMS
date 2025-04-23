package com.example.sarshms.HeadStaff;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageBedsActivity extends AppCompatActivity {

    private Spinner spinnerRooms;
    private RecyclerView recyclerBeds;
    private BedAdapter bedAdapter;
    private List<String> roomIds = new ArrayList<>();
    private List<String> roomDetails = new ArrayList<>();
    private List<BedModel> bedList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String hospitalUsername;
    private Button btnAddBed;
    private String selectedRoom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_beds);

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");

        spinnerRooms = findViewById(R.id.spinner_rooms);
        recyclerBeds = findViewById(R.id.recycler_beds);
        btnAddBed = findViewById(R.id.btn_add_bed);

        recyclerBeds.setLayoutManager(new LinearLayoutManager(this));
        bedAdapter = new BedAdapter(bedList, hospitalUsername);
        recyclerBeds.setAdapter(bedAdapter);

        loadRoomIds();

        spinnerRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoom = roomIds.get(position);
                loadBedsForRoom(selectedRoom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAddBed.setOnClickListener(v -> {
            if (selectedRoom.isEmpty()) return;

            // Add new bed to the selected room
            db.collection("Hospitals").document(hospitalUsername)
                    .collection("ROOMS").document(selectedRoom)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            int maxIndex = 0;
                            for (String key : data.keySet()) {
                                if (key.startsWith("bed")) {
                                    try {
                                        int num = Integer.parseInt(key.substring(3));
                                        if (num > maxIndex) maxIndex = num;
                                    } catch (Exception ignored) {}
                                }
                            }
                            String newBedId = "bed" + (maxIndex + 1);
                            db.collection("Hospitals").document(hospitalUsername)
                                    .collection("ROOMS").document(selectedRoom)
                                    .update(newBedId, "Available")
                                    .addOnSuccessListener(unused -> {
                                        // After adding the bed, update the available bed count and reload beds
                                        updateAvailableBedCount(selectedRoom);
                                        loadBedsForRoom(selectedRoom);
                                        loadRoomIds();  // Refresh room list to reflect updated available beds count
                                    });
                        }
                    });
        });
    }

    // Loads the room IDs and the total available beds for each room
    private void loadRoomIds() {
        db.collection("Hospitals").document(hospitalUsername)
                .collection("ROOMS")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        roomIds.clear();
                        roomDetails.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String roomId = doc.getId();
                            roomIds.add(roomId);

                            // Get the available beds count safely, default to 0 if null
                            Object availableBedsObj = doc.get("totalAvailableBeds");
                            int availableBeds = 0;
                            if (availableBedsObj != null) {
                                availableBeds = Math.toIntExact((long) availableBedsObj);
                            }

                            roomDetails.add("Room: " + roomId + " - Available Beds: " + availableBeds);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_custom, roomDetails);
                        adapter.setDropDownViewResource(R.layout.spinner_dropdown_custom);
                        spinnerRooms.setAdapter(adapter);
                    }
                });
    }

    // Loads the beds for a specific room
    private void loadBedsForRoom(String roomId) {
        db.collection("Hospitals").document(hospitalUsername)
                .collection("ROOMS")
                .document(roomId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> bedMap = documentSnapshot.getData();
                        bedList.clear();
                        for (Map.Entry<String, Object> entry : bedMap.entrySet()) {
                            if (!entry.getKey().equals("totalAvailableBeds")) {
                                bedList.add(new BedModel(roomId, entry.getKey(), entry.getValue().toString()));
                            }
                        }
                        bedAdapter.setBedList(bedList);
                        bedAdapter.notifyDataSetChanged();
                    }
                });
    }

    // Updates the available bed count for a specific room
    private void updateAvailableBedCount(String roomId) {
        db.collection("Hospitals").document(hospitalUsername)
                .collection("ROOMS").document(roomId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        int available = 0;
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().startsWith("bed") && entry.getValue().equals("Available")) {
                                available++;
                            }
                        }
                        // Update the total available beds count in Firestore
                        db.collection("Hospitals").document(hospitalUsername)
                                .collection("ROOMS").document(roomId)
                                .update("totalAvailableBeds", available);
                    }
                });
    }
}
