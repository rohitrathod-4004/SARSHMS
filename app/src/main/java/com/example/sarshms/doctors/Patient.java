package com.example.sarshms.doctors;

public class Patient {
    private String bed;
    private String doctorEmail;
    private String notes;
    private String room;
    private String status;
    private String username;

    public Patient() {
        // Required empty constructor for Firestore
    }

    public Patient(String bed, String doctorEmail, String notes, String room, String status, String username) {
        this.bed = bed;
        this.doctorEmail = doctorEmail;
        this.notes = notes;
        this.room = room;
        this.status = status;
        this.username = username;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
