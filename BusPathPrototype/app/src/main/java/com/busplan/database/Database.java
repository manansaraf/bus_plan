package com.busplan.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.busplan.MainActivity;
import com.busplan.R;
import com.busplan.utils.BusStopInfo;

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
 * This class is responsible for managing the database. It checks the version and populates the bus
 * stop table if needed.
 */
public class Database {
	private Context context;
	private MainActivity main;

	/**
	 * Called from the main activity and handles the function calls to check if database needs to be
	 * populated or updated
	 *
	 * @param context
	 */
	public void populate(Context context) {
		this.context = context;
		this.main = (MainActivity) context;
		String URL = "https://developer.cumtd.com/api/v2.2/JSON/GetLastFeedUpdate?key="
				+ context.getResources().getString(R.string.apiKey);

		//check connection
		ConnectivityManager connMgr = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new getDate().execute(URL);
		}
	}

	private class getDate extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			try {
				checkUpdateDate(urls[0]);
				return "";
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {

		}

	}

	/**
	 * Checks the version table and sees if the current returned data version from the API is different
	 * than the data version in the database. If so it calls a function to re fill the table.
	 *
	 * @param url
	 * @throws IOException
	 */
	public void checkUpdateDate(String url) throws IOException {
		VersionDAO versionDAO = new VersionDAO(this.context);
		versionDAO.open();
		String JSONString = makeConnection(url);
		String newFeedDate = buildLastFeedJSON(JSONString);
		String lastUpdatedDate = versionDAO.getDate();
		if (!newFeedDate.equals(lastUpdatedDate)) {
			versionDAO.setDate(newFeedDate);
			String URL = "https://developer.cumtd.com/api/v2.2/JSON/GetStops?key="
					+ context.getResources().getString(R.string.apiKey);
			main.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					main.showProgressBar();
				}
			});
			new getStops().execute(URL);
		} else {
			main.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					main.closeProgressBar();
				}
			});
		}
	}

	private class getStops extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			try {
				CreateBusStop(urls[0]);
				return "";
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			main.closeProgressBar();
		}
	}

	/**
	 * Called to add stops to the database from the JSON returned from the API
	 *
	 * @param url - JSON from the API
	 * @throws IOException
	 */
	public void CreateBusStop(String url) throws IOException {
		String JSONstring = makeConnection(url);
		List<BusStopInfo> busStopList = buildBusStopJSON(JSONstring);
		BusStopsDAO busStopsDAO = new BusStopsDAO(this.context);
		busStopsDAO.open();
		busStopsDAO.drop();
		for (BusStopInfo busStop : busStopList) {
			busStopsDAO.createStop(busStop.getStopName(), busStop.getStopID(), busStop.getLatitude(), busStop.getLongitude());
		}
	}

	private String makeConnection(String urlString) throws IOException {
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

	private String readIt(InputStream stream) throws IOException {
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

	private List<BusStopInfo> buildBusStopJSON(String str) throws IOException {
		JSONObject JObject;
		List<BusStopInfo> BusList = null;
		try {
			JObject = new JSONObject(str);
			JSONArray JArray = JObject.getJSONArray("stops");
			BusList = new ArrayList<>();
			for (int i = 0; i < JArray.length(); i++) {
				JObject = JArray.getJSONObject(i);
				String Stop_id = JObject.getString("stop_id");
				String Stop_name = JObject.getString("stop_name");
				JSONArray JArray_child = JObject.getJSONArray("stop_points");
				JObject = JArray_child.getJSONObject(0);
				String Stop_lat = JObject.getString("stop_lat");
				String Stop_lon = JObject.getString("stop_lon");

				BusStopInfo stopInfo = new BusStopInfo(Stop_name, Stop_id, Double.parseDouble(Stop_lat), Double.parseDouble(Stop_lon));
				BusList.add(stopInfo);
			}
		} catch (JSONException e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return BusList;
	}

	private String buildLastFeedJSON(String str) throws IOException {
		JSONObject JObject;
		String lastFeedDate = "";
		try {
			JObject = new JSONObject(str);
			lastFeedDate = JObject.getString("last_updated");

		} catch (JSONException e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return lastFeedDate;
	}
}
