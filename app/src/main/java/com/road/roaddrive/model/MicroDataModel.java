package com.road.roaddrive.model;

public class MicroDataModel {
    private MyLatLng source,destination;
    private String sourceName,destinationName,hoursType,microType,seatType,additional,uid,key,status;
    private TimeStamp timeStamp;
    private int microRequired;

    public MicroDataModel() {
    }

    public MicroDataModel(MyLatLng source, MyLatLng destination, String sourceName, String destinationName, String hoursType, String microType, String seatType, String additional, TimeStamp timeStamp, int microRequired,String uid) {
        this.source = source;
        this.destination = destination;
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.hoursType = hoursType;
        this.microType = microType;
        this.seatType = seatType;
        this.additional = additional;
        this.timeStamp = timeStamp;
        this.microRequired = microRequired;
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

    public String getMicroType() {
        return microType;
    }

    public void setMicroType(String microType) {
        this.microType = microType;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
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

    public int getMicroRequired() {
        return microRequired;
    }

    public void setMicroRequired(int microRequired) {
        this.microRequired = microRequired;
    }
}
