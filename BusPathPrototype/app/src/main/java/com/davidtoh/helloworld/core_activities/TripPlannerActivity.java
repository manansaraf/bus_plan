package com.davidtoh.helloworld.core_activities;

import android.app.Activity;
import android.os.Bundle;

import com.davidtoh.helloworld.R;

/**
 * Created by davidtoh on 2/21/15.
 */
public class TripPlannerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_planner);

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

}
