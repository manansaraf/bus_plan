package com.busplan.core_activities;

/**
 * Created by dylan on 4/03/15.
 * This activity is the main activity of the scheduler feature. Here the scheduler can be turn off
 * and on and users can view and delete alarms.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.busplan.R;
import com.busplan.database.AlarmDAO;
import com.busplan.utils.AlarmInfo;
import com.busplan.utils.AlarmReceiver;

import java.util.Calendar;

public class SchedulerActivity extends Activity implements AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener {

	private ArrayAdapter<AlarmInfo> mAdapter;
	private AlarmDAO alarmDAO;

	/**
	 * This function gets called when the activity is made, it fetches the alarms from the database
	 * and makes them be displayed in the listView
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduler);

		ListView listview = (ListView) findViewById(R.id.alarmListView);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);

		alarmDAO = new AlarmDAO(this);
		alarmDAO.open();
		mAdapter = new ArrayAdapter<>(this, R.layout.scheduler_list_item,
				alarmDAO.getAllAlarms());
		listview.setAdapter(mAdapter);
	}

	/**
	 * Called after a pause state, clears the listView and redisplays the alarms. This is useful to
	 * update the view if any alarm was edited or deleted.
	 */
	@Override
	public void onResume() {
		super.onResume();
		mAdapter.clear();
		mAdapter.addAll(alarmDAO.getAllAlarms());
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_scheduler, menu);
		return true;
	}

	/**
	 * Called from the Add Reminder button, redirects user to the add reminder activity
	 *
	 * @param view
	 */
	public void toSchedulerAddReminder(View view) {
		Intent changeToScheduler = new Intent(view.getContext(), SchedulerAddReminderActivity.class);
		startActivityForResult(changeToScheduler, 0);
	}

	/**
	 * Called when user clicks an alarm in the listView, takes them to the edit page to edit that alarm
	 *
	 * @param av
	 * @param v
	 * @param position
	 * @param id
	 */
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(this, SchedulerAddReminderActivity.class);
		intent.putExtra("alarm", position);
		startActivity(intent);
	}

	/**
	 * Called when user long clicks an alarm in the listView, sets up the delete function for each
	 * alarm
	 *
	 * @param av
	 * @param v
	 * @param pos
	 * @param id
	 * @return
	 */
	public boolean onItemLongClick(final AdapterView<?> av, View v, final int pos, final long id) {

		final AlertDialog.Builder b = new AlertDialog.Builder(SchedulerActivity.this);
		b.setIcon(android.R.drawable.ic_dialog_alert);
		b.setMessage("Delete selected reminder?");
		b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				deleteAlarm(pos);
			}
		});
		b.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		b.show();
		return true;
	}

	private void deleteAlarm(int position) {
		alarmDAO = new AlarmDAO(this);
		alarmDAO.open();
		AlarmInfo alarmInfo = alarmDAO.getAlarm(position);
		removeAlarm(alarmInfo);
		alarmDAO.deleteAlarm(alarmInfo.getID());
		Toast.makeText(getApplicationContext(), "Alarm deleted", Toast.LENGTH_LONG).show();
		finish();
		startActivity(getIntent());
	}

	private void removeAlarm(AlarmInfo alarmInfo) {
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.putExtra("type", alarmInfo.getID());

		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmInfo.getID(), intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pendingIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_settings:
				//openSettings();
				return true;
			case R.id.action_on:
				setAlarm();
				return true;
			case R.id.action_off:
				cancelAlarm();
				return true;
			case R.id.action_help:
				Toast.makeText(getApplicationContext(), "Click a reminder to edit it, and click " +
							"and hold a reminder to delete it. To use, turn on Scheduler in Menu.",
							Toast.LENGTH_LONG).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void setAlarm() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("type",0);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
		Toast.makeText(getApplicationContext(), "Scheduler alarms turned on", Toast.LENGTH_LONG).show();
	}

	private void cancelAlarm() {
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.putExtra("type",0);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
		am.cancel(pendingIntent);
		Toast.makeText(getApplicationContext(), "Scheduler alarms turned off", Toast.LENGTH_LONG).show();
	}
}
