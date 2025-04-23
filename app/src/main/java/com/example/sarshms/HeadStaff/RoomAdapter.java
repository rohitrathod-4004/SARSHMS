package com.example.sarshms.HeadStaff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sarshms.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<RoomModel> roomList;
    private String hospitalUsername;

    public RoomAdapter(List<RoomModel> roomList, String hospitalUsername) {
        this.roomList = roomList;
        this.hospitalUsername = hospitalUsername;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomModel room = roomList.get(position);
        holder.tvRoomNumber.setText("Room " + room.getRoomNumber());

        holder.btnSetBeds.setOnClickListener(v -> {
            int bedCount;
            try {
                bedCount = Integer.parseInt(holder.etBedCount.getText().toString());
            } catch (Exception e) {
                holder.etBedCount.setError("Invalid number");
                return;
            }

            room.setTotalBeds(bedCount);
            Map<String, Object> bedData = new HashMap<>();
            int availableBeds = 0;

            for (int i = 1; i <= bedCount; i++) {
                bedData.put("bed" + i, "Available");
                availableBeds++; // All beds are available at init
            }

            bedData.put("totalAvailableBeds", availableBeds); // ✅ Add the field

            FirebaseFirestore.getInstance()
                    .collection("Hospitals")
                    .document(hospitalUsername)
                    .collection("ROOMS")
                    .document("Room" + room.getRoomNumber())
                    .set(bedData);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    // ✅ Utility method to call after bed status updates
    public void updateTotalAvailableBeds(String hospitalUsername, String roomId) {
        FirebaseFirestore.getInstance()
                .collection("Hospitals")
                .document(hospitalUsername)
                .collection("ROOMS")
                .document(roomId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int availableBeds = 0;
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                String key = entry.getKey();
                                Object value = entry.getValue();
                                if (key.startsWith("bed") && "Available".equals(value)) {
                                    availableBeds++;
                                }
                            }
                        }

                        FirebaseFirestore.getInstance()
                                .collection("Hospitals")
                                .document(hospitalUsername)
                                .collection("ROOMS")
                                .document(roomId)
                                .update("totalAvailableBeds", availableBeds);
                    }
                });
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNumber;
        EditText etBedCount;
        Button btnSetBeds;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tv_room_number);
            etBedCount = itemView.findViewById(R.id.et_bed_count);
            btnSetBeds = itemView.findViewById(R.id.btn_set_beds);
        }
    }
}
