package com.road.roaddrive.model;

public class FixTripDetailsModel {
    private double sourceLat,sourceLng,desLat,desLng;
    private int fare;
    private String requestorUid;
    private boolean accept;
    public FixTripDetailsModel() {
    }

    public FixTripDetailsModel(double sourceLat, double sourceLng, double desLat, double desLng, int fare, String requestorUid) {
        this.sourceLat = sourceLat;
        this.sourceLng = sourceLng;
        this.desLat = desLat;
        this.desLng = desLng;
        this.fare = fare;
        this.requestorUid = requestorUid;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public String getRequestorUid() {
        return requestorUid;
    }

    public void setRequestorUid(String requestorUid) {
        this.requestorUid = requestorUid;
    }

    public double getSourceLat() {
        return sourceLat;
    }

    public void setSourceLat(double sourceLat) {
        this.sourceLat = sourceLat;
    }

    public double getSourceLng() {
        return sourceLng;
    }

    public void setSourceLng(double sourceLng) {
        this.sourceLng = sourceLng;
    }

    public double getDesLat() {
        return desLat;
    }

    public void setDesLat(double desLat) {
        this.desLat = desLat;
    }

    public double getDesLng() {
        return desLng;
    }

    public void setDesLng(double desLng) {
        this.desLng = desLng;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }
}
