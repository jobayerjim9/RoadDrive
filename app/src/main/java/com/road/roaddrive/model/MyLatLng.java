package com.road.roaddrive.model;

import com.google.android.gms.maps.model.LatLng;

public class MyLatLng {
    private double lat;
    private double lng;

    public MyLatLng() {
    }

    public MyLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    public MyLatLng(LatLng latLng)
    {
        this.lat=latLng.latitude;
        this.lng=latLng.longitude;
    }
    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}

