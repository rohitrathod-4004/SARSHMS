package com.example.sarshms.receptionist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;

import java.util.List;

public class HistoryAppointmentAdapter extends RecyclerView.Adapter<HistoryAppointmentAdapter.ViewHolder> {

    private final Context context;
    private final List<HistoryAppointment> appointmentList;

    public HistoryAppointmentAdapter(Context context, List<HistoryAppointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPatientName, txtDate, txtStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPatientName = itemView.findViewById(R.id.txt_patient_name);
            txtDate = itemView.findViewById(R.id.txt_appointment_date);
            txtStatus = itemView.findViewById(R.id.txt_status);
        }
    }

    @NonNull
    @Override
    public HistoryAppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAppointmentAdapter.ViewHolder holder, int position) {
        HistoryAppointment appt = appointmentList.get(position);
        holder.txtPatientName.setText("Patient: " + appt.getPatientName());
        holder.txtDate.setText("Date: " + appt.getDate());
        holder.txtStatus.setText("Status: " + appt.getStatus());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }
}
