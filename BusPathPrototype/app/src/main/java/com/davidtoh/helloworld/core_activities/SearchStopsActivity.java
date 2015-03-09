package com.davidtoh.helloworld.core_activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.davidtoh.helloworld.R;

/**
 * Created by dylan on 2/17/15.
 */
public class SearchStopsActivity extends Activity implements AdapterView.OnItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_stops);

		ListView listview = (ListView) findViewById(R.id.listView);
		listview.setOnItemClickListener(this);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
	}

    private void doMySearch(String query) {

    }

	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(this, BusStopStatisticsActivity.class);
		intent.putExtra("position", position);

		intent.putExtra("id", id);
		startActivity(intent);
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);
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
}
