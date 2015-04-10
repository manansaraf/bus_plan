package com.davidtoh.helloworld.utils;

/**
 * Created by manansaraf on 4/8/15.
 */
public class Loc {
    double longitude;
    double latitude;

    public Loc(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

}
