package com.davidtoh.helloworld.utils;

/**
 * Created by dylan on 2/27/15.
 * this class saves information form the stop JSON to be displayed on the BusStopStatistics activity
 */
public class BusRouteInfo {
	String busName = null;
	int timeExpected = 0;
	String stopID = "";

	public BusRouteInfo(String busName, int timeExpected, String stopID) {
		this.busName = busName;
		this.timeExpected = timeExpected;
		this.stopID = stopID;
	}

	public String getBusName() {
		return busName;
	}

	public int getTimeExpected() {
		return timeExpected;
	}

	public String getStopID() {
		return stopID;
	}
}
