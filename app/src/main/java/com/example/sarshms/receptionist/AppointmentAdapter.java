package com.example.sarshms.receptionist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private String hospitalId;
    private List<Appointment> appointmentList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AppointmentAdapter(Context context, List<Appointment> appointmentList, String hospitalId) {
        this.context = context;
        this.hospitalId = hospitalId;

        // Sort appointments by date and time (earliest first)
        appointmentList.sort((a1, a2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); // Adjust format if needed
                return Objects.requireNonNull(sdf.parse(a1.getDate() + " " + a1.getTime()))
                        .compareTo(sdf.parse(a2.getDate() + " " + a2.getTime()));
            } catch (Exception e) {
                return 0;
            }
        });

        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvSerial.setText("S.No: " + (position + 1));
        holder.tvDoctor.setText("Doctor: " + appointment.getDoctor());
        holder.tvDate.setText("Date: " + appointment.getDate());
        holder.tvTime.setText("Time: " + appointment.getTime());
        holder.tvUser.setText("User: " + appointment.getUserEmail());

        holder.btnSend.setOnClickListener(v -> sendAppointmentToDoctor(appointment, position));
        holder.btnReject.setOnClickListener(v -> rejectAppointment(appointment, position));
    }

    private void sendAppointmentToDoctor(Appointment appointment, int position) {
        String selectedDoctor = appointment.getDoctor();

        db.collection("Hospitals").get().addOnSuccessListener(hospitalSnapshots -> {
            for (QueryDocumentSnapshot hospitalDoc : hospitalSnapshots) {
                CollectionReference doctorsRef = db.collection("Hospitals")
                        .document(hospitalDoc.getId())
                        .collection("Doctors");

                doctorsRef.document(selectedDoctor)
                        .get()
                        .addOnSuccessListener(docSnapshot -> {
                            if (docSnapshot.exists()) {
                                doctorsRef.document(selectedDoctor)
                                        .collection("Appointments")
                                        .document("TempAppointment")
                                        .set(appointment)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context, "Appointment sent to doctor", Toast.LENGTH_SHORT).show();
                                            updateOriginalAppointmentStatus(appointment, "sent", position);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(context, "Error sending to doctor", Toast.LENGTH_SHORT).show());
                            }
                        });
            }
        });
    }

    private void rejectAppointment(Appointment appointment, int position) {
        updateOriginalAppointmentStatus(appointment, "rejected", position);
    }

    private void updateOriginalAppointmentStatus(Appointment appointment, String status, int position) {
        db.collection("Hospitals")
                .document(hospitalId)
                .collection("Appointments")
                .whereEqualTo("doctor", appointment.getDoctor())
                .whereEqualTo("date", appointment.getDate())
                .whereEqualTo("time", appointment.getTime())
                .whereEqualTo("userEmail", appointment.getUserEmail())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(context, "No matching appointments found", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            doc.getReference().update("status", status)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Appointment marked as " + status, Toast.LENGTH_SHORT).show();
                                        appointmentList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, appointmentList.size()); // 👈 Important to update serial numbers
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Query failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerial, tvDoctor, tvDate, tvTime, tvUser;
        Button btnSend, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSerial = itemView.findViewById(R.id.tv_serial);
            tvDoctor = itemView.findViewById(R.id.tv_doctor);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvUser = itemView.findViewById(R.id.tv_user);
            btnSend = itemView.findViewById(R.id.btn_send);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
