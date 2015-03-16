package com.davidtoh.helloworld.core_activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.utils.BusStopsDAO;

/**
 * Created by dylan on 2/17/15.
 */
public class SearchStopsActivity extends Activity implements AdapterView.OnItemClickListener,
        SearchView.OnQueryTextListener {

    private ArrayAdapter mAdapter;
    private SearchView mSearch;
    private BusStopsDAO bstopsDAO;
    private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_stops);
		listview = (ListView) findViewById(R.id.listView);
		listview.setOnItemClickListener(this);

        bstopsDAO = new BusStopsDAO(this);
        bstopsDAO.open();

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bstopsDAO.getAllStops());
        listview.setAdapter(mAdapter);



        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
	}

    private void doMySearch(String query) {

    }

    @Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(this, BusStopStatisticsActivity.class);
		intent.putExtra("busStopName", l.getItemAtPosition(position).toString());

		intent.putExtra("id", id);
		startActivity(intent);
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);

        mSearch = (SearchView)menu.findItem(R.id.action_search).getActionView();
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
        //mAdapter.addAll(bstopsDAO.searchStops(newText));
        Log.d("", "TEST VALUE1");
        mAdapter.notifyDataSetChanged();
        return false;
    }
}
