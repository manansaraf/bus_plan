package com.davidtoh.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by dylan on 2/19/15.
 * idea for this gotten from http://stackoverflow.com/questions/13281197/android-how-to-create-clickable-listview
 */
public class BusStopStatistics extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_stop_statistics);

		Intent intent = getIntent();
		int position = intent.getIntExtra("position", 0);

		String[] myKeys = getResources().getStringArray(R.array.stops);

		TextView textView = (TextView) findViewById(R.id.textview);
		textView.setText(myKeys[position]);
	}

}

