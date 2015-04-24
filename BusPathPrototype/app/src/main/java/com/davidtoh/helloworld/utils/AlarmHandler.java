package com.davidtoh.helloworld.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import com.davidtoh.helloworld.database.AlarmDAO;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.Locale;

/**
 * Created by Elee on 2015-04-17.
 * Class that called from Alarm, and check database to push today's alarm.
 */
public class AlarmHandler {
	private Context context;
	AlarmDAO alarmDAO;

	public AlarmHandler(Context context) {
		this.context = context;
	}

	public void setTodaysAlarms() {
		setAlarms(getTodayAlarmList());
	}

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
