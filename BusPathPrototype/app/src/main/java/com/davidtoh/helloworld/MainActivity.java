package com.davidtoh.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void toNearbyStops(View view) {
        Intent changeToNearby = new Intent(view.getContext(), NearbyStops.class);
        startActivityForResult(changeToNearby, 0);
    }

	public void toSearchStops(View view) {
		Intent changeToSearch = new Intent(view.getContext(), SearchStops.class);
		startActivityForResult(changeToSearch, 0);
	}

    public void toScheduler(View view) {
        Intent changeToScheduler = new Intent(view.getContext(), Scheduler.class);
        startActivityForResult(changeToScheduler, 0);
    }

    public void toTripPlanner(View view) {
        Intent changeToTripPlanner = new Intent(view.getContext(), TripPlanner.class);
        startActivityForResult(changeToTripPlanner, 0);
    }

    public void toAlarm(View view) {
        Intent changeToFavorites = new Intent(view.getContext(), Alarm.class);
        startActivityForResult(changeToFavorites, 0);
    }
}
