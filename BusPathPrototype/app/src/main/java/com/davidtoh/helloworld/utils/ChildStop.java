package com.davidtoh.helloworld.utils;

/**
 * Created by dylan on 3/3/15.
 * helper class to hold information to make expandableListViews on BusStopStatistics actitivy
 */
public class ChildStop {
	String stopName;
	String stopID;

	public ChildStop(String stopName, String stopID) {
		this.stopName = stopName;
		this.stopID = stopID;
	}

	public String getStopName() {
		return this.stopName;
	}

	public String getStopID(){
		return this.stopID;
	}
}
