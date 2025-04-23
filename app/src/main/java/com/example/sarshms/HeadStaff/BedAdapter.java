package com.example.sarshms.HeadStaff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class BedAdapter extends RecyclerView.Adapter<BedAdapter.BedViewHolder> {

    private List<BedModel> bedList;
    private String hospitalUsername;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> statusOptions = Arrays.asList("Available", "Occupied", "Unavailable", "Dead");

    public BedAdapter(List<BedModel> bedList, String hospitalUsername) {
        this.bedList = bedList;
        this.hospitalUsername = hospitalUsername;
    }

    @NonNull
    @Override
    public BedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bed, parent, false);
        return new BedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BedViewHolder holder, int position) {
        BedModel bed = bedList.get(position);
        holder.tvBedId.setText(bed.getBedId());

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerStatus.setAdapter(adapter);

        // Set spinner selection to current status
        int selectedIndex = statusOptions.indexOf(bed.getStatus());
        if (selectedIndex != -1) {
            holder.spinnerStatus.setSelection(selectedIndex);
        }

        // Listener to update bed status
        holder.spinnerStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            boolean firstTime = true;

            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                if (firstTime) {
                    firstTime = false;
                    return;
                }

                String selectedStatus = statusOptions.get(pos);
                db.collection("Hospitals")
                        .document(hospitalUsername)
                        .collection("ROOMS")
                        .document(bed.getRoomId())
                        .update(bed.getBedId(), selectedStatus)
                        .addOnSuccessListener(unused -> {
                            bed.setStatus(selectedStatus);
                            Toast.makeText(holder.itemView.getContext(), "Status updated", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Delete button
        holder.btnDeleteBed.setOnClickListener(v -> {
            db.collection("Hospitals")
                    .document(hospitalUsername)
                    .collection("ROOMS")
                    .document(bed.getRoomId())
                    .update(bed.getBedId(), "Dead")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(holder.itemView.getContext(), "Bed marked as Dead", Toast.LENGTH_SHORT).show();
                        bed.setStatus("Dead");
                        holder.spinnerStatus.setSelection(statusOptions.indexOf("Dead"));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(holder.itemView.getContext(), "Error deleting bed", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return bedList.size();
    }

    public static class BedViewHolder extends RecyclerView.ViewHolder {
        TextView tvBedId;
        Spinner spinnerStatus;
        View btnDeleteBed;

        public BedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBedId = itemView.findViewById(R.id.tv_bed_id);
            spinnerStatus = itemView.findViewById(R.id.spinner_bed_status);
            btnDeleteBed = itemView.findViewById(R.id.btn_delete_bed);
        }
    }

    // New method to update the bed list and notify the adapter
    public void setBedList(List<BedModel> bedList) {
        this.bedList = bedList;
        notifyDataSetChanged();  // This will refresh the RecyclerView with the new data
    }
}
