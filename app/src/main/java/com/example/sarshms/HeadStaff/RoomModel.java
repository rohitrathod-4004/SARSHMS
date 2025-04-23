package com.example.sarshms.HeadStaff;

import java.util.HashMap;
import java.util.Map;

public class RoomModel {
    private String roomNumber;      // Represents Room Number (e.g. Room1, Room2)
    private int totalBeds;          // Total beds in the room
    private Map<String, String> beds;  // A map of bed ids to their statuses (Available, Occupied, etc.)

    // Default constructor
    public RoomModel() {}

    // Constructor
    public RoomModel(String roomNumber, int totalBeds, Map<String, String> beds) {
        this.roomNumber = roomNumber;
        this.totalBeds = totalBeds;
        this.beds = beds;
    }

    // Add this constructor to RoomModel.java


    // Getter and Setter methods
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getTotalBeds() {
        return totalBeds;
    }

    public void setTotalBeds(int totalBeds) {
        this.totalBeds = totalBeds;
    }

    public Map<String, String> getBeds() {
        return beds;
    }

    public void setBeds(Map<String, String> beds) {
        this.beds = beds;
    }
}
