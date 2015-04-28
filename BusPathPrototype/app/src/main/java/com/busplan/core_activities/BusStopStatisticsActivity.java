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
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.busplan.R;
import com.busplan.database.FavoriteStopsDAO;
import com.busplan.utils.BusRouteInfo;
import com.busplan.utils.BusStopInfo;
import com.busplan.database.BusStopsDAO;
import com.busplan.utils.Connection;
import com.busplan.utils.CustomListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dylan on 2/19/15.
 * This activity displays all the incoming routes for the stop selected and passed to it from another
 * activity. From here a stop can be favorited and the user can click a route to see the shape.
 */
public class BusStopStatisticsActivity extends Activity {

	private ProgressBar spinner;
	private FavoriteStopsDAO fstopsDAO;
	private CheckBox mCheckBox;
	private String name;
	private String stopID;
	private BusStopInfo busStopInfo;
	private HashMap<String, BusRouteInfo> hash;

	/**
	 * This function gets called when the activity is made, it makes sure the calling activity
	 * passed it a stop to look up
	 *
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_stop_statistics);
		Intent intent = getIntent();
		fstopsDAO = new FavoriteStopsDAO(this);
		if (intent.hasExtra("busStopName")) {
			name = intent.getStringExtra("busStopName");
			hash = new HashMap<>();
			makeAPICalls(name);
		}
	}

	private void makeAPICalls(String stopName) {
		BusStopsDAO busStopsDAO = new BusStopsDAO(this);
		busStopsDAO.open();
		busStopInfo = busStopsDAO.getStop(stopName);
		stopID = busStopInfo.getStopID();

		TextView textView = (TextView) findViewById(R.id.statisticsStatusView);
		textView.setVisibility(View.GONE);
		//check connection
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		String departureURL = "https://developer.cumtd.com/api/v2.2/JSON/GetDeparturesByStop?key="
				+ getResources().getString(R.string.apiKey) + "&stop_id=" + stopID;
		String stopURL = "https://developer.cumtd.com/api/v2.2/JSON/GetStop?key="
				+ getResources().getString(R.string.apiKey) + "&stop_id=" + stopID;

		spinner = (ProgressBar) findViewById(R.id.progressBar1);
		spinner.setVisibility(View.GONE);
		if (networkInfo != null && networkInfo.isConnected()) {
			showProgressBar();
			new getDeparturesByStop().execute(departureURL, stopURL);
		} else {
			textView.setVisibility(View.VISIBLE);
			textView.setText("No network connection available.");
		}
	}

	/* most of the networking code from basic android developers website
	http://developer.android.com/training/basics/network-ops/connecting.html
	 */
	private class getDeparturesByStop extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			try {
				createLists(urls[0], urls[1]);
				return "";
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

	private void createLists(String departURL, String stopURL) throws IOException {
		Connection connect = new Connection(departURL);
		String departJSON = connect.getJSON();
		connect = new Connection(stopURL);
		String stopJSON = connect.getJSON();

		List<BusRouteInfo> BusList = buildDepartJSON(departJSON);
		List<BusStopInfo> ChildList = buildStopJSON(stopJSON);

		HashMap<String, List<String>> allStopInfoList = new HashMap<>();
		List<String> listDataHeader = new ArrayList<>();

		for (BusStopInfo stop : ChildList) {
			List<String> busStopInfoList = new ArrayList<>();
			for (BusRouteInfo route : BusList) {
				if (route.getStopID().equals(stop.getStopID())) {
					String value = route.getBusName() + ":"
							+ route.getTimeExpected();
					if (route.isIstop()) {
						value += "Y";
					} else
						value += "N";
					hash.put(value, route);
					busStopInfoList.add(value);
				}
			}
			allStopInfoList.put(stop.getStopName(), busStopInfoList);
			listDataHeader.add(stop.getStopName());
		}

		final CustomListAdapter listAdapter = new CustomListAdapter(this, listDataHeader, allStopInfoList);
		final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expandRouteView);
		expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {
				String childText = (String) listAdapter.getChild(groupPosition, childPosition);
				BusRouteInfo route = hash.get(childText);
				// This is just testing to see what i come up with
				if (route.getShape_id() != null) {
					Intent intent = new Intent(BusStopStatisticsActivity.this, DrawRouteActivity.class);
					intent.putExtra("vehicle_id", route.getVehicleID());
					intent.putExtra("shape_id", route.getShape_id());
					intent.putExtra("route_color", route.getRouteColor());
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), "No information available",
							Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
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
	}

	/**
	 * This function interprets the JSON string recieved from the API call and retrieves specific
	 * information that will be displayed in this activity
	 *
	 * @param str - JSON that needs to be broken down and interpreted
	 * @return - list of stops found in the str JSON
	 * @throws IOException
	 */
	public List<BusRouteInfo> buildDepartJSON(String str) throws IOException {
		JSONObject JObject;
		List<BusRouteInfo> BusList = null;
		try {
			JObject = new JSONObject(str);
			JSONArray JArray = JObject.getJSONArray("departures");
			BusList = new ArrayList<>();
			for (int i = 0; i < JArray.length(); i++) {
				JObject = JArray.getJSONObject(i);
				JSONObject color = JObject.getJSONObject("route");
				JSONObject shape = null;
				if (JObject.has("trip"))
					shape = JObject.getJSONObject("trip");
				boolean istop = JObject.getBoolean("is_istop");
				String Shape = null;
				if (shape != null) {
					Shape = shape.getString("shape_id");

					Shape = Shape.replaceAll(" ", "%20");
				}
				Log.v("Android", JObject.getString("vehicle_id"));
				BusRouteInfo routeInfo = new BusRouteInfo(JObject.getString("headsign"),
						Integer.parseInt(JObject.getString("expected_mins")), JObject.getString("stop_id")
						, Shape, JObject.getInt("vehicle_id"), "#" + color.getString("route_color"), istop);

				BusList.add(routeInfo);
			}
		} catch (JSONException e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return BusList;
	}

	public List<BusStopInfo> buildStopJSON(String str) throws IOException {
		JSONObject JObject;
		List<BusStopInfo> ChildList = null;
		try {
			JObject = new JSONObject(str);
			JSONArray JArray = JObject.getJSONArray("stops").getJSONObject(0).getJSONArray("stop_points");
			ChildList = new ArrayList<>();
			for (int i = 0; i < JArray.length(); i++) {
				JObject = JArray.getJSONObject(i);
				BusStopInfo childStop = new BusStopInfo(JObject.getString("stop_name"), JObject.getString("stop_id"), 0, 0);
				ChildList.add(childStop);
			}
		} catch (JSONException e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return ChildList;
	}

	/**
	 * shows the status spinner while the API call is happening
	 */
	public void showProgressBar() {
		spinner.setVisibility(View.VISIBLE);
	}

	/**
	 * closes the spinner after the API call is done or failed to work
	 */
	public void closeProgressBar() {
		spinner.setVisibility(View.GONE);
	}

	/**
	 * used to set the onClick function for the favorite button in the action bar menu
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_stats, menu);

		mCheckBox = (CheckBox) menu.findItem(R.id.favorite).getActionView().findViewById(R.id.favoriteCB);
		mCheckBox.setChecked(getFavoriteStatus());

		mCheckBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				fstopsDAO.open();
				if (mCheckBox.isChecked()) {
					fstopsDAO.createFavoriteStop(name, stopID);
					Toast.makeText(getApplicationContext(), "Favorite added", Toast.LENGTH_LONG).show();
				} else {
					fstopsDAO.deleteFavoriteStop(name);
					Toast.makeText(getApplicationContext(), "Favorite removed", Toast.LENGTH_LONG).show();
				}
			}
		});
		return true;
	}

	/**
	 * checks to see if the current stop that this activity is displaying is a favorite of the user
	 *
	 * @return
	 */
	public boolean getFavoriteStatus() {
		fstopsDAO.open();
		List<BusStopInfo> allFavorites = fstopsDAO.getAllFavoriteStops();
		boolean isFavorite = false;
		for (int i = 0; i < allFavorites.size(); i++) {
			if (allFavorites.get(i).getStopName().equals(busStopInfo.getStopName()))
				isFavorite = true;
		}
		return isFavorite;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_refresh:
				finish();
				startActivity(getIntent());
				return true;
			case R.id.action_help:
				Toast.makeText(getApplicationContext(), "Click a stop to view the route shape",
						Toast.LENGTH_LONG).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
		startActivity(getIntent());
	}
}