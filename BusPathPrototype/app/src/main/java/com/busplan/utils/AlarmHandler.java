package com.busplan.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import com.busplan.database.AlarmDAO;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.Locale;

/**
 * Created by Elee on 2015-04-17.
 * This class is responsible for setting a system alarm using the alarm manager
 */
public class AlarmHandler {
	private Context context;
	AlarmDAO alarmDAO;

	public AlarmHandler(Context context) {
		this.context = context;
	}

	/**
	 * Used to set all of the alarms that occur on the current day that are in the database
	 */
	public void setTodaysAlarms() {
		setAlarms(getTodayAlarmList());
	}

	/**
	 * Used to set a specific alarm added by a user if it occurs later on the same day it was added
	 *
	 * @param alarm - the alarmInfo that was just added to the database
	 */
	public void setThisAlarm(AlarmInfo alarm) {
		List<AlarmInfo> alarmInfos = new ArrayList<>();
		alarmInfos.add(alarm);

		Calendar current = Calendar.getInstance();
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
		String weekDay = dayFormat.format(current.getTime()).toLowerCase();

		Calendar alarmCalendar = Calendar.getInstance();
		String[] time = alarm.getTime().split(":");
		int hours = Integer.parseInt(time[0]);
		int minutes = Integer.parseInt(time[1]);
		alarmCalendar.set(Calendar.HOUR_OF_DAY, hours);
		alarmCalendar.set(Calendar.MINUTE, minutes);

		if (alarm.getDay().contains(weekDay) && alarmCalendar.compareTo(current) > 0) {
			setAlarms(alarmInfos);
		}
	}

	private List<AlarmInfo> getTodayAlarmList() {
		String weekDay;
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
		Calendar calendar = Calendar.getInstance();
		weekDay = dayFormat.format(calendar.getTime()).toLowerCase();

		alarmDAO = new AlarmDAO(context);
		alarmDAO.open();
		return alarmDAO.getAlarmsByDay(weekDay);
	}

	private void setAlarms(List<AlarmInfo> alarms) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.SECOND, 0);
		for (int i = 0; i < alarms.size(); i++) {
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra("type", alarms.get(i).getID());
			String[] time = alarms.get(i).getTime().split(":");
			int hours = Integer.parseInt(time[0]);
			int minutes = Integer.parseInt(time[1]);
			c.set(Calendar.HOUR_OF_DAY, hours);
			c.set(Calendar.MINUTE, minutes);
			c.add(Calendar.MINUTE, -45);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarms.get(i).getID(), intent, 0);
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
		}
		removeDayFromNonRepeating(alarms);
	}

	private void removeDayFromNonRepeating(List<AlarmInfo> alarms) {
		String weekDay;
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
		Calendar calendar = Calendar.getInstance();
		weekDay = dayFormat.format(calendar.getTime()).toLowerCase();
		alarmDAO = new AlarmDAO(context);
		alarmDAO.open();
		for (AlarmInfo alarm : alarms) {
			if (!Boolean.valueOf(alarm.getRepeat())) {
				String days = alarm.getDay().replace(weekDay + " ", "");
				alarmDAO.editAlarm(alarm.getID(), alarm.getDestination(), alarm.getTime(), days,
						alarm.getRepeat());
			}
		}
	}
}
