package com.davidtoh.helloworld;

import android.content.Intent;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.CheckBox;

import com.davidtoh.helloworld.core_activities.BusStopStatisticsActivity;
import com.davidtoh.helloworld.utils.BusRouteInfo;
import com.davidtoh.helloworld.utils.BusStopInfo;

import java.io.IOException;
import java.util.List;

/**
 * Created by dylan on 2/20/15.
 * class to test the basic functions in BusStopStatistics Activity
 */
public class BusStopStatisticsActivityTest extends ActivityInstrumentationTestCase2<BusStopStatisticsActivity> {

    private BusStopStatisticsActivity busStopStatisticsActivity;
    private CheckBox mCheckBox;
    private boolean status;

    public BusStopStatisticsActivityTest() {
        super(BusStopStatisticsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra("busStopName", "Illini Union");
        setActivityIntent(intent);
        busStopStatisticsActivity = getActivity();
        mCheckBox = (CheckBox)busStopStatisticsActivity.findViewById(R.id.favoriteCB);
        status = busStopStatisticsActivity.getFavoriteStatus();
    }


    public void testPreconditions() {
        assertNotNull("busStopStatistics is null", busStopStatisticsActivity);
    }

    public void testSetCheckBox() {
        assertEquals(mCheckBox.isChecked(), busStopStatisticsActivity.getFavoriteStatus());
        assertTrue(true);
    }

    @UiThreadTest
    public void testAddFavorite() {
        busStopStatisticsActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!status)
                    mCheckBox.performClick(); // if not favorited, add to favorites
                assertTrue(busStopStatisticsActivity.getFavoriteStatus());
                if(!status)
                    mCheckBox.performClick(); // if originally not favorite, remove from favorites
            }
        });
    }

    @UiThreadTest
    public void testRemoveFavorite() {
        busStopStatisticsActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(status)
                    mCheckBox.performClick(); // if favorited, remove from favorites
                assertFalse(busStopStatisticsActivity.getFavoriteStatus());
                if(status)
                    mCheckBox.performClick(); // if originally favorited, add to favorites
            }
        });
    }

    public void testBuildDepartJSON() {
        Resources res = getInstrumentation().getTargetContext().getResources();
        try {
            List<BusRouteInfo> stops =  busStopStatisticsActivity.buildDepartJSON(res.getString(R.string.iuBusStop));
            assertNotNull("stops is null", stops);
            assertEquals("13N Silver", stops.get(0).getBusName());
            assertEquals(0, stops.get(0).getTimeExpected());
            assertEquals("5W GreenHOPPER", stops.get(1).getBusName());
            assertEquals(3, stops.get(2).getTimeExpected());
        } catch (IOException e) {
            Log.e("TEST_ERROR", e.getMessage());
        }
    }

    public void testBuildStopJSON() {
        Resources res = getInstrumentation().getTargetContext().getResources();
        try {
            List<BusStopInfo> children =  busStopStatisticsActivity.buildStopJSON(res.getString(R.string.childTest));
            assertNotNull("children is null", children);
            assertEquals("Illini Union (South Side Shelter)", children.get(0).getStopName());
            assertEquals("IU:1", children.get(0).getStopID());
            assertEquals("Illini Union (Engineering Side)", children.get(1).getStopName());
            assertEquals("IU:2", children.get(1).getStopID());
        } catch (IOException e) {
            Log.e("TEST_ERROR", e.getMessage());
        }
    }

    //this test requires network access
    public void testMakeConnection() {
        String url = "https://developer.cumtd.com/api/v2.2/JSON/GetDeparturesByStop?key=a6030286b6ed4d609f2178e7cc5a17c9&stop_id=IU";
        try {
            String result = busStopStatisticsActivity.makeConnection(url);
            assertTrue(result.contains("{\"method\":\"GetDeparturesByStop\",\"params\":{\"stop_id\":\"IU\"}}"));
        } catch (IOException e) {
            Log.e("TEST_ERROR", e.getMessage());
        }
    }
}
