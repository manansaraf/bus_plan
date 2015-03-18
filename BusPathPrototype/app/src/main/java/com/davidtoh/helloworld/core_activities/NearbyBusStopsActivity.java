package com.davidtoh.helloworld.core_activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.database.BusStopsDAO;
import com.davidtoh.helloworld.utils.BusStopInfo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class NearbyBusStopsActivity extends FragmentActivity {

	private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	public BusStopInfo[] markers;
    List<BusStopInfo> list;
    BusStopsDAO busStops;
	LocationManager loc;
	LocationListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby_bus_stops);
		loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
		final Location lastKnown = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnown != null) {
			LatLng latLng = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
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
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
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
					Intent intent = new Intent(NearbyBusStopsActivity.this, NearbyBusStopsActivity.class);
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
		createBusStopLocation();
	}

	private void createBusStopLocation() {
        busStops = new BusStopsDAO(this);
        busStops.open();
        list = busStops.getAllStops();
		List<BusStopInfo> marker = list;
		markers = new BusStopInfo[list.size()];
		for (int i = 0; i < marker.size(); i++) {
			mMap.addMarker(new MarkerOptions().position(new LatLng(marker.get(i).getLatitude(), marker.get(i).getLongitude())).title(marker.get(i).getStopName()));
			markers[i] = marker.get(i);
		}
		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				LatLng latLng = marker.getPosition();
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17.0f);
				mMap.moveCamera(cameraUpdate);
				marker.showInfoWindow();
				return true;
			}
		});
		mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent intent = new Intent(NearbyBusStopsActivity.this, BusStopStatisticsActivity.class);
				intent.putExtra("busStopName", marker.getTitle());
				startActivity(intent);

			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		loc.removeUpdates(listener);
	}
}
