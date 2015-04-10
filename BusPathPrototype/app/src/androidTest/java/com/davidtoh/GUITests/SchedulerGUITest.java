package com.davidtoh.GUITests;

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.core_activities.SchedulerActivity;
import com.davidtoh.helloworld.database.AlarmDAO;
import com.davidtoh.helloworld.utils.AlarmInfo;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import org.hamcrest.Matchers;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.startsWith;


/**
 * Created by Elee on 2015-04-08.
 * Testing Scheudeler  GUI system automatically. As of currently the tests CANNOT all be run at
 * the same time. The testing framework does not clear the activities form the stack and it
 * prevents the tests from running sequentially
 */
public class SchedulerGUITest extends ActivityInstrumentationTestCase2<SchedulerActivity> {

	private SchedulerActivity schedulerActivity;
	private AlarmDAO alarmDAO;

	public SchedulerGUITest() {
		super(SchedulerActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		if (schedulerActivity == null)
			schedulerActivity = getActivity();
		else
			getActivity();
		alarmDAO = new AlarmDAO(schedulerActivity);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAddReminder() {
		addAlarm();
		onData(hasToString(startsWith("Illini")))
				.inAdapterView(ViewMatchers.withId(R.id.alarmListView)).atPosition(0)
				.check(ViewAssertions.matches(
						ViewMatchers.withText(Matchers.containsString("Illini Union"))));

		AlarmInfo alarm = getAlarmFromList();
		assertEquals("Illini Union", alarm.getDestination());
		alarmDAO.deleteAlarm(alarm.getID());
	}

	public void testEditReminder() {
		addAlarm();
		onData(hasToString(startsWith("Illini")))
				.inAdapterView(withId(R.id.alarmListView)).atPosition(0)
				.perform(click());
		onView(withId(R.id.wednesday)).perform(click());
		onView(withId(R.id.checkBox)).perform(click());
		onView(withId(R.id.AlarmSubmit)).perform(click());

		AlarmInfo alarm = getAlarmFromList();
		assertEquals("monday wednesday ", alarm.getDay());
		assertEquals("true", alarm.getRepeat());
		alarmDAO.deleteAlarm(alarm.getID());
	}

	public void testDeleteReminder() {
		addAlarm();
		alarmDAO.open();
		int sizeBefore = alarmDAO.getAllAlarms().size();
		onData(hasToString(startsWith("Illini")))
				.inAdapterView(withId(R.id.alarmListView)).atPosition(0)
				.perform(longClick());
		onView(withText("Yes")).perform(click());
		int sizeAfter = alarmDAO.getAllAlarms().size();
		assertEquals(sizeBefore, sizeAfter + 1);
	}

	public void testIncompleteAddReminder() {
		alarmDAO.open();
		int sizeBefore = alarmDAO.getAllAlarms().size();
		onView(withId(R.id.addRemButton)).perform(click());
		onView(withId(R.id.dest)).perform(click());
		onData(hasToString(startsWith("Activities and Recreation"))).perform(click());
		onView(withId(R.id.AlarmSubmit)).perform(click());

		int sizeAfter = alarmDAO.getAllAlarms().size();
		assertEquals(sizeBefore, sizeAfter);

		onView(withId(R.id.monday)).perform(click());
		onView(withId(R.id.AlarmSubmit)).perform(click());

		sizeAfter = alarmDAO.getAllAlarms().size();
		assertEquals(sizeBefore, sizeAfter);

		onView(withId(R.id.arrivetime)).perform(click());
		onView(withText("OK")).perform(click());
		onView(withId(R.id.AlarmSubmit)).perform(click());

		sizeAfter = alarmDAO.getAllAlarms().size();
		assertEquals(sizeBefore + 1, sizeAfter);

		AlarmInfo alarm = getAlarmFromList();
		alarmDAO.deleteAlarm(alarm.getID());
	}

	private void addAlarm() {
		onView(withId(R.id.addRemButton)).perform(click());
		onView(withId(R.id.dest)).perform(click());
		onData(hasToString(startsWith("Illini Union"))).perform(click());
		onView(withId(R.id.monday)).perform(click());
		onView(withId(R.id.arrivetime)).perform(click());
		onView(withText("OK")).perform(click());
		onView(withId(R.id.AlarmSubmit)).perform(click());
	}

	private AlarmInfo getAlarmFromList() {
		alarmDAO.open();
		int pos = ((ListView) schedulerActivity.findViewById(R.id.alarmListView)).getChildCount();
		return alarmDAO.getAlarm(pos);
	}
}
