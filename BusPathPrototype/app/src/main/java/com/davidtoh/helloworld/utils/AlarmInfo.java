package com.davidtoh.helloworld.utils;

import java.util.Calendar;

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

	public AlarmInfo() {
	}

	@Override
	public String toString() {
		String[] time_a = this.getTime().split(":");
		int hours = Integer.parseInt(time_a[0]);
		int minutes = Integer.parseInt(time_a[1]);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hours);
		c.set(Calendar.MINUTE, minutes);
		String time = DateParser.toString(c.getTime());
		return this.getDestination() + "  |  " + time;
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
