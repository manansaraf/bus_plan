package com.davidtoh.helloworld.core_activities;

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

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.utils.BusRouteInfo;
import com.davidtoh.helloworld.utils.BusStopInfo;
import com.davidtoh.helloworld.database.BusStopsDAO;
import com.davidtoh.helloworld.utils.ExpandableListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dylan on 2/19/15.
 * activity to display routes coming to each child stop of stop selected
 */
public class BusStopStatisticsActivity extends Activity{
    private ProgressBar spinner;
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_stop_statistics);
		Intent intent = getIntent();
		if(intent.hasExtra("busStopName")) {
			String name = intent.getStringExtra("busStopName");
			makeAPICalls(name);
		}
	}

	private void makeAPICalls(String name) {

		BusStopsDAO busStopsDAO = new BusStopsDAO(this);
		busStopsDAO.open();
		BusStopInfo busStopInfo = busStopsDAO.getStop(name);

		String stopID = busStopInfo.getStopID();

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

		spinner = (ProgressBar)findViewById(R.id.progressBar1);
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
			if(!result.equals("")) {
				textView.setVisibility(View.VISIBLE);
				textView.setText(result);
			}
            closeProgressBar();
		}
	}

	public String makeConnection(String urlString) throws IOException{
		InputStream inputStream = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			inputStream = conn.getInputStream();
			return readIt(inputStream);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	private void createLists(String departURL, String stopURL) throws IOException {
		String departJSON = makeConnection(departURL);
		String stopJSON = makeConnection(stopURL);

		List<BusRouteInfo> BusList = buildDepartJSON(departJSON);
		List<BusStopInfo> ChildList = buildStopJSON(stopJSON);

		HashMap<String, List<String>> allStopInfoList = new HashMap<>();
		List<String> listDataHeader = new ArrayList<>();

		for (BusStopInfo stop : ChildList) {
			List<String> busStopInfoList = new ArrayList<>();
			for(BusRouteInfo route : BusList){
				if(route.getStopID().equals(stop.getStopID())) {
					busStopInfoList.add(route.getBusName() + ":"
							+ route.getTimeExpected());
				}
			}
			allStopInfoList.put(stop.getStopName(), busStopInfoList);
			listDataHeader.add(stop.getStopName());
		}

		final ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, allStopInfoList);
		final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expandRouteView);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				expListView.setAdapter(listAdapter);
				int count = listAdapter.getGroupCount();
				for(int pos = 0; pos < count; pos++) {
					expListView.expandGroup(pos);
				}
			}
		});
	}

	public String readIt(InputStream stream) throws IOException {
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

		StringBuilder JSONResult = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
            JSONResult.append(line);
		}
		reader.close();
		return JSONResult.toString();
	}

    public List<BusRouteInfo> buildDepartJSON(String str) throws IOException{
        JSONObject JObject;
        List<BusRouteInfo> BusList = null;
        try {
            JObject = new JSONObject(str);
            JSONArray JArray = JObject.getJSONArray("departures");
            BusList = new ArrayList<>();
            for (int i = 0; i < JArray.length(); i++) {
                JObject = JArray.getJSONObject(i);
                BusRouteInfo routeInfo = new BusRouteInfo(JObject.getString("headsign"),Integer.parseInt(JObject.getString("expected_mins")), JObject.getString("stop_id"));
                BusList.add(routeInfo);
            }
        }catch (JSONException e) {
            Log.e("JSON ERROR: ", e.getMessage());
        }
        return BusList;
    }

	public List<BusStopInfo> buildStopJSON(String str) throws IOException{
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
		}catch (JSONException e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return ChildList;
	}
    public void showProgressBar(){
        spinner.setVisibility(View.VISIBLE);
    }
    public void closeProgressBar(){
        spinner.setVisibility(View.GONE);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_stats, menu);
		return true;
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