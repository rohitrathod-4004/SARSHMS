package com.example.sarshms.receptionist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private List<Appointment> appointmentList;

    public AppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvDoctor.setText("Doctor: " + appointment.getDoctor());
        holder.tvDate.setText("Date: " + appointment.getDate());
        holder.tvTime.setText("Time: " + appointment.getTime());
        holder.tvUser.setText("User: " + appointment.getUserEmail());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctor, tvDate, tvTime, tvUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctor = itemView.findViewById(R.id.tv_doctor);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvUser = itemView.findViewById(R.id.tv_user);
        }
    }
}
