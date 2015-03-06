package com.davidtoh.helloworld;

import android.test.ActivityInstrumentationTestCase2;

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

    public void testMarkers() {
        locations_marker[] loc = nearbyBusStops.getLocationOfMarkers();
        locations_marker[] test =nearbyBusStops.getMarkers();
        assertEquals(loc,test);
    }

    public void testLocations(){
        locations_marker loc = new locations_marker(0.1,6.1,"Dummy Place");
        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();
        String stop = loc.getStopNames();
        assertEquals(longitude,0.1);
        assertEquals(latitude,6.1);
        assertTrue("Dummy Place".equals(stop));
    }
}