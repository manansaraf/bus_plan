package com.davidtoh.helloworld;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.davidtoh.helloworld.utils.locations_marker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearbyBusStopsActivity extends FragmentActivity {

	private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	public locations_marker[] markers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby_bus_stops);
		setUpMapIfNeeded();

	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
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
		final LocationManager loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final Location lastKnown = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnown != null) {
			Log.v("Android", "Hi");
			LatLng latLng = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
			mMap.moveCamera(cameraUpdate);
		} else {
			LatLng latLng = new LatLng(40.1094, -88.2272);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10.0f);
			mMap.moveCamera(cameraUpdate);
		}
		mMap.setMyLocationEnabled(true);
		LocationListener listener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				//mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("Marker1"));
				Log.v("android", "enter1");
				LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
				mMap.moveCamera(cameraUpdate);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				if ((status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) & provider.equals("gps")) {
					LocationManager loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					Location lastKnown = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					LatLng latLng = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
					mMap.moveCamera(cameraUpdate);
				} else if (provider.equals("gps") && status == LocationProvider.AVAILABLE) {

					loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
				}
			}

			@Override
			public void onProviderEnabled(String provider) {
				if (provider.equals("gps")) {
					Log.v("android", "enter2");
					Intent intent = new Intent(NearbyBusStopsActivity.this, NearbyBusStopsActivity.class);
					startActivity(intent);

				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				if (provider.equals("gps")) {
					Log.v("android", "enter3");
					LatLng latLng = new LatLng(40.1094, -88.2272);
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13.0f);
					mMap.moveCamera(cameraUpdate);
				}
			}
		};
		loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, listener);
		createBusStopLocation();
	}

	private void createBusStopLocation() {
		locations_marker[] marker = getLocationOfMarkers();
		markers = new locations_marker[marker.length];
		for (int i = 0; i < marker.length; i++) {
			mMap.addMarker(new MarkerOptions().position(new LatLng(marker[i].getLatitude(), marker[i].getLongitude())).title(marker[i].getStopName()));
			markers[i] = marker[i];
		}
		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				LatLng latLng = marker.getPosition();
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18.0f);
				mMap.moveCamera(cameraUpdate);
				marker.showInfoWindow();
				return true;
			}
		});
		mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent intent = new Intent(NearbyBusStopsActivity.this, BusStopStatistics.class);
				//TODO Send a stop id from the database
				intent.putExtra("busStopID", 1);
				startActivity(intent);

			}
		});
	}

	public locations_marker[] getLocationOfMarkers() {
		locations_marker[] marker = new locations_marker[5];
		marker[0] = new locations_marker(40.114455, -88.229298, "White and Wright");
		marker[1] = new locations_marker(40.114452, -88.230285, "White and Sixth");
		marker[2] = new locations_marker(40.113428, -88.228877, "Wright and Stoughton");
		marker[3] = new locations_marker(40.112852, -88.229020, "Wright and Springfield");
		marker[4] = new locations_marker(40.108590, -88.228847, "Transit Plaza");
		return marker;
	}

	public locations_marker[] getMarkers() {
		return markers;
	}

}