package com.davidtoh.helloworld;

import android.test.ActivityInstrumentationTestCase2;

import com.davidtoh.helloworld.core_activities.SchedulerActivity;

/**
 * Created by dylan on 4/8/15.\
 * unit testing for SchedulerActivity
 */
public class SchedulerActivityTest extends ActivityInstrumentationTestCase2<SchedulerActivity> {
	private SchedulerActivity schedulerActivity;

	public SchedulerActivityTest() {
		super(SchedulerActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		schedulerActivity = getActivity();
	}

	public void testPreconditions() {
		assertNotNull("SchedulerActivity is null", schedulerActivity);
	}
}
