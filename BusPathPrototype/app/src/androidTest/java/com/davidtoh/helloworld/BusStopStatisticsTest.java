package com.davidtoh.helloworld;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by dylan on 2/20/15.
 * class to test the basic functions in BusStopStatistics Activity
 */
public class BusStopStatisticsTest extends ActivityInstrumentationTestCase2<BusStopStatistics> {

	private BusStopStatistics busStopStatistics;

	public BusStopStatisticsTest() {
		super(BusStopStatistics.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		busStopStatistics = getActivity();
	}


	public void testPreconditions() {
		assertNotNull("busStopStatistics is null", busStopStatistics);
	}
}
