package com.davidtoh.helloworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

			// Convert the InputStream into a string
			return readIt(inputStream);

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

		StringBuilder JSONresult = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			JSONresult.append(line);
		}
		reader.close();
		return JSONresult.toString();
	}

}

