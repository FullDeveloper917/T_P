package com.property.david.tp.Models;

/**
 * Created by goldc on 11/16/2017.
 */

public class Mark {
    private String userId;
    private double latitude;
    private double longitude;
    private String date;

    public Mark() {
        this.userId = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.date = "";
    }

    public Mark(Mark clone) {
        this.userId = clone.getUserId();
        this.latitude = clone.getLatitude();
        this.longitude = clone.getLongitude();
        this.date = clone.getDate();
    }

    public Mark(String topicId, String userId, double latitude, double longitude, String date) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
