package com.davidtoh.helloworld.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.core_activities.SchedulerPlannerActivity;

/**
 * Created by dylan on 4/17/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(context, SchedulerPlannerActivity.class);
		notificationIntent.putExtra("position", 1);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,
				notificationIntent,  0);


		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Notification note = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_action_search)
				.setContentTitle("Alarm Fired")
				.setContentText("Events To be Performed")
				.setSound(alarmSound)
				.setWhen(when)
				.setAutoCancel(true)
				.setContentIntent(pendingIntent)
				.build();
		notificationManager.notify(0, note);
	}
}
