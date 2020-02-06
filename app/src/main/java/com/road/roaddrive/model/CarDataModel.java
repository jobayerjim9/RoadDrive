package com.road.roaddrive.model;

public class CarDataModel {
    private MyLatLng source,destination;
    private String sourceName,destinationName,hoursRequired,carType,details,uid,key,status;
    private TimeStamp timeStamp;
    private int carRequired;

    public CarDataModel() {
    }

    public CarDataModel(MyLatLng source, MyLatLng destination, String sourceName, String destinationName, String hoursRequired, String carType, String details, TimeStamp timeStamp, int carRequired,String uid) {
        this.source = source;
        this.destination = destination;
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.hoursRequired = hoursRequired;
        this.carType = carType;
        this.details = details;
        this.timeStamp = timeStamp;
        this.carRequired = carRequired;
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

    public String getHoursRequired() {
        return hoursRequired;
    }

    public void setHoursRequired(String hoursRequired) {
        this.hoursRequired = hoursRequired;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getCarRequired() {
        return carRequired;
    }

    public void setCarRequired(int carRequired) {
        this.carRequired = carRequired;
    }
}
