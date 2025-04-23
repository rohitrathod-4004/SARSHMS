package com.example.sarshms.doctors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {
    private List<Prescription> prescriptionList;

    public PrescriptionAdapter(List<Prescription> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }

    @Override
    public PrescriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PrescriptionViewHolder holder, int position) {
        Prescription prescription = prescriptionList.get(position);

        // Format the timestamp to readable date string
        Timestamp timestamp = prescription.getTimestamp();
        String formattedDate = "Unknown date";
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            formattedDate = sdf.format(date);
        }

        // Bind formatted data
        holder.tvDate.setText("Date: " + formattedDate);
        holder.tvPrescription.setText("Prescription: " + prescription.getMedicines());
        holder.tvLabTest.setText("Lab Test: " + prescription.getDiagnosis());
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public static class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvPrescription, tvLabTest;

        public PrescriptionViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPrescription = itemView.findViewById(R.id.tvPrescription);
            tvLabTest = itemView.findViewById(R.id.tvLabTest);
        }
    }
}
