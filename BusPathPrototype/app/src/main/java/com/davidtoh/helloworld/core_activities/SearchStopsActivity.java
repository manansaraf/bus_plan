package com.davidtoh.helloworld.core_activities;

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

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.database.BusStopsDAO;
import com.davidtoh.helloworld.utils.BusStopInfo;

/**
 * Created by dylan on 2/17/15.
 * this activity lists all of the stops in the database and when clicked, goes to the
 * BusStopStatisticsActivity
 */
public class SearchStopsActivity extends Activity implements AdapterView.OnItemClickListener,
		SearchView.OnQueryTextListener {

	private ArrayAdapter<BusStopInfo> mAdapter;
	private BusStopsDAO bstopsDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_stops);
		ListView listview = (ListView) findViewById(R.id.listView);
		listview.setOnItemClickListener(this);

		bstopsDAO = new BusStopsDAO(this);
		bstopsDAO.open();

		mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bstopsDAO.getAllStops());
		listview.setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        String stopName = l.getItemAtPosition(position).toString();
		startActivity(newBusStopStatisticsIntent(stopName));
	}

    public Intent newBusStopStatisticsIntent(String stopName)
    {
        Intent intent = new Intent();
        intent.setClass(this, BusStopStatisticsActivity.class);
        intent.putExtra("busStopName", stopName);
        return intent;
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
