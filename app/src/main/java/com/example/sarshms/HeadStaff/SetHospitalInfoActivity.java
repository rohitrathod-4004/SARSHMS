package com.example.sarshms.HeadStaff;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetHospitalInfoActivity extends AppCompatActivity {

    private EditText etTotalRooms;
    private Button btnGenerateRooms;
    private RecyclerView recyclerRooms;
    private String hospitalUsername;
    private List<RoomModel> roomList;
    private RoomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_hospital_info);

        etTotalRooms = findViewById(R.id.et_total_rooms);
        btnGenerateRooms = findViewById(R.id.btn_generate_rooms);
        recyclerRooms = findViewById(R.id.recycler_rooms);
        recyclerRooms.setLayoutManager(new LinearLayoutManager(this));

        hospitalUsername = getIntent().getStringExtra("hospitalUsername");

        btnGenerateRooms.setOnClickListener(v -> {
            int totalRooms;
            try {
                totalRooms = Integer.parseInt(etTotalRooms.getText().toString());
            } catch (Exception e) {
                etTotalRooms.setError("Enter a valid number");
                return;
            }

            roomList = new ArrayList<>();
            for (int i = 1; i <= totalRooms; i++) {
                String roomId = "Room" + i;
                roomList.add(new RoomModel(roomId, i, new HashMap<>()));
            }

            adapter = new RoomAdapter(roomList, hospitalUsername);
            recyclerRooms.setAdapter(adapter);
        });
    }
}
