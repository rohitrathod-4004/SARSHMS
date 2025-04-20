package com.example.sarshms.receptionist;

public class Appointment {
    private String doctor;
    private String date;
    private String time;
    private String userEmail;
    private String username;

    public Appointment() { }

    public Appointment(String doctor, String date, String time, String userEmail, String username) {
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.userEmail = userEmail;
        this.username = username;
    }

    public String getDoctor() { return doctor; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getUserEmail() { return userEmail; }
    public String getUsername() { return username; }

}
