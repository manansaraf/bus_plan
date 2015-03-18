package com.davidtoh.helloworld.utils;

public class TripInfo {
    String startBusStop;
    String endBusStop;
    String startTime;
    String endTime;
    double walk_distance;
    String bus_name;

    public String getStartBusStop() {
        return startBusStop;
    }

    public String getEndBusStop() {
        return endBusStop;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public double getWalk_distance() {
        return walk_distance;
    }

    public String getBus_name() {
        return bus_name;
    }

    public TripInfo(String startBusStop,String endBusStop,String startTime,String endTime,
                    double walk_distance, String bus_name){
        this.startBusStop = startBusStop;
        this.endBusStop = endBusStop;
        this.startTime = startTime;
        this.endTime = endTime;
        this.walk_distance = walk_distance;
        this.bus_name = bus_name;
    }
}
