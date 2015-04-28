package com.busplan.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Helper class to make a connection to the given URL. This is used to make all of the API calls in
 * the activities
 */
public class Connection {
	String connectionURL;
	String JSON;

	public Connection(String connectionURL) {
		this.connectionURL = connectionURL;
		try {
			JSON = makeConnection(connectionURL);
		} catch (IOException e) {
			Log.e("ConnecitonError: ", e.getMessage());
		}
	}

	public String getJSON() {
		return JSON;
	}

	/**
	 * Used to connect to the given URL
	 *
	 * @param urlString - the url to connect to
	 * @return - JSON string result of the API call
	 * @throws IOException
	 */
	public String makeConnection(String urlString) throws IOException {
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

	/**
	 * Converts the result of the connection to JSON format
	 *
	 * @param stream - the result of the API call
	 * @return - JSON string
	 * @throws IOException
	 */
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
}
