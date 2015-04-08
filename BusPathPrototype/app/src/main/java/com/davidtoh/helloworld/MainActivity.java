package com.davidtoh.helloworld;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.davidtoh.helloworld.core_activities.BusStopStatisticsActivity;
import com.davidtoh.helloworld.core_activities.NearbyBusStopsActivity;
import com.davidtoh.helloworld.core_activities.SchedulerActivity;
import com.davidtoh.helloworld.core_activities.SearchStopsActivity;
import com.davidtoh.helloworld.core_activities.TripPlannerActivity;
import com.davidtoh.helloworld.database.BusStopDatabase;
import com.davidtoh.helloworld.database.FavoriteStopsDAO;
import com.davidtoh.helloworld.utils.BusStopInfo;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

	private ArrayAdapter<BusStopInfo> mAdapter;
	private FavoriteStopsDAO fstopsDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView listview = (ListView) findViewById(R.id.listView);
		listview.setOnItemClickListener(this);
		BusStopDatabase busStopDatabase = new BusStopDatabase();
		busStopDatabase.populate(this);

		fstopsDAO = new FavoriteStopsDAO(this);
		fstopsDAO.open();
		mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
				fstopsDAO.getAllFavoriteStops());
		listview.setAdapter(mAdapter);


		Intent intent = new Intent(this, SearchStopsActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n  = new Notification.Builder(this)
				.setContentTitle("New mail from " + "test@gmail.com")
				.setContentText("Subject")
				.setSmallIcon(R.drawable.ic_action_search)
				.setContentIntent(pIntent)
				.setAutoCancel(true)
				.build();


		NotificationManager notificationManager =
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.clear();
		mAdapter.addAll(fstopsDAO.getAllFavoriteStops());
		mAdapter.notifyDataSetChanged();
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
		intent.putExtra("busStopName", l.getItemAtPosition(position).toString());

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
}
