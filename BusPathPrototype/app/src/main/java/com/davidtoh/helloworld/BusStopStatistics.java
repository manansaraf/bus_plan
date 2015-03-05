package com.davidtoh.helloworld;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.davidtoh.helloworld.utils.BusRouteInfo;
import com.davidtoh.helloworld.utils.ChildStop;
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
 * idea for this gotten from http://stackoverflow.com/questions/13281197/android-how-to-create-clickable-listview
 */
public class BusStopStatistics extends Activity{
    private ProgressBar spinner;
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_stop_statistics);

		Intent intent = getIntent();
		int position = intent.getIntExtra("position", 0);

		String[] myKeys = getResources().getStringArray(R.array.stops);
		String stopID = myKeys[position];

		//not needed
		TextView textView = (TextView) findViewById(R.id.textview);
		//textView.setText(stopID);

		//check connection
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		String departureURL = "https://developer.cumtd.com/api/v2.2/JSON/GetDeparturesByStop?key="
				+ getResources().getString(R.string.apiKey) + "&stop_id=" + stopID;
		String stopURL = "https://developer.cumtd.com/api/v2.2/JSON/GetStop?key="
				+ getResources().getString(R.string.apiKey) + "&stop_id=" + stopID;

		//Log.d("departURL", departureURL);
		//Log.d("stopURL", stopURL);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
		if (networkInfo != null && networkInfo.isConnected()) {
            ShowProgressBar();
			new getDeparturesByStop().execute(departureURL, stopURL);
		} else {
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
				return downloadUrl(urls[0], urls[1]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			//displayed for testing purposes
			TextView textView = (TextView) findViewById(R.id.textview);
			textView.setText(result);
            CloseProgressBar();
		}
	}


	private String downloadUrl(String departURL, String stopURL) throws IOException {
		InputStream inputStream = null;

		try {
			URL url = new URL(departURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			inputStream = conn.getInputStream();
            String departJSON = readIt(inputStream);

			url = new URL(stopURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			inputStream = conn.getInputStream();
			String stopJSON = readIt(inputStream);

			List<BusRouteInfo> BusList = buildDepartJSON(departJSON);
			List<ChildStop> ChildList = buildStopJSON(stopJSON);

			HashMap<String, List<String>> allStopInfoList = new HashMap<>();
			List<String> listDataHeader = new ArrayList<>();

			for (ChildStop stop : ChildList) {
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

			//final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
			//		android.R.layout.simple_list_item_1, busStopInfoList);

			final ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, allStopInfoList);
			final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expandRouteView);

			//final ListView listView = (ListView) findViewById(R.id.routeView);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					expListView.setAdapter(listAdapter);
				}
			});
            //System.out.println(JSONdata);
            return "";
			// Convert the InputStream into a string
			//return (readJsonStream(inputStream).get(0)).getBusName();

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
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
            Log.d("JSON ERROR: ", e.getMessage());
        }
        return BusList;
    }

	public List<ChildStop> buildStopJSON(String str) throws IOException{
		JSONObject JObject;
		List<ChildStop> ChildList = null;
		try {
			JObject = new JSONObject(str);
			JSONArray JArray = JObject.getJSONArray("stops").getJSONObject(0).getJSONArray("stop_points");
			ChildList = new ArrayList<>();
			for (int i = 0; i < JArray.length(); i++) {
				JObject = JArray.getJSONObject(i);
				ChildStop childStop = new ChildStop(JObject.getString("stop_name"), JObject.getString("stop_id"));
				ChildList.add(childStop);
			}
		}catch (JSONException e) {
			Log.d("JSON ERROR: ", e.getMessage());
		}
		return ChildList;
	}
    public void ShowProgressBar(){
        spinner.setVisibility(View.VISIBLE);
    }
    public void CloseProgressBar(){
        spinner.setVisibility(View.GONE);
    }
    //public List<BusRouteInfo> readJson
	/* JSON parser code from basic android development site
	http://developer.android.com/reference/android/util/JsonReader.html
	 */
/*	public List<BusRouteInfo> readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readAllStops(reader);
		} finally {
			reader.close();
		}
	}

	public List<BusRouteInfo> readAllStops(JsonReader reader) throws IOException {
		List<BusRouteInfo> stops = new ArrayList<>();

		reader.beginArray();
		while (reader.hasNext()) {
			stops.add(readStopInfo(reader));
		}
		reader.endArray();
		return stops;
	}

	public BusRouteInfo readStopInfo(JsonReader reader) throws IOException {
		String busName = null;
		int timeExpected = 0;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			switch (name) {
				case "headsign":
					busName = reader.nextString();
					break;
				case "expected_mins":
					timeExpected = reader.nextInt();
					break;
				default:
					reader.skipValue();
					break;
			}
		}
		reader.endObject();
		return new BusRouteInfo(busName, timeExpected);
	}*/
}