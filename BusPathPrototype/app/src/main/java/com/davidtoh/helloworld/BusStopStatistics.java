package com.davidtoh.helloworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

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
import java.util.List;

/**
 * Created by dylan on 2/19/15.
 * idea for this gotten from http://stackoverflow.com/questions/13281197/android-how-to-create-clickable-listview
 */
public class BusStopStatistics extends Activity{
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
		textView.setText(stopID);

		//check connection
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		String urlString = "https://developer.cumtd.com/api/v2.2/JSON/GetDeparturesByStop?key="
				+ getResources().getString(R.string.apiKey) + "&stop_id=" + stopID;
		Log.d("URL", urlString);

		if (networkInfo != null && networkInfo.isConnected()) {
			new getDeparturesByStop().execute(urlString);
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
				return downloadUrl(urls[0]);
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
		}
	}

	private String downloadUrl(String myurl) throws IOException {
		InputStream inputStream = null;

		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			inputStream = conn.getInputStream();
            String JSONString = readIt(inputStream);
            List<BusStopInfo> BusList = BuildJSON(JSONString);
            String TestString = "";
            for(int i = 0; i < BusList.size();i++){
                TestString += BusList.get(i).getBusName() + " : " + BusList.get(i).getTimeExpected() + " ";
            }
            //System.out.println(JSONdata);
            return TestString;
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

    public List<BusStopInfo> BuildJSON(String str) throws IOException{
        JSONObject JObject = null;
        List<BusStopInfo> BusList = null;
        try {
            JObject = new JSONObject(str);
            JSONArray JArray = JObject.getJSONArray("departures");
            BusList = new ArrayList<BusStopInfo>();
            for (int i = 0; i < JArray.length(); i++) {
                JObject = JArray.getJSONObject(i);
                BusStopInfo stopInfo = new BusStopInfo(JObject.getString("headsign"),Integer.parseInt(JObject.getString("expected_mins")));
                BusList.add(stopInfo);
            }
        }catch (JSONException e) {
            Log.d("JSON ERROR: ", e.getMessage());
        }
        return BusList;
    }
    //public List<BusStopInfo> readJson
	/* JSON parser code from basic android development site
	http://developer.android.com/reference/android/util/JsonReader.html
	 */
	public List<BusStopInfo> readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readAllStops(reader);
		} finally {
			reader.close();
		}
	}

	public List<BusStopInfo> readAllStops(JsonReader reader) throws IOException {
		List<BusStopInfo> stops = new ArrayList<>();

		reader.beginArray();
		while (reader.hasNext()) {
			stops.add(readStopInfo(reader));
		}
		reader.endArray();
		return stops;
	}

	public BusStopInfo readStopInfo(JsonReader reader) throws IOException {
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
		return new BusStopInfo(busName, timeExpected);
	}
}