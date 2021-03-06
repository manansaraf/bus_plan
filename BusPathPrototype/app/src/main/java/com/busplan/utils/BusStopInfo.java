package com.busplan.utils;

/**
 * Created by dylan on 3/3/15.
 * Helper class to pass information to and from the database for Bus Stops
 */
public class BusStopInfo {
	String stopName;
	String stopID;
	double longitude;
	double latitude;

	public BusStopInfo(String stopName, String stopID, double latitude, double longitude) {
		this.stopName = stopName;
		this.stopID = stopID;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public BusStopInfo() {
	}

	public String getStopName() {
		return this.stopName;
	}

	@Override
	public String toString() {
		return this.getStopName();
	}

	public String getStopID() {
		return this.stopID;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setStopName(String name) {
		this.stopName = name;
	}

	public void setStopID(String ID) {
		this.stopID = ID;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
