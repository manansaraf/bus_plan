package com.davidtoh.helloworld.core_activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.davidtoh.helloworld.R;

import com.davidtoh.helloworld.utils.Loc;
import com.davidtoh.helloworld.utils.connection;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrawRouteActivity extends FragmentActivity {

	private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	LocationManager loc;
	LocationListener listener;
	String shape_id;
	int vehicle_id;
	String route_color;
	List<Loc> points;
	Loc Loc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw_route);
		loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setUpMapIfNeeded();
		Intent intent = getIntent();
		vehicle_id = intent.getIntExtra("vehicle_id", 1);
		shape_id = intent.getStringExtra("shape_id");
		route_color = intent.getStringExtra("route_color");
		points = null;
		makeAPICalls(vehicle_id, shape_id);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_draw_route, menu);

		return true;
	}

	private void makeAPICalls(int vehicleID, String shapeID) {

		//check connection
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		String vehicle = "" + vehicleID;
		if (vehicle.length() < 4) {
			vehicle = "0" + vehicle;
		}
		String vehicleURL = "https://developer.cumtd.com/api/v2.2/JSON/GetVehicle?key="
				+ getResources().getString(R.string.apiKey) + "&vehicle_id=" + vehicle;
		String shapeURL = "https://developer.cumtd.com/api/v2.2/JSON/GetShape?key="
				+ getResources().getString(R.string.apiKey) + "&shape_id=" + shapeID;

		if (networkInfo != null && networkInfo.isConnected()) {
			new drawRoute().execute(vehicleURL, shapeURL);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	 * installed) and the map has not already been instantiated.. This will ensure that we only ever
	 * call {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p/>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
	 * install/update the Google Play services APK on their device.
	 * <p/>
	 * A user can return to this FragmentActivity after following the prompt and correctly
	 * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
	 * have been completely destroyed during this process (it is likely that it would only be
	 * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the camera. In this case, we
	 * just add a marker near Africa.
	 * <p/>
	 * This should only be called once and when we are sure that {@link #mMap} is not null.
	 */
	private void setUpMap() {
		final Location lastKnown = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnown != null) {
			LatLng latLng = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14.0f);
			mMap.moveCamera(cameraUpdate);
		} else {
			LatLng latLng = new LatLng(40.1094, -88.2272);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13.0f);
			mMap.moveCamera(cameraUpdate);
		}
		mMap.setMyLocationEnabled(true);
		listener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14.0f);
				mMap.moveCamera(cameraUpdate);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				if ((status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) & provider.equals("gps")) {
					LocationManager loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					Location lastKnown = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					LatLng latLng = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
					mMap.moveCamera(cameraUpdate);
				} else if (provider.equals("gps") && status == LocationProvider.AVAILABLE) {

					loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
				}
			}

			@Override
			public void onProviderEnabled(String provider) {
				if (provider.equals("gps")) {
					Intent intent = new Intent(DrawRouteActivity.this, NearbyBusStopsActivity.class);
					startActivity(intent);

				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				if (provider.equals("gps")) {
					LatLng latLng = new LatLng(40.1094, -88.2272);
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13.0f);
					mMap.moveCamera(cameraUpdate);
				}
			}
		};
		loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);

		//createBusStopLocation();
	}

	public void drawRouteS(List<Loc> points) {
		PolylineOptions line = new PolylineOptions();
		int i;
		for (i = 0; i < points.size(); i++) {
			line.add(new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude()));
		}
		int color = Color.parseColor(route_color);
		line.color(color);
		mMap.addPolyline(line);
	}

	public void drawBusLocation(Loc cur_bus_location) {
		mMap.addMarker(new MarkerOptions().position(new LatLng(cur_bus_location.getLatitude(), cur_bus_location.getLongitude())).title("BUS"));
		LatLng latLng = new LatLng(cur_bus_location.getLatitude(), cur_bus_location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.5f);
		mMap.moveCamera(cameraUpdate);

	}

	@Override
	protected void onStop() {
		super.onStop();
		loc.removeUpdates(listener);
	}

	/* most of the networking code from basic android developers website
	http://developer.android.com/training/basics/network-ops/connecting.html
	 */
	private class drawRoute extends AsyncTask<String, Void, String> {
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

		}
	}

	private void createLists(String vehicleURL, String shapeURL) throws IOException {
		connection connect = new connection(shapeURL);
		String shapeJSON = connect.getJSON();
		connect = new connection(vehicleURL);
		String vehicleJSON = connect.getJSON();
		points = buildShapeJSON(shapeJSON);
		Loc = buildVehicleJSON(vehicleJSON);


		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				drawRouteS(points);
				drawBusLocation(Loc);
			}
		});
	}

	public List<Loc> buildShapeJSON(String str) throws IOException {
		JSONObject JObject;
		List<Loc> BusList = null;
		try {
			JObject = new JSONObject(str);
			JSONArray JArray = JObject.getJSONArray("shapes");
			BusList = new ArrayList<>();
			for (int i = 0; i < JArray.length(); i++) {
				JObject = JArray.getJSONObject(i);
				Loc point = new Loc(JObject.getDouble("shape_pt_lat"), JObject.getDouble("shape_pt_lon"));
				BusList.add(point);
			}
		} catch (JSONException e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return BusList;
	}

	public Loc buildVehicleJSON(String str) throws IOException {
		JSONObject JObject;
		Loc point = null;
		try {
			JObject = new JSONObject(str);
			JObject = JObject.getJSONArray("vehicles").getJSONObject(0);
			JObject = JObject.getJSONObject("location");
			point = new Loc(JObject.getDouble("lat"), JObject.getDouble("lon"));
		} catch (JSONException e) {
			Log.e("JSON ERROR: ", e.getMessage());
		}
		return point;
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

	public GoogleMap getMap() {
		return mMap;
	}
}
