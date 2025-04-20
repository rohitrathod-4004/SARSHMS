package com.example.sarshms.HeadStaff;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class BedAllotmentAdapter extends RecyclerView.Adapter<BedAllotmentAdapter.BedViewHolder> {

    private List<Map<String, Object>> admittedPatients;
    private String hospitalUsername;
    private Context context;

    public BedAllotmentAdapter(List<Map<String, Object>> admittedPatients, String hospitalUsername, Context context) {
        this.admittedPatients = admittedPatients;
        this.hospitalUsername = hospitalUsername;
        this.context = context;
    }

    @NonNull
    @Override
    public BedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admitted_patient, parent, false);
        return new BedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BedViewHolder holder, int position) {
        Map<String, Object> patient = admittedPatients.get(position);
        String username = (String) patient.get("username");
        String notes = (String) patient.get("notes");
        String bedNo = patient.containsKey("bedNo") ? patient.get("bedNo").toString() : "Not Allotted";

        holder.tvUsername.setText("Username: " + username);
        holder.tvNotes.setText("Notes: " + (notes != null ? notes : "-"));
        holder.tvBedNo.setText("Bed No: " + bedNo);

        holder.btnAllot.setOnClickListener(v -> showBedInputDialog(username));
    }

    @Override
    public int getItemCount() {
        return admittedPatients.size();
    }

    private void showBedInputDialog(String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Allot Bed to " + username);

        final EditText input = new EditText(context);
        input.setHint("Enter Bed Number");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Allot", (dialog, which) -> {
            String bedNo = input.getText().toString().trim();
            if (!bedNo.isEmpty()) {
                FirebaseFirestore.getInstance()
                        .collection("Hospitals")
                        .document(hospitalUsername)
                        .collection("AdmittedPatients")
                        .document(username)
                        .update("bedNo", bedNo)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(context, "Bed " + bedNo + " allotted to " + username, Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Failed to allot bed", Toast.LENGTH_SHORT).show());
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    static class BedViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername, tvNotes, tvBedNo;
        Button btnAllot;

        public BedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvNotes = itemView.findViewById(R.id.tv_notes);
            tvBedNo = itemView.findViewById(R.id.tv_bed_no);
            btnAllot = itemView.findViewById(R.id.btn_allot_bed);
        }
    }
}
