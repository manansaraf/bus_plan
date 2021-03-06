package com.busplan.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.busplan.R;
import com.busplan.core_activities.SchedulerViewTripActivity;
import com.busplan.database.AlarmDAO;

import java.util.Calendar;

/**
 * Created by dylan on 4/17/15.
 * This class is responsible for creating the notifications for scheduler when a system alarm is
 * received
 */
public class AlarmReceiver extends BroadcastReceiver {

	/**
	 * When an alarm is triggered, this distinguishes if it is a reminder alarm or the scheduled
	 * repeating alarm that triggers to set the alarms for each day
	 *
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getIntExtra("type", 0) == 0) {
			runMidnightProcess(context);
		} else {
			sendNotificationForAlarm(context, intent.getIntExtra("type", 0));
		}

	}

	private void runMidnightProcess(Context context) {
		AlarmHandler alarmHandler = new AlarmHandler(context);
		alarmHandler.setTodaysAlarms();
	}

	private void sendNotificationForAlarm(Context context, int id) {
		AlarmDAO alarmDAO = new AlarmDAO(context);
		alarmDAO.open();
		AlarmInfo alarminfo = alarmDAO.getAlarmById(id);


		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(context, SchedulerViewTripActivity.class);
		notificationIntent.putExtra("id", id);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, id,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


		String[] time_a = alarminfo.getTime().split(":");
		int hours = Integer.parseInt(time_a[0]);
		int minutes = Integer.parseInt(time_a[1]);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hours);
		c.set(Calendar.MINUTE, minutes);
		String time = DateParser.toString(c.getTime());
		Notification note = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Scheduler Reminder")
				.setContentText(alarminfo.getDestination() + " " + time)
				.setSound(alarmSound)
				.setWhen(when)
				.setAutoCancel(true)
				.setContentIntent(pendingIntent)
				.build();
		notificationManager.notify(0, note);
	}
}
