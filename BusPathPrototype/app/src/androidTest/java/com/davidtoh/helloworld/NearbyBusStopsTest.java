package com.davidtoh.helloworld;

import android.test.ActivityInstrumentationTestCase2;

import com.davidtoh.helloworld.utils.locations_marker;

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
        locations_marker[] test = nearbyBusStops.getMarkers();
        assertEquals(loc[3].getLatitude(),test[3].getLatitude());
		assertNotSame(loc[3].getStopName(),test[1].getStopName());
    }

    public void testLocations(){
        locations_marker loc = new locations_marker(0.1,6.1,"Dummy Place");
        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();
        String stop = loc.getStopName();
        assertEquals(longitude,6.1);
        assertEquals(latitude,0.1);
        assertTrue("Dummy Place".equals(stop));
    }
}