package com.davidtoh.helloworld.core_activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.davidtoh.helloworld.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by davidtoh on 2/21/15.
 */
public class TripPlannerActivity extends FragmentActivity {
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_planner);
        Intent intent = getIntent();
        if(intent.hasExtra("startStopName")){
            EditText editText = (EditText) findViewById(R.id.sourceDest);
            editText.setText(intent.getStringExtra("startStopName"));
        }
        if(intent.hasExtra("endStopName")){
            EditText editText = (EditText) findViewById(R.id.endDest);
            editText.setText(intent.getStringExtra("endStopName"));
        }

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        setDateField();
        setTimeField();
//        Spinner dateSpinner = (Spinner) findViewById(R.id.dates);
//// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
//                R.array.stops, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        dateSpinner.setAdapter(adapter1);
//
//        Spinner timeSpinner = (Spinner) findViewById(R.id.times);
//// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
//                R.array.stops, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        timeSpinner.setAdapter(adapter2);
    }
    public void startNameFiller(View view){
        EditText editText = (EditText) findViewById(R.id.endDest);
        String endStop = editText.getText().toString();
        Intent intent = new Intent(TripPlannerActivity.this, SearchStopsTripPlannerActivity.class);
        intent.putExtra("endStopName",endStop);
        intent.putExtra("stop",1);
        startActivity(intent);

    }
    public void endNameFiller(View view){
        Intent intent = new Intent(TripPlannerActivity.this, SearchStopsTripPlannerActivity.class);
        EditText editText = (EditText) findViewById(R.id.sourceDest);
        intent.putExtra("startStopName",editText.getText().toString());
        intent.putExtra("stop",2);
        startActivity(intent);

    }
    public void submitTripPlan(View view){
        Intent intent = new Intent(TripPlannerActivity.this, TripPlannerResultActivity.class);
        //TODO Send a stop id from the database
        EditText editText = (EditText) findViewById(R.id.sourceDest);
        intent.putExtra("startStopName",editText.getText().toString());
        EditText editText2 = (EditText) findViewById(R.id.endDest);
        intent.putExtra("endStopName",editText2.getText().toString());
        EditText time = (EditText) findViewById(R.id.time);
        String Time = time.getText().toString();
        if(!Time.equals("")){
            intent.putExtra("time",Time);
        }
        EditText date = (EditText) findViewById(R.id.date);
        String Date = date.getText().toString();
        if(!Date.equals("")){
            intent.putExtra("date",Date);
        }
        startActivity(intent);
    }

    public void showDatePickerDialog(View v) {
        datePickerDialog.show();
    }
    public void showTimePickerDialog(View v) {
        timePickerDialog.show();
    }
    private void setTimeField() {
        final EditText timeEdit = (EditText) findViewById(R.id.time);

        Calendar newCalendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int hour, int minute) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(newDate.get(Calendar.YEAR),newDate.get(Calendar.MONTH),
                        newDate.get(Calendar.DAY_OF_MONTH),hour,minute);
                Log.v("time",newDate.getTime().toString());
                timeEdit.setText(timeFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), false);
    }
    private void setDateField() {
        final EditText dateEdit = (EditText) findViewById(R.id.date);

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateEdit.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

}
