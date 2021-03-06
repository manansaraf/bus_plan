package com.busplan.core_activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.busplan.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by davidtoh on 2/21/15.
 * this activity takes a start, destination, and optional time and date and uses the API to get
 * routes from one stop to the other
 */
public class TripPlannerActivity extends FragmentActivity {
	private DatePickerDialog datePickerDialog;
	private TimePickerDialog timePickerDialog;
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat timeFormatter;
    /**
     * This function gets called when the activity is made, it makes sure the calling activity
     * passed it a stop to look up
     *
     * @param savedInstanceState
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_planner);
		Intent intent = getIntent();
		if (intent.hasExtra("startStopName")) {
			EditText editText = (EditText) findViewById(R.id.sourceDest);
			editText.setText(intent.getStringExtra("startStopName"));
		}
		if (intent.hasExtra("endStopName")) {
			EditText editText = (EditText) findViewById(R.id.endDest);
			editText.setText(intent.getStringExtra("endStopName"));
		}

		dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
		setDateField();
		setTimeField();
	}

    /**
     * This function calls the search stop activity when the start stop edit text field is pressed
     * @param view, the button view
     */
	public void startNameFiller(View view) {
		EditText editText = (EditText) findViewById(R.id.endDest);
		String endStop = editText.getText().toString();
		Intent intent = new Intent(TripPlannerActivity.this, SearchStopsTripPlannerActivity.class);
		intent.putExtra("endStopName", endStop);
		intent.putExtra("stop", 1);
		startActivity(intent);

	}
    /**
     * This function calls the search stop activity when the end stop edit text field is pressed
     * @param view, the button view
     */
	public void endNameFiller(View view) {
		Intent intent = new Intent(TripPlannerActivity.this, SearchStopsTripPlannerActivity.class);
		EditText editText = (EditText) findViewById(R.id.sourceDest);
		intent.putExtra("startStopName", editText.getText().toString());
		intent.putExtra("stop", 2);
		startActivity(intent);

	}

    /**
     * This function retrieves all the information gathered in this page such as the start stop,
     * the end stop, the date and the time if given and then sends it to the TripPlannerResultActivity
     * which then shows the planned trips.
     * All this happens when the submit button is pressed
     * @param view, the submit button view
     */
	public void submitTripPlan(View view) {
		Intent intent = new Intent(TripPlannerActivity.this, TripPlannerResultActivity.class);

		EditText editText = (EditText) findViewById(R.id.sourceDest);
		EditText editText2 = (EditText) findViewById(R.id.endDest);
		String start = editText.getText().toString();
		String end = editText2.getText().toString();

		if (start.length() > 0 && end.length() > 0) {
			intent.putExtra("startStopName", editText.getText().toString());
			intent.putExtra("endStopName", editText2.getText().toString());
			EditText time = (EditText) findViewById(R.id.time);
			String Time = time.getText().toString();
			if (!Time.equals("")) {
				intent.putExtra("time", Time);
			}
			EditText date = (EditText) findViewById(R.id.date);
			String Date = date.getText().toString();
			if (!Date.equals("")) {
				intent.putExtra("date", Date);
			}
			startActivity(intent);
		} else {
			String message = "Missing ";
			if (start.length() == 0)
				message += "start stop, ";
			if (end.length() == 0)
				message += "end stop, ";
			message = message.substring(0, message.length() - 2);
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}
	}

	public void showDatePickerDialog(View v) {
		datePickerDialog.show();
	}

	public void showTimePickerDialog(View v) {
		timePickerDialog.show();
	}

    /**
     * This function creates a timePickerDialog for the user to enter the time he wants
     */
	private void setTimeField() {
		final EditText timeEdit = (EditText) findViewById(R.id.time);

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
     * This function creates a datePickerDialog for the user to enter the time he wants
     */
	private void setDateField() {
		final EditText dateEdit = (EditText) findViewById(R.id.date);

		Calendar newCalendar = Calendar.getInstance();

		datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar newDate = Calendar.getInstance();
				newDate.set(year, monthOfYear, dayOfMonth);
				dateEdit.setText(dateFormatter.format(newDate.getTime()));
			}

		}, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
	}

}
