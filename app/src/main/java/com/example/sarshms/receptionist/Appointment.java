package com.example.sarshms.receptionist;

public class Appointment {
    private String doctor;
    private String date;
    private String time;
    private String userEmail;

    public Appointment() { }

    public Appointment(String doctor, String date, String time, String userEmail) {
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.userEmail = userEmail;
    }

    public String getDoctor() { return doctor; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getUserEmail() { return userEmail; }
}
