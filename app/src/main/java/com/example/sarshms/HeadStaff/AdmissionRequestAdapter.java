package com.example.sarshms.HeadStaff;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sarshms.R;
import com.google.firebase.firestore.*;

import java.util.*;

public class AdmissionRequestAdapter extends RecyclerView.Adapter<AdmissionRequestAdapter.ViewHolder> {

    private List<Map<String, Object>> requests;
    private String hospitalUsername;
    private Context context;

    public AdmissionRequestAdapter(List<Map<String, Object>> requests, String hospitalUsername, Context context) {
        this.requests = requests;
        this.hospitalUsername = hospitalUsername;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admission_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> req = requests.get(position);
        String username = (String) req.get("username");
        String notes = (String) req.get("notes");
        String doctor = (String) req.get("doctorEmail");

        holder.tvUsername.setText("Patient: " + username);
        holder.tvDoctor.setText("Requested by: " + doctor);
        holder.tvNotes.setText("Notes: " + (notes != null ? notes : "-"));

        holder.btnAccept.setOnClickListener(v -> {
            showRoomSelectionDialog(username, notes, doctor);
        });

        holder.btnReject.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("Hospitals")
                    .document(hospitalUsername)
                    .collection("AdmissionRequests")
                    .document(username)
                    .update("admissionStatus", "Rejected")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Request rejected", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvDoctor, tvNotes;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvDoctor = itemView.findViewById(R.id.tv_doctor);
            tvNotes = itemView.findViewById(R.id.tv_notes);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }

    private void showRoomSelectionDialog(String username, String notes, String doctor) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Hospitals")
                .document(hospitalUsername)
                .collection("ROOMS")
                .get()
                .addOnSuccessListener(roomSnapshots -> {
                    List<String> roomNames = new ArrayList<>();
                    Map<String, Map<String, Object>> roomBedMap = new HashMap<>();

                    for (DocumentSnapshot doc : roomSnapshots) {
                        roomNames.add(doc.getId());
                        roomBedMap.put(doc.getId(), doc.getData());
                    }

                    if (roomNames.isEmpty()) {
                        Toast.makeText(context, "No rooms found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select Room");

                    String[] roomArray = roomNames.toArray(new String[0]);
                    builder.setItems(roomArray, (dialog, which) -> {
                        String selectedRoom = roomArray[which];
                        Map<String, Object> beds = roomBedMap.get(selectedRoom);

                        // Show available beds
                        List<String> availableBeds = new ArrayList<>();
                        for (String bedKey : beds.keySet()) {
                            if ("Available".equals(beds.get(bedKey))) {
                                availableBeds.add(bedKey);
                            }
                        }

                        if (availableBeds.isEmpty()) {
                            Toast.makeText(context, "No available beds in this room", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AlertDialog.Builder bedDialog = new AlertDialog.Builder(context);
                        bedDialog.setTitle("Select Bed in " + selectedRoom);
                        String[] bedArray = availableBeds.toArray(new String[0]);
                        bedDialog.setItems(bedArray, (bedDialogInterface, bedIndex) -> {
                            String selectedBed = bedArray[bedIndex];

                            // 1. Mark bed as Occupied
                            db.collection("Hospitals")
                                    .document(hospitalUsername)
                                    .collection("ROOMS")
                                    .document(selectedRoom)
                                    .update(selectedBed, "Occupied");

                            // 2. Update Admission Request status
                            db.collection("Hospitals")
                                    .document(hospitalUsername)
                                    .collection("AdmissionRequests")
                                    .document(username)
                                    .update("admissionStatus", "Accepted");

                            // 3. Add to AdmittedPatients
                            Map<String, Object> admittedData = new HashMap<>();
                            admittedData.put("username", username);
                            admittedData.put("notes", notes);
                            admittedData.put("room", selectedRoom);
                            admittedData.put("bed", selectedBed);
                            admittedData.put("status", "Admitted");
                            admittedData.put("doctorEmail", doctor);
                            admittedData.put("admissionTimestamp", FieldValue.serverTimestamp());  // Admission timestamp

                            // Save to hospital side collection
                            db.collection("Hospitals")
                                    .document(hospitalUsername)
                                    .collection("AdmittedPatients")
                                    .document(username)
                                    .set(admittedData)
                                    .addOnSuccessListener(unused -> {
                                        // 4. Save to user side collection
                                        Map<String, Object> userAdmittedData = new HashMap<>();
                                        userAdmittedData.put("admissionTimestamp", FieldValue.serverTimestamp());
                                        userAdmittedData.put("room", selectedRoom);
                                        userAdmittedData.put("bed", selectedBed);

                                        db.collection("Users")
                                                .document(username)
                                                .collection("Hospitals")
                                                .document(hospitalUsername)
                                                .set(userAdmittedData)
                                                .addOnSuccessListener(userUnused -> {
                                                    Toast.makeText(context, "Patient admitted & bed allotted", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(context, "Failed to save timestamp for user", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to admit patient", Toast.LENGTH_SHORT).show();
                                    });
                        });

                        bedDialog.show();
                    });

                    builder.show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to fetch rooms", Toast.LENGTH_SHORT).show();
                });
    }
}
