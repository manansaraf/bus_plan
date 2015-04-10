package com.davidtoh.helloworld.utils;

/**
 * Created by dylan on 2/27/15.
 * this class saves information form the stop JSON to be displayed on the BusStopStatistics activity
 */
public class BusRouteInfo {
	String busName = null;
	int timeExpected = 0;
	String stopID = "";
    String shape_id=null;
    int vehicleID=0;
    String routeColor=null;

	public BusRouteInfo(String busName, int timeExpected, String stopID,
                        String shape_id,int vehicleID, String routeColor) {
		this.busName = busName;
		this.timeExpected = timeExpected;
		this.stopID = stopID;
        this.shape_id=shape_id;
        this.vehicleID = vehicleID;
        this.routeColor = routeColor;
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
    public int getVehicleID()
    {
        return vehicleID;
    }
    public String getShape_id(){
        return shape_id;
    }
    public String getRouteColor(){
        return routeColor;
    }


}
