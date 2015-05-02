package com.busplan.core_activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.busplan.database.BusStopsDAO;
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

public class TripPlannerResultActivity extends Activity {
	private ProgressBar spinner;
    /**
     * This function gets called when the activity is made, it makes sure the calling activity
     * passed it a stop to look up
     *
     * @param savedInstanceState
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_planner_result);
		Intent intent = getIntent();
		String end_name = "";
		String start_name = "";
		String timeAndDate = "";
		if (intent.hasExtra("endStopName") && intent.hasExtra("startStopName")) {
			end_name = intent.getStringExtra("endStopName");
			start_name = intent.getStringExtra("startStopName");
		}
		if (intent.hasExtra("date")) {
			String date = intent.getStringExtra("date");
			timeAndDate += "&date=" + date;
		}
		if (intent.hasExtra("time")) {
			String time = intent.getStringExtra("time");
			timeAndDate += "&time=" + time;
		}
		makeAPICalls(start_name, end_name, timeAndDate);
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
    /**
     * This function calls the CUMTD API functions to the planned trips from the API.
     *
     * @param start_name, the start stop name
     * @param end_name, the end stop name
     * @param timeAndDate, the time and date of the planned trip if given
     */
	private void makeAPICalls(String start_name, String end_name, String timeAndDate) {

		BusStopsDAO busStopsDAO = new BusStopsDAO(this);
		busStopsDAO.open();
		BusStopInfo start_busStopInfo = busStopsDAO.getStop(start_name);

		String start_stopID = start_busStopInfo.getStopID();
		BusStopInfo end_busStopInfo = busStopsDAO.getStop(end_name);

		String end_stopID = end_busStopInfo.getStopID();

		TextView textView = (TextView) findViewById(R.id.statisticsStatusView);
		textView.setVisibility(View.GONE);
		//check connection
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		String tripPlannerURL = "https://developer.cumtd.com/api/v2.2/JSON/GetPlannedTripsByStops?key="
				+ getResources().getString(R.string.apiKey) + "&origin_stop_id=" + start_stopID +
				"&destination_stop_id=" + end_stopID + timeAndDate;
		Log.v("Android", tripPlannerURL);
		spinner = (ProgressBar) findViewById(R.id.progressBar1);
		spinner.setVisibility(View.GONE);
		if (networkInfo != null && networkInfo.isConnected()) {
			showProgressBar();
			new getTrip().execute(tripPlannerURL);
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

    /**
     * This function calls the api function, retrieves the JSON file from the API,
     * sends it to the JSON parsers below and then calls the Custom List Adapters
     * to build that information on the UI thread.
     * @param tripPlannerURL, the URL to call tehe API function
     * @return String
     * @throws IOException
     */
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
					String inside = "Walk from " + trip.getStartBusStop() + " to " + trip.getEndBusStop() +
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

	private void showProgressBar() {
		spinner.setVisibility(View.VISIBLE);
	}

	private void closeProgressBar() {
		spinner.setVisibility(View.GONE);
	}
    /**
     * This function interprets the JSON string recieved from the API call and retrieves specific
     * information that will be displayed in this activity
     *
     * @param str - JSON that needs to be broken down and interpreted
     * @return - list of stops found in the str JSON
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
					JSONObject JObject_leg = JArray_inside.getJSONObject(j);
					TripInfo trip;
					if (JObject_leg.getString("type").equals("Walk")) {
						trip = getTripInfoWalk(JObject_leg);
						list.add(trip);
					} else {
						trip = getTripInfoService(JObject_leg);
						list.add(trip);
					}


				}
				trips.add(list);
			}
		} catch (Exception e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return trips;
	}
    /**
     * This function interprets the JSON string recieved from the API call and retrieves specific
     * information that will be displayed in this activity
     *
     * @param JObject_leg - JSON that needs to be broken down and interpreted
     * @return - list of stops found in the str JSON
     * @throws IOException
     */
	public TripInfo getTripInfoWalk(JSONObject JObject_leg) {
		TripInfo trip = null;
		try {
			JSONObject walk = JObject_leg.getJSONObject("walk");
			JSONObject beginStop = walk.getJSONObject("begin");
			String start_stop = beginStop.getString("name");
			String start_time = beginStop.getString("time");
			Date date = DateParser.parse(start_time);
			start_time = DateParser.toString(date);
			JSONObject endStop = walk.getJSONObject("end");
			String end_stop = endStop.getString("name");
			String end_time = endStop.getString("time");
			date = DateParser.parse(end_time);
			end_time = DateParser.toString(date);
			trip = new TripInfo(start_stop, end_stop, start_time, end_time, walk.getDouble("distance"), null);

		} catch (Exception e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return trip;
	}
    /**
     * This function interprets the JSON string recieved from the API call and retrieves specific
     * information that will be displayed in this activity
     *
     * @param JObject_leg - JSON that needs to be broken down and interpreted
     * @return - list of stops found in the str JSON
     * @throws IOException
     */
	public TripInfo getTripInfoService(JSONObject JObject_leg) {
		TripInfo trip = null;
		try {
			JSONArray service_array = JObject_leg.getJSONArray("services");
			for (int k = 0; k < service_array.length(); k++) {
				JSONObject service = service_array.getJSONObject(k);
				JSONObject route = service.getJSONObject("route");
				JSONObject beginStop = service.getJSONObject("begin");
				String start_stop = beginStop.getString("name");
				String start_time = beginStop.getString("time");
				Date date = DateParser.parse(start_time);
				start_time = DateParser.toString(date);
				JSONObject endStop = service.getJSONObject("end");
				String end_stop = endStop.getString("name");
				String end_time = endStop.getString("time");
				date = DateParser.parse(end_time);
				end_time = DateParser.toString(date);
				JSONObject trip_json = service.getJSONObject("trip");
				String direction = trip_json.getString("direction");
				char direct = direction.charAt(0);
				String bus_name = route.getString("route_short_name") + direct + " " + route.getString("route_id");
				trip = new TripInfo(start_stop, end_stop, start_time, end_time, 0, bus_name);
			}

		} catch (Exception e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return trip;
	}

}
