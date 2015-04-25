package com.busplan.utils;

/**
 * Created by manansaraf on 4/8/15.
 */
public class LocationInfo {
	double longitude;
	double latitude;

	public LocationInfo(double latitude, double longitude) {
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
