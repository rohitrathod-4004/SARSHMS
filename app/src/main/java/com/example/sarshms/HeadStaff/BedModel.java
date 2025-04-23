package com.example.sarshms.HeadStaff;

public class BedModel {
    private String roomId;
    private String bedId;
    private String status;

    public BedModel(String roomId, String bedId, String status) {
        this.roomId = roomId;
        this.bedId = bedId;
        this.status = status;
    }

    public String getRoomId() { return roomId; }
    public String getBedId() { return bedId; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
