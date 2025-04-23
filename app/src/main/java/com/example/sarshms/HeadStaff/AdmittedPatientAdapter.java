package com.example.sarshms.HeadStaff;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sarshms.R;

import java.util.List;
import java.util.Map;

public class AdmittedPatientAdapter extends RecyclerView.Adapter<AdmittedPatientAdapter.ViewHolder> {

    private List<Map<String, Object>> patients;
    private String hospitalUsername;
    private Context context;

    public AdmittedPatientAdapter(List<Map<String, Object>> patients, String hospitalUsername, Context context) {
        this.patients = patients;
        this.hospitalUsername = hospitalUsername;
        this.context = context;
    }

    @NonNull
    @Override
    public AdmittedPatientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admitted_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdmittedPatientAdapter.ViewHolder holder, int position) {
        Map<String, Object> data = patients.get(position);
        String username = (String) data.get("username");
        String doctor = (String) data.get("doctorEmail");
        String room = (String) data.get("room");
        String bed = (String) data.get("bed");

        holder.tvUsername.setText("Patient: " + username);
        holder.tvDoctor.setText("Doctor: " + doctor);
        holder.tvRoomBed.setText("Room: " + room + ", Bed: " + bed);

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PatientDetailActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            intent.putExtra("patientUsername", username);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvDoctor, tvRoomBed;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvDoctor = itemView.findViewById(R.id.tv_doctor);
            tvRoomBed = itemView.findViewById(R.id.tv_room_bed);
            btnView = itemView.findViewById(R.id.btn_view);
        }
    }
}
