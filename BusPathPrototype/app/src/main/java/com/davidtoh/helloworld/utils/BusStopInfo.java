package com.davidtoh.helloworld.utils;

/**
 * Created by dylan on 3/3/15.
 * helper class to hold information to make expandableListViews on BusStopStatistics actitivy
 */
public class BusStopInfo {
	String stopName;
	String stopID;
	double longitude;
	double latitude;

	public BusStopInfo(String stopName, String stopID, double longitude, double latitude) {
		this.stopName = stopName;
		this.stopID = stopID;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public String getStopName() {
		return this.stopName;
	}

	public String getStopID(){
		return this.stopID;
	}

	public double getLongitude(){
		return longitude;
	}

	public double getLatitude(){
		return latitude;
	}
}
