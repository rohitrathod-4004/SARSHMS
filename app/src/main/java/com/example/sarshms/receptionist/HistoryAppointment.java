package com.example.sarshms.receptionist;

public class HistoryAppointment {
    private String patientName;
    private String date;
    private String status;

    public HistoryAppointment() { }

    public HistoryAppointment(String patientName, String date, String status) {
        this.patientName = patientName;
        this.date = date;
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
