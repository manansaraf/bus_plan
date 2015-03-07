package com.davidtoh.helloworld.utils;


public class locations_marker {
    double longitude;
    double latitude;
    String stopName;

    public locations_marker(double latitude, double longitude, String stopName){
        this.longitude = longitude;
        this.latitude = latitude;
        this.stopName = stopName;
    }

    public double getLongitude(){
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
    public String getStopName(){
        return stopName;
    }
}
