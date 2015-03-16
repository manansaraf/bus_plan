package com.davidtoh.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.davidtoh.helloworld.core_activities.BusStopStatisticsActivity;
import com.davidtoh.helloworld.core_activities.NearbyBusStopsActivity;
import com.davidtoh.helloworld.core_activities.SchedulerActivity;
import com.davidtoh.helloworld.core_activities.SearchStopsActivity;
import com.davidtoh.helloworld.core_activities.TripPlannerActivity;
import com.davidtoh.helloworld.database.BusStopDatabase;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		ListView listview = (ListView) findViewById(R.id.listView);
		listview.setOnItemClickListener(this);
        BusStopDatabase busStopDatabase = new BusStopDatabase();
        busStopDatabase.populate(this);
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

    public void toNearbyStops(View view) {
        Intent changeToNearby = new Intent(view.getContext(), NearbyBusStopsActivity.class);
        startActivityForResult(changeToNearby, 0);
    }

	public void toSearchStops(View view) {
		Intent changeToSearch = new Intent(view.getContext(), SearchStopsActivity.class);
		startActivityForResult(changeToSearch, 0);
	}

    public void toScheduler(View view) {
        Intent changeToScheduler = new Intent(view.getContext(), SchedulerActivity.class);
        startActivityForResult(changeToScheduler, 0);
    }

    public void toTripPlanner(View view) {
        Intent changeToTripPlanner = new Intent(view.getContext(), TripPlannerActivity.class);
        startActivityForResult(changeToTripPlanner, 0);
    }

    /*public void toAlarm(View view) {
        Intent changeToFavorites = new Intent(view.getContext(), Alarm.class);
        startActivityForResult(changeToFavorites, 0);
    }*/
}
