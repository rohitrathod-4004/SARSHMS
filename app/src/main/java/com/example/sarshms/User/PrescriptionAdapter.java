package com.example.sarshms.User;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sarshms.R;
import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {

    private final List<PrescriptionModel> list;

    public PrescriptionAdapter(List<PrescriptionModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvPrescription, tvLabTest;

        public ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);
            tvPrescription = view.findViewById(R.id.tvPrescription);
            tvLabTest = view.findViewById(R.id.tvLabTest);
        }
    }

    @Override
    public PrescriptionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PrescriptionAdapter.ViewHolder holder, int position) {
        PrescriptionModel item = list.get(position);
        holder.tvDate.setText("Date: " + item.getAppointmentDate());
        holder.tvPrescription.setText("Prescription: " + item.getPrescription());
        holder.tvLabTest.setText("Lab Test: " + item.getLabTest());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
