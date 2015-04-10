package com.davidtoh.helloworld.core_activities;

/**
 * Created by dylan on 4/03/15.
 * main scheduler activity where alarms are listed
 */

import android.app.Activity;
import android.app.AlertDialog;
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

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.database.AlarmDAO;
import com.davidtoh.helloworld.utils.AlarmInfo;

public class SchedulerActivity extends Activity implements AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener {

	private ArrayAdapter<AlarmInfo> mAdapter;
	private AlarmDAO alarmDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduler);

		ListView listview = (ListView) findViewById(R.id.alarmListView);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);

		alarmDAO = new AlarmDAO(this);
		alarmDAO.open();
		mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
				alarmDAO.getAllAlarms());
		listview.setAdapter(mAdapter);
	}

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

	public void toSchedulerAddReminder(View view) {
		Intent changeToScheduler = new Intent(view.getContext(), SchedulerManageActivity.class);
		startActivityForResult(changeToScheduler, 0);
	}

	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(this, SchedulerManageActivity.class);
		intent.putExtra("alarm", position);
		startActivity(intent);
	}

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
		alarmDAO.deleteAlarm(alarmInfo.getID());
		Toast.makeText(getApplicationContext(), "Alarm deleted", Toast.LENGTH_LONG).show();
		finish();
		startActivity(getIntent());
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
