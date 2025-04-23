package com.example.sarshms.HeadStaff;

public class AdmittedPatient {
    private String username, doctorEmail, room, bed, hospitalUsername;

    public AdmittedPatient() {} // Required for Firestore

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getBed() { return bed; }
    public void setBed(String bed) { this.bed = bed; }

    public String getHospitalUsername() { return hospitalUsername; }
    public void setHospitalUsername(String hospitalUsername) { this.hospitalUsername = hospitalUsername; }
}
