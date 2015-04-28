package com.busplan.core_activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.busplan.R;
import com.busplan.database.AlarmDAO;
import com.busplan.database.BusStopsDAO;
import com.busplan.utils.AlarmInfo;
import com.busplan.utils.BusStopInfo;
import com.busplan.utils.Connection;
import com.busplan.utils.CustomListAdapterTripPlanner;
import com.busplan.utils.DateParser;
import com.busplan.utils.TripInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Elee on 2015-04-08.
 * This activity is responsible for displaying the trip information of the reminder once the user
 * clicks on the reminder notification
 */
public class SchedulerViewTripActivity extends Activity {
	private ProgressBar spinner;
	private BusStopsDAO busStopsDAO;
	LocationManager loc;

	/**
	 * This function gets called when the activity is made, it gets the extra information passed to
	 * this activity and gets the reminder alarm and uses it to make an API call
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_planner_result);
		Intent intent = getIntent();
		int position = intent.getIntExtra("id", 0);
		Log.d("POSITION", position + "");

		AlarmDAO alarmDAO = new AlarmDAO(this);
		busStopsDAO = new BusStopsDAO(this);
		alarmDAO.open();
		AlarmInfo alarmInfo = alarmDAO.getAlarmById(position);
		loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		spinner = (ProgressBar) findViewById(R.id.progressBar1);
		spinner.setVisibility(View.GONE);
		makeAPICalls(alarmInfo);
		if (alarmInfo.getDay().equals("")) {
			alarmDAO.deleteAlarm(alarmInfo.getID());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_trip_planner_result, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void makeAPICalls(AlarmInfo alarmInfo) {
		String destination = alarmInfo.getDestination();
		String arriveTime = alarmInfo.getTime();
		busStopsDAO.open();
		BusStopInfo busStopInfo = busStopsDAO.getStop(destination);
		double dest_lat = busStopInfo.getLatitude();
		double dest_lon = busStopInfo.getLongitude();
		final Location lastKnown = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double origin_lat = lastKnown.getLatitude();
		double origin_lon = lastKnown.getLongitude();
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		TextView textView = (TextView) findViewById(R.id.statisticsStatusView);
		textView.setVisibility(View.GONE);
		String SchedulerURL =
				"https://developer.cumtd.com/api/v2.2/JSON/GetPlannedTripsByLatLon?key="
						+ getResources().getString(R.string.apiKey) + "&origin_lat=" + origin_lat
						+ "&origin_lon=" + origin_lon + "&destination_lat=" + dest_lat
						+ "&destination_lon=" + dest_lon + "&time=" + arriveTime
						+ "&arrive_depart=arrive";

		if (networkInfo != null && networkInfo.isConnected()) {
			showProgressBar();
			new getTrip().execute(SchedulerURL);
		} else {
			textView.setVisibility(View.VISIBLE);
			textView.setText("No network connection available.");
		}
	}

	private class getTrip extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			try {
				return createLists(urls[0]);

			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}


		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			TextView textView = (TextView) findViewById(R.id.statisticsStatusView);
			if (!result.equals("")) {
				textView.setVisibility(View.VISIBLE);
				textView.setText(result);
			}
			closeProgressBar();
		}
	}

	private String createLists(String tripPlannerURL) throws IOException {
		Connection connect = new Connection(tripPlannerURL);
		String tripJSON = connect.getJSON();
		List<List<TripInfo>> TripList = buildTripJSON(tripJSON);

		HashMap<String, List<String>> fullTripInfoList = new HashMap<>();
		List<String> listDataHeader = new ArrayList<>();
		int i = 0;
		for (List<TripInfo> stop : TripList) {
			i++;
			List<String> busStopInfoList = new ArrayList<>();
			for (TripInfo trip : stop) {
				if (trip.getBus_name() == null) {
					String location = trip.getStartBusStop();
					if (location.contains(".")) {
						location = "your location";
					}
					String inside = "Walk from " + location + " to " + trip.getEndBusStop() +
							" for " + trip.getWalk_distance() + " miles";
					String up = trip.getStartTime();
					busStopInfoList.add(up + "%" + inside);
				} else {
					String inside = "Board " + trip.getBus_name() + " from " + trip.getStartBusStop();
					String up = trip.getStartTime();
					busStopInfoList.add(up + "%" + inside);
					inside = "Get off at " + trip.getEndBusStop();
					up = trip.getEndTime();
					busStopInfoList.add(up + "%" + inside);
				}
			}
			fullTripInfoList.put("Trip " + i, busStopInfoList);
			listDataHeader.add("Trip " + i);
		}

		final CustomListAdapterTripPlanner listAdapter = new CustomListAdapterTripPlanner(this, listDataHeader, fullTripInfoList);
		final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expandRouteView);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				expListView.setAdapter(listAdapter);
				int count = listAdapter.getGroupCount();
				for (int pos = 0; pos < count; pos++) {
					expListView.expandGroup(pos);
				}
			}
		});
		if (listAdapter.getGroupCount() == 0)
			return "No trips available at this time";
		else
			return "";
	}

	/**
	 * Similar to the trip planner JSON builder, this function parses the JSON returned from the
	 * API call in a fashion that can be displayed in a listView for the user to see
	 *
	 * @param str - string of the JSON return from the API call
	 * @return - list of the info that the listView needs to display
	 * @throws IOException
	 */
	public List<List<TripInfo>> buildTripJSON(String str) throws IOException {
		JSONObject JObject;
		List<List<TripInfo>> trips = null;
		try {
			JObject = new JSONObject(str);
			JSONArray JArray = JObject.getJSONArray("itineraries");
			trips = new ArrayList<>();
			for (int i = 0; i < JArray.length(); i++) {
				JSONObject JObject_inside = JArray.getJSONObject(i);
				JSONArray JArray_inside = JObject_inside.getJSONArray("legs");
				List<TripInfo> list = new ArrayList<>();
				for (int j = 0; j < JArray_inside.length(); j++) {
					String start_stop, end_stop, start_time, end_time;
					JSONObject JObject_leg = JArray_inside.getJSONObject(j);
					TripInfo trip;
					if (JObject_leg.getString("type").equals("Walk")) {
						JSONObject walk = JObject_leg.getJSONObject("walk");
						JSONObject beginStop = walk.getJSONObject("begin");
						start_stop = beginStop.getString("name");
						start_time = beginStop.getString("time");
						Date date = DateParser.parse(start_time);
						start_time = DateParser.toString(date);
						JSONObject endStop = walk.getJSONObject("end");
						end_stop = endStop.getString("name");
						end_time = endStop.getString("time");
						date = DateParser.parse(end_time);
						end_time = DateParser.toString(date);
						trip = new TripInfo(start_stop, end_stop, start_time, end_time, walk.getDouble("distance"), null);
						list.add(trip);
					} else {
						JSONArray service_array = JObject_leg.getJSONArray("services");
						for (int k = 0; k < service_array.length(); k++) {
							JSONObject service = service_array.getJSONObject(k);
							JSONObject route = service.getJSONObject("route");
							JSONObject beginStop = service.getJSONObject("begin");
							start_stop = beginStop.getString("name");
							start_time = beginStop.getString("time");
							Date date = DateParser.parse(start_time);
							start_time = DateParser.toString(date);
							JSONObject endStop = service.getJSONObject("end");
							end_stop = endStop.getString("name");
							end_time = endStop.getString("time");
							date = DateParser.parse(end_time);
							end_time = DateParser.toString(date);
							JSONObject trip_json = service.getJSONObject("trip");
							String direction = trip_json.getString("direction");
							char direct = direction.charAt(0);
							String bus_name = route.getString("route_short_name") + direct + " " + route.getString("route_id");
							trip = new TripInfo(start_stop, end_stop, start_time, end_time, 0, bus_name);
							list.add(trip);
						}
					}


				}
				trips.add(list);
			}
		} catch (Exception e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return trips;
	}

	private void showProgressBar() {
		spinner.setVisibility(View.VISIBLE);
	}

	private void closeProgressBar() {
		spinner.setVisibility(View.GONE);
	}
}
