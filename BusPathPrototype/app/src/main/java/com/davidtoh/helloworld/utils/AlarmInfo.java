package com.davidtoh.helloworld.utils;

/**
 * Created by dylan on 4/03/2015.
 * helper class to pass information to and from the database
 */
public class AlarmInfo {
	int id;
	String destination;
	String time;
	String day;
	String repeat;

	public AlarmInfo(int id, String destination, String time, String day, String repeat) {
		this.id = id;
		this.destination = destination;
		this.time = time;
		this.day = day;
		this.repeat = repeat;
	}

	public AlarmInfo() {
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

	public int getID() {
		return this.id;
	}

	public String getDestination() {
		return this.destination;
	}

	public String getTime() {
		return this.time;
	}

	public String getDay() {
		return this.day;
	}

	public String getRepeat() {
		return this.repeat;
	}
}
