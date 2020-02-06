package com.road.roaddrive.model;

public class BikeDataModel {
    private MyLatLng source,destination;
    private String sourceName,destinationName,hoursType,additional,uid,key,status;
    private TimeStamp timeStamp;
    private int bikeRequired;
    public BikeDataModel() {
    }

    public BikeDataModel(MyLatLng source, MyLatLng destination, String sourceName, String destinationName, String hoursType, String additional, TimeStamp timeStamp, int bikeRequired, String uid) {
        this.source = source;
        this.destination = destination;
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.hoursType = hoursType;
        this.additional = additional;
        this.timeStamp = timeStamp;
        this.bikeRequired = bikeRequired;
        this.uid=uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public MyLatLng getSource() {
        return source;
    }

    public void setSource(MyLatLng source) {
        this.source = source;
    }

    public MyLatLng getDestination() {
        return destination;
    }

    public void setDestination(MyLatLng destination) {
        this.destination = destination;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getHoursType() {
        return hoursType;
    }

    public void setHoursType(String hoursType) {
        this.hoursType = hoursType;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getBikeRequired() {
        return bikeRequired;
    }

    public void setBikeRequired(int bikeRequired) {
        this.bikeRequired = bikeRequired;
    }
}
