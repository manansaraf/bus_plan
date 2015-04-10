package com.davidtoh.helloworld;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.davidtoh.helloworld.core_activities.TripPlannerResultActivity;
import com.davidtoh.helloworld.utils.TripInfo;
import com.davidtoh.helloworld.utils.connection;

import java.io.IOException;
import java.util.List;

/**
 * Created by dylan on 2/20/15.
 * class to test the basic functions in BusStopStatistics Activity
 */
public class TripPlannerResultActivityTest extends ActivityInstrumentationTestCase2<TripPlannerResultActivity> {

	private TripPlannerResultActivity tripPlannerResultActivity;

	public TripPlannerResultActivityTest() {
		super(TripPlannerResultActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent();
		intent.putExtra("startStopName", "Illini Union");
		intent.putExtra("endStopName", "Wright and Springfield");
		setActivityIntent(intent);
		tripPlannerResultActivity = getActivity();
	}


	public void testPreconditions() {
		assertNotNull("tripPlannerResult is null", tripPlannerResultActivity);
	}

	public void testBuildTripJSONTrue() {
		try {
			List<List<TripInfo>> stops = tripPlannerResultActivity.buildTripJSON("{\"time\":\"2015-04-09T13:03:40-05:00\",\"new_changeset\":true,\"status\":{\"code\":200,\"msg\":\"ok\"},\"rqst\":{\"method\":\"GetPlannedTripsByStops\",\"params\":{\"destination_stop_id\":\"IT\",\"origin_stop_id\":\"IU\"}},\"itineraries\":[{\"start_time\":\"2015-04-09T13:08:00-05:00\",\"end_time\":\"2015-04-09T13:15:00-05:00\",\"travel_time\":7,\"legs\":[{\"services\":[{\"begin\":{\"lat\":40.110446,\"lon\":-88.227402,\"name\":\"Illini Union (Engineering Side)\",\"stop_id\":\"IU:2\",\"time\":\"2015-04-09T13:08:00-05:00\"},\"end\":{\"lat\":40.115362,\"lon\":-88.241443,\"name\":\"Illinois Terminal (Platform C)\",\"stop_id\":\"IT:5\",\"time\":\"2015-04-09T13:15:00-05:00\"},\"route\":{\"route_color\":\"006991\",\"route_id\":\"TEAL\",\"route_long_name\":\"Teal\",\"route_short_name\":\"12\",\"route_text_color\":\"ffffff\"},\"trip\":{\"trip_id\":\"[@7.0.41893871@][4][1243540851671]\\/18__T2UIMF\",\"trip_headsign\":\"WEST - ILLINOIS TERMINAL\",\"route_id\":\"TEAL\",\"block_id\":\"T2UIMF\",\"direction\":\"West\",\"service_id\":\"T2UIMF\",\"shape_id\":\"12W TEAL 12\"}}],\"type\":\"Service\"}]},{\"start_time\":\"2015-04-09T13:28:00-05:00\",\"end_time\":\"2015-04-09T13:35:00-05:00\",\"travel_time\":7,\"legs\":[{\"services\":[{\"begin\":{\"lat\":40.110446,\"lon\":-88.227402,\"name\":\"Illini Union (Engineering Side)\",\"stop_id\":\"IU:2\",\"time\":\"2015-04-09T13:28:00-05:00\"},\"end\":{\"lat\":40.115362,\"lon\":-88.241443,\"name\":\"Illinois Terminal (Platform C)\",\"stop_id\":\"IT:5\",\"time\":\"2015-04-09T13:35:00-05:00\"},\"route\":{\"route_color\":\"006991\",\"route_id\":\"TEAL\",\"route_long_name\":\"Teal\",\"route_short_name\":\"12\",\"route_text_color\":\"ffffff\"},\"trip\":{\"trip_id\":\"[@7.0.41893871@][4][1243540851671]\\/19__T1UIMF\",\"trip_headsign\":\"WEST - ILLINOIS TERMINAL\",\"route_id\":\"TEAL\",\"block_id\":\"T1UIMF\",\"direction\":\"West\",\"service_id\":\"T1UIMF\",\"shape_id\":\"12W TEAL 12\"}}],\"type\":\"Service\"}]},{\"start_time\":\"2015-04-09T13:38:00-05:00\",\"end_time\":\"2015-04-09T13:45:00-05:00\",\"travel_time\":7,\"legs\":[{\"services\":[{\"begin\":{\"lat\":40.110446,\"lon\":-88.227402,\"name\":\"Illini Union (Engineering Side)\",\"stop_id\":\"IU:2\",\"time\":\"2015-04-09T13:38:00-05:00\"},\"end\":{\"lat\":40.115362,\"lon\":-88.241443,\"name\":\"Illinois Terminal (Platform C)\",\"stop_id\":\"IT:5\",\"time\":\"2015-04-09T13:45:00-05:00\"},\"route\":{\"route_color\":\"006991\",\"route_id\":\"TEAL\",\"route_long_name\":\"Teal\",\"route_short_name\":\"12\",\"route_text_color\":\"ffffff\"},\"trip\":{\"trip_id\":\"[@14.0.51708725@][4][1275506123875]\\/13__T3UIMF\",\"trip_headsign\":\"WEST - ILLINOIS TERMINAL\",\"route_id\":\"TEAL\",\"block_id\":\"T3UIMF\",\"direction\":\"West\",\"service_id\":\"T3UIMF\",\"shape_id\":\"TEAL 23\"}}],\"type\":\"Service\"}]}]})");
			assertNotNull("stops is null", stops);
			assertEquals(3, stops.size());
			assertEquals("Illini Union (Engineering Side)", stops.get(0).get(0).getStartBusStop());
			Log.v("Android", stops.get(0).get(0).getStartTime());
			assertEquals("01:08 PM", stops.get(0).get(0).getStartTime());
			assertEquals("12W TEAL", stops.get(0).get(0).getBus_name());
		} catch (IOException e) {
			Log.e("TEST_ERROR", e.getMessage());
		}
	}

	public void testBuildTripJSONFalse() {
		try {
			List<List<TripInfo>> stops = tripPlannerResultActivity.buildTripJSON("{\"time\":\"2015-04-09T13:42:02-05:00\",\"new_changeset\":true,\"status\":{\"code\":400,\"msg\":\"Destination stop_id not found\"},\"rqst\":{\"params\":{}}})");
			assertNull("stops is null", stops);

		} catch (IOException e) {
			Log.e("TEST_ERROR", e.getMessage());
		}
	}

	//this test requires network access
	public void testMakeConnection() {
		String url = "https://developer.cumtd.com/api/v2.2/JSON/GetPlannedTripsByStops?key=a6030286b6ed4d609f2178e7cc5a17c9&origin_stop_id=IU&destination_stop_id=IT";
		connection connect = new connection(url);
		String result = connect.getJSON();
		assertTrue(result.contains("{\"method\":\"GetPlannedTripsByStops\",\"params\":{\"destination_stop_id\":\"IT\",\"origin_stop_id\":\"IU\"}}"));

	}
}
