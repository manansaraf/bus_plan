package com.davidtoh.helloworld.core_activities;

/**
 * Created by davidtoh on 2/21/15.
 */

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.davidtoh.helloworld.R;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Locale;

public class SchedulerManageActivity extends Activity implements AdapterView.OnItemClickListener {
    private TimePickerDialog timePickerDialog;
    private SimpleDateFormat timeFormatter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduler_add_reminder);

        Intent intent = getIntent();
        if (intent.hasExtra("endStopName")) {
            EditText editText = (EditText) findViewById(R.id.dest);
            editText.setText(intent.getStringExtra("endStopName"));
        }
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        setTimeField();

        /*

		Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
				R.array.stops, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
		spinner1.setAdapter(adapter1);

		Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
				R.array.stops, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
		spinner2.setAdapter(adapter2);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public void onItemClick(AdapterView<?> l, View v, int position, long id) {

		Intent intent = new Intent();
		intent.setClass(this, BusStopStatisticsActivity.class);
		intent.putExtra("position", position);

		intent.putExtra("id", id);
		startActivity(intent);
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
    public void endNameFiller(View view) {
        Intent intent = new Intent(SchedulerManageActivity.this, SearchStopsTripPlannerActivity.class);
        EditText editText = (EditText) findViewById(R.id.dest);
        intent.putExtra("startStopName", editText.getText().toString());
        intent.putExtra("stop", 2);
        intent.putExtra("classname","Scheduler");
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

        }, newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), false);
    }
    public void addAlarm(View view){

        EditText destination = (EditText) findViewById(R.id.dest);
        String end = destination.getText().toString();
        EditText time = (EditText) findViewById(R.id.arrivetime);
        String Time = time.getText().toString();
        makeDayString();

        if (end.length() > 0 && Time.length() >0 ) {




        }

    }
    private String makeDayString(){
        ToggleButton sunday = (ToggleButton)findViewById(R.id.sunday);
        ToggleButton monday = (ToggleButton)findViewById(R.id.monday);
        ToggleButton tuesday = (ToggleButton)findViewById(R.id.tuesday);
        ToggleButton wednesday = (ToggleButton)findViewById(R.id.wednesday);
        ToggleButton thursday = (ToggleButton)findViewById(R.id.thursday);
        ToggleButton friday = (ToggleButton)findViewById(R.id.friday);
        ToggleButton saturday = (ToggleButton)findViewById(R.id.saturday);
        ToggleButton[] dayArray = new ToggleButton[] {sunday, monday,tuesday, wednesday, thursday,
                friday, saturday};
        String dayString = "";
        for(ToggleButton day : dayArray){
            if(day.isChecked()){
                dayString += day.getHint();
                Log.d("check string", dayString);
            }

        }

        return dayString;
    }
}

