package com.busplan.core_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.busplan.R;
import com.busplan.database.BusStopsDAO;
import com.busplan.utils.BusStopInfo;
import com.busplan.widgets.BusStopWidgetConfig;

/**
 * Created by dylan on 2/17/15.
 * this activity is a helper for trip planner and displays the trip options
 */
public class SearchStopsTripPlannerActivity extends Activity implements AdapterView.OnItemClickListener,
		SearchView.OnQueryTextListener {

	private ArrayAdapter<BusStopInfo> mAdapter;
	private BusStopsDAO bstopsDAO;
	private String startStopName;
	private String endStopName;
	private int stop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_stops);
		ListView listview = (ListView) findViewById(R.id.listView);
		listview.setOnItemClickListener(this);

		bstopsDAO = new BusStopsDAO(this);
		bstopsDAO.open();

		mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
				bstopsDAO.getAllStops());
		listview.setAdapter(mAdapter);


		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (intent.hasExtra("startStopName")) {
			startStopName = intent.getStringExtra("startStopName");
		}
		if (intent.hasExtra("endStopName")) {
			endStopName = intent.getStringExtra("endStopName");
		}
		stop = intent.getIntExtra("stop", 1);
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {

		Intent intent = new Intent();
		if (getIntent().hasExtra("classname")) {
			if (getIntent().getStringExtra("classname").equals("Scheduler")) {
				intent.setClass(this, SchedulerAddReminderActivity.class);
			}
		} else if (getIntent().hasExtra("stopWidget")) {
			intent.setClass(this, BusStopWidgetConfig.class);
			intent.putExtra("WIDGET_ID", getIntent().getIntExtra("WIDGET_ID", 0));
		} else
			intent.setClass(this, TripPlannerActivity.class);

		if (stop == 1) {
			intent.putExtra("startStopName", l.getItemAtPosition(position).toString());
			intent.putExtra("endStopName", endStopName);
			startActivity(intent);
		} else {
			intent.putExtra("startStopName", startStopName);
			intent.putExtra("endStopName", l.getItemAtPosition(position).toString());
			startActivity(intent);
		}
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);

		SearchView mSearch = (SearchView) menu.findItem(R.id.action_search).getActionView();
		mSearch.setOnQueryTextListener(this);
		return true;
		//return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_search:
				//openSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		onQueryTextChange(query);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		mAdapter.clear();
		mAdapter.addAll(bstopsDAO.searchStops(newText));
		mAdapter.notifyDataSetChanged();
		return false;
	}
}
