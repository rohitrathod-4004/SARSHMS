package com.example.sarshms.doctors;
import com.google.firebase.Timestamp;

public class Prescription {
    private String symptoms;
    private String diagnosis;
    private String medicines;
    private String notes;
    private Timestamp timestamp;

    public Prescription() {}

    public Prescription(String symptoms, String diagnosis, String medicines, String notes, Timestamp timestamp) {
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.medicines = medicines;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getMedicines() { return medicines; }
    public void setMedicines(String medicines) { this.medicines = medicines; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}