package com.davidtoh.helloworld;

/**
 * Created by dylan on 2/27/15.
 */
public class BusStopInfo {
	String busName = null;
	int timeExpected = 0;

	public BusStopInfo(String busName, int timeExpected) {
		this.busName = busName;
		this.timeExpected = timeExpected;
	}

	public String getBusName() {
		return busName;
	}

	public int getTimeExpected() {
		return timeExpected;
	}
}
