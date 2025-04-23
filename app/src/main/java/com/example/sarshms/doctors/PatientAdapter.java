package com.example.sarshms.doctors;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sarshms.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
    private List<Patient> patientList;
    private String hospitalUsername , doctorEmail ;

    public PatientAdapter(List<Patient> patientList ,String hospitalUsername , String doctorEmail) {
        this.patientList = patientList;
        this.hospitalUsername = hospitalUsername;
        this.doctorEmail =  doctorEmail;
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);

        holder.textViewUsername.setText("Username: " + patient.getUsername());
        holder.textViewRoom.setText("Room: " + patient.getRoom());
        holder.textViewBed.setText("Bed: " + patient.getBed());
        holder.textViewStatus.setText("Status: " + patient.getStatus());

        holder.btnViewReport.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PatientDetailsActivity.class);
            intent.putExtra("hospitalUsername", hospitalUsername);
            intent.putExtra("doctorEmail" ,doctorEmail);
            intent.putExtra("patientUsername", patient.getUsername());  // Use proper key
            v.getContext().startActivity(intent);
        });

        holder.btnDischarge.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String patientUsername = patient.getUsername();
            String room = patient.getRoom();
            String bed = patient.getBed();

            // 1. Get Admitted patient data
            db.collection("Hospitals")
                    .document(hospitalUsername)
                    .collection("AdmittedPatients")
                    .document(patientUsername)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Patient data
                            var patientData = documentSnapshot.getData();

                            // 2. Set dischargeTimestamp & admissionTo in user-side hospital subcollection
                            // 2. Set dischargeTimestamp, admissionTo, room, bed, and admission status in user-side hospital subcollection
                            DocumentReference userRef = db.collection("Users").document(patientUsername);
                            DocumentReference hospitalRef = userRef.collection("Hospitals").document(hospitalUsername);

                            // Update parent document: "status of admission"
                            userRef.update("statusOfAdmission", "Discharged")
                                    .addOnSuccessListener(aVoid -> {
                                        // Now update the nested hospital document with more detailed info
                                        hospitalRef.update(
                                                "dischargeTimestamp", com.google.firebase.Timestamp.now(),
                                                "admissionTo", doctorEmail,
                                                "status", "Discharged"
                                        ).addOnSuccessListener(aVoid2 -> {
                                            Log.d("Firestore", "Hospital sub-doc updated with discharge info");
                                        }).addOnFailureListener(e -> {
                                            Log.e("Firestore", "Failed to update hospital sub-doc", e);
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Failed to update user status of admission", e);
                                    });



                            // 3. Move to DischargedPatients
                            db.collection("Hospitals")
                                    .document(hospitalUsername)
                                    .collection("DischargedPatients")
                                    .document(patientUsername)
                                    .set(patientData)
                                    .addOnSuccessListener(aVoid -> {

                                        // 4. Delete from AdmittedPatients including subcollections like Prescriptions
                                        deleteDocumentWithSubcollections(
                                                db,
                                                "Hospitals/" + hospitalUsername + "/AdmittedPatients/" + patientUsername
                                        );

                                        // 5. Set room-bed to Available
                                        db.collection("Hospitals")
                                                .document(hospitalUsername)
                                                .collection("ROOMS")
                                                .document(room)
                                                .update(bed, "Available");

                                        Toast.makeText(v.getContext(), "Patient discharged successfully", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
        });


    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername, textViewRoom, textViewBed, textViewStatus;
        Button btnViewReport, btnDischarge;

        public PatientViewHolder(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewPatientUsername);
            textViewRoom = itemView.findViewById(R.id.textViewRoom);
            textViewBed = itemView.findViewById(R.id.textViewBed);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            btnViewReport = itemView.findViewById(R.id.btnViewReport);
            btnDischarge = itemView.findViewById(R.id.btnDischarge);
        }
    }

    private void deleteDocumentWithSubcollections(FirebaseFirestore db, String docPath) {
        db.collection(docPath.substring(0, docPath.lastIndexOf('/')))
                .document(docPath.substring(docPath.lastIndexOf('/') + 1))
                .collection("Prescriptions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (var doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().delete();  // delete each sub-doc
                    }

                    // Now delete the main document after subcollection cleared
                    db.document(docPath).delete();
                });
    }

}
