package com.busplan.core_activities;

/**
 * Created by dylan on 3/03/15.
 * This activity is a helper activity for the scheduler and handles the user input for each alarm
 */

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.busplan.R;
import com.busplan.database.AlarmDAO;
import com.busplan.utils.AlarmHandler;
import com.busplan.utils.AlarmInfo;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Locale;

public class SchedulerAddReminderActivity extends Activity {
	private TimePickerDialog timePickerDialog;
	private SimpleDateFormat timeFormatter;
	private AlarmDAO alarmDAO;

	/**
	 * This function gets called when the activity is made, it sets up the activity depending on if
	 * the user is adding a new alarm or trying to edit an existing alarm
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduler_add_reminder);

		Intent intent = getIntent();
		if (intent.hasExtra("endStopName")) {
			EditText editText = (EditText) findViewById(R.id.dest);
			editText.setText(intent.getStringExtra("endStopName"));
			getIntent().removeExtra("endStopName");
		}
		if (intent.hasExtra("alarm")) {
			populateFields();
		}
		timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
		setTimeField();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
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

	public void showTimePickerDialog(View v) {
		timePickerDialog.show();
	}

	/**
	 * Called when the user is inputting the destination field for the alarm, it takes them to
	 * the SearchStopsTripPlannerActivity for them to pick a stop and then returns
	 *
	 * @param view
	 */
	public void endNameFiller(View view) {
		Intent intent = new Intent(SchedulerAddReminderActivity.this, SearchStopsTripPlannerActivity.class);
		EditText editText = (EditText) findViewById(R.id.dest);
		intent.putExtra("startStopName", editText.getText().toString());
		intent.putExtra("stop", 2);
		intent.putExtra("classname", "Scheduler");
		startActivity(intent);
	}

	private void setTimeField() {
		final EditText timeEdit = (EditText) findViewById(R.id.arrivetime);

		Calendar newCalendar = Calendar.getInstance();

		timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

			public void onTimeSet(TimePicker view, int hour, int minute) {
				Calendar newDate = Calendar.getInstance();
				newDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH),
						newDate.get(Calendar.DAY_OF_MONTH), hour, minute);
				timeEdit.setText(timeFormatter.format(newDate.getTime()));
			}

		}, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);
	}

	/**
	 * Checks to make sure all parameters are included and if so then adds or edits the alarm and
	 * displays a message to the user. Returns to the main scheduler activity on success.
	 *
	 * @param view
	 */
	public void addAlarm(View view) {

		EditText destText = (EditText) findViewById(R.id.dest);
		String destination = destText.getText().toString();
		EditText timeText = (EditText) findViewById(R.id.arrivetime);
		String time = timeText.getText().toString();
		String days = makeDayString();
		CheckBox repeatBox = (CheckBox) findViewById(R.id.checkBox);
		Boolean repeat = repeatBox.isChecked();

		if (destination.length() > 0 && time.length() > 0 && days.length() > 0) {
			alarmDAO = new AlarmDAO(this);
			alarmDAO.open();
			AlarmInfo alarmInfo;
			if (!getIntent().hasExtra("alarm")) {
				alarmDAO.createAlarm(destination, time, days, String.valueOf(repeat));
				Toast.makeText(getApplicationContext(), "Alarm added", Toast.LENGTH_LONG).show();
				alarmInfo = alarmDAO.getLastAlarm();
			} else {
				int id = getIntent().getIntExtra("id", 0);
				alarmDAO.editAlarm(id, destination, time, days, String.valueOf(repeat));
				Toast.makeText(getApplicationContext(), "Alarm edited", Toast.LENGTH_LONG).show();
				getIntent().removeExtra("alarm");
				alarmInfo = alarmDAO.getAlarmById(id);
			}
			AlarmHandler alarmHandler = new AlarmHandler(this);
			alarmHandler.setThisAlarm(alarmInfo);
			finish();
			Intent intent = new Intent(SchedulerAddReminderActivity.this, SchedulerActivity.class);
			startActivity(intent);

		} else {
			String message = "Missing ";
			if (destination.length() == 0)
				message += "destination, ";
			if (time.length() == 0)
				message += "a time, ";
			if (days.length() == 0)
				message += "days of the week, ";
			message = message.substring(0, message.length() - 2);
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}
	}

	private String makeDayString() {
		ToggleButton[] dayArray = new ToggleButton[]{
				(ToggleButton) findViewById(R.id.sunday),
				(ToggleButton) findViewById(R.id.monday),
				(ToggleButton) findViewById(R.id.tuesday),
				(ToggleButton) findViewById(R.id.wednesday),
				(ToggleButton) findViewById(R.id.thursday),
				(ToggleButton) findViewById(R.id.friday),
				(ToggleButton) findViewById(R.id.saturday)};

		String dayString = "";
		for (ToggleButton day : dayArray) {
			if (day.isChecked()) {
				dayString += day.getHint() + " ";
			}
		}
		return dayString;
	}

	private void populateFields() {
		int alarmpos = getIntent().getIntExtra("alarm", 0);

		alarmDAO = new AlarmDAO(this);
		alarmDAO.open();
		AlarmInfo alarmInfo = alarmDAO.getAlarm(alarmpos);

		EditText destinationText = (EditText) findViewById(R.id.dest);
		destinationText.setText(alarmInfo.getDestination());
		EditText timeText = (EditText) findViewById(R.id.arrivetime);
		timeText.setText(alarmInfo.getTime());
		CheckBox repeatBox = (CheckBox) findViewById(R.id.checkBox);
		repeatBox.setChecked(Boolean.valueOf(alarmInfo.getRepeat()));

		ToggleButton sunday = (ToggleButton) findViewById(R.id.sunday);
		ToggleButton monday = (ToggleButton) findViewById(R.id.monday);
		ToggleButton tuesday = (ToggleButton) findViewById(R.id.tuesday);
		ToggleButton wednesday = (ToggleButton) findViewById(R.id.wednesday);
		ToggleButton thursday = (ToggleButton) findViewById(R.id.thursday);
		ToggleButton friday = (ToggleButton) findViewById(R.id.friday);
		ToggleButton saturday = (ToggleButton) findViewById(R.id.saturday);
		String days = alarmInfo.getDay();

		if (days.contains("sunday"))
			sunday.setChecked(true);
		if (days.contains("monday"))
			monday.setChecked(true);
		if (days.contains("tuesday"))
			tuesday.setChecked(true);
		if (days.contains("wednesday"))
			wednesday.setChecked(true);
		if (days.contains("thursday"))
			thursday.setChecked(true);
		if (days.contains("friday"))
			friday.setChecked(true);
		if (days.contains("saturday"))
			saturday.setChecked(true);
		getIntent().putExtra("id", alarmInfo.getID());
	}
}

