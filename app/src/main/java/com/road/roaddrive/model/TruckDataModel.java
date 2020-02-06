package com.road.roaddrive.model;

public class TruckDataModel {
    private int totalTruck,labour;
    private MyLatLng source,destination;
    private TimeStamp timeStamp;
    private String vehicleType,vehicleSize,itemType,productDescription,loadLocation,unloadLocation,uid,status;
    private String key;
    public TruckDataModel() {
    }

    public TruckDataModel(int totalTruck, int labour, MyLatLng source, MyLatLng destination, TimeStamp timeStamp, String vehicleType, String vehicleSize, String itemType, String productDescription, String loadLocation,String unloadLocation,String uid) {
        this.totalTruck = totalTruck;
        this.labour = labour;
        this.source = source;
        this.destination = destination;
        this.timeStamp = timeStamp;
        this.vehicleType = vehicleType;
        this.vehicleSize = vehicleSize;
        this.itemType = itemType;
        this.productDescription = productDescription;
        this.loadLocation=loadLocation;
        this.unloadLocation=unloadLocation;
        this.uid=uid;
        this.status="Open";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLoadLocation() {
        return loadLocation;
    }

    public void setLoadLocation(String loadLocation) {
        this.loadLocation = loadLocation;
    }

    public String getUnloadLocation() {
        return unloadLocation;
    }

    public void setUnloadLocation(String unloadLocation) {
        this.unloadLocation = unloadLocation;
    }

    public int getLabour() {
        return labour;
    }

    public void setLabour(int labour) {
        this.labour = labour;
    }

    public int getTotalTruck() {
        return totalTruck;
    }

    public void setTotalTruck(int totalTruck) {
        this.totalTruck = totalTruck;
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

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleSize() {
        return vehicleSize;
    }

    public void setVehicleSize(String vehicleSize) {
        this.vehicleSize = vehicleSize;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
