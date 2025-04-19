package com.example.sarshms.User;
public class PrescriptionModel {
    private String prescription;
    private String labTest;
    private String appointmentDate;

    public PrescriptionModel(String prescription, String labTest, String appointmentDate) {
        this.prescription = prescription;
        this.labTest = labTest;
        this.appointmentDate = appointmentDate;
    }

    public String getPrescription() { return prescription; }
    public String getLabTest() { return labTest; }
    public String getAppointmentDate() { return appointmentDate; }
}
