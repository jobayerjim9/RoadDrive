package com.road.roaddrive.model;

public class AppData {
    private static TruckDataModel truckDataModel;
    private static DriverProfile driverProfile;
    private static CarDataModel carDataModel;
    private static MicroDataModel microDataModel;
    private static BikeDataModel bikeDataModel;

    public static BikeDataModel getBikeDataModel() {
        return bikeDataModel;
    }

    public static void setBikeDataModel(BikeDataModel bikeDataModel) {
        AppData.bikeDataModel = bikeDataModel;
    }

    public static MicroDataModel getMicroDataModel() {
        return microDataModel;
    }

    public static void setMicroDataModel(MicroDataModel microDataModel) {
        AppData.microDataModel = microDataModel;
    }

    public static TruckDataModel getTruckDataModel() {
        return truckDataModel;
    }

    public static void setTruckDataModel(TruckDataModel truckDataModel) {
        AppData.truckDataModel = truckDataModel;
    }

    public static DriverProfile getDriverProfile() {
        return driverProfile;
    }

    public static void setDriverProfile(DriverProfile driverProfile) {
        AppData.driverProfile = driverProfile;
    }

    public static CarDataModel getCarDataModel() {
        return carDataModel;
    }

    public static void setCarDataModel(CarDataModel carDataModel) {
        AppData.carDataModel = carDataModel;
    }
}
