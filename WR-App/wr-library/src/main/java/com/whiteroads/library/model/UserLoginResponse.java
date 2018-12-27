package com.whiteroads.library.model;

public class UserLoginResponse extends CommonResponse {
    private String userEmail;
    private int userId;
    private String vehicleNumber;
    private int frequency;
    private int uploadTimeLocation;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getUploadTimeLocation() {
        return uploadTimeLocation;
    }

    public void setUploadTimeLocation(int uploadTimeLocation) {
        this.uploadTimeLocation = uploadTimeLocation;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
