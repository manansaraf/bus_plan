package com.busplan.helloworld;

import android.test.ActivityInstrumentationTestCase2;

import com.busplan.core_activities.NearbyBusStopsActivity;
import com.busplan.utils.BusStopInfo;


/**
 * Created by manansaraf on 03/01/15.
 * class to test the basic functions in BusStopStatistics Activity
 */
public class NearbyBusStopsTest extends ActivityInstrumentationTestCase2<NearbyBusStopsActivity> {

	private NearbyBusStopsActivity nearbyBusStops;

	public NearbyBusStopsTest() {
		super(NearbyBusStopsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		nearbyBusStops = getActivity();
	}


	public void testPreconditions() {
		assertNotNull(nearbyBusStops);
	}

	public void testLocations() {
		BusStopInfo loc = new BusStopInfo("Dummy Place", "", 0.1, 6.1);
		double longitude = loc.getLongitude();
		double latitude = loc.getLatitude();
		String stop = loc.getStopName();
		assertEquals(longitude, 6.1);
		assertEquals(latitude, 0.1);
		assertTrue("Dummy Place".equals(stop));
	}
}