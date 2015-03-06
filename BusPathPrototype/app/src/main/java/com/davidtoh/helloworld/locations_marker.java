package com.davidtoh.helloworld;


public class locations_marker {
    double longitude;
    double latitude;
    String stopNames;
    public locations_marker(double latitude, double longitude, String stopNames){
        this.longitude=longitude;
        this.latitude = latitude;
        this.stopNames = stopNames;
    }
    public double getLongitude(){
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
    public String getStopNames(){
        return stopNames;
    }
}
