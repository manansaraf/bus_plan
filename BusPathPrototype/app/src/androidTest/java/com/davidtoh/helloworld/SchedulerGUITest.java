package com.davidtoh.helloworld;

import com.davidtoh.helloworld.core_activities.SchedulerActivity;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;

import org.hamcrest.Matchers;
import org.junit.Before;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.startsWith;


/**
 * Created by Elee on 2015-04-08.
 * Testing Scheudeler  GUI system automatically.
 */
public class SchedulerGUITest extends ActivityInstrumentationTestCase2<SchedulerActivity> {

    //private SchedulerActivity schedulerActivity;

    public SchedulerGUITest() {
        super(SchedulerActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        getActivity();

    }

    public void testScheduler(){
        onView(withId(R.id.addRemButton)).perform(click());
        onView(withId(R.id.dest)).perform(click());
        onData(hasToString(startsWith("Illini Union"))).perform(click());
        onView(withId(R.id.monday)).perform(click());
        onView(withId(R.id.arrivetime)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.AlarmSubmit)).perform(click());
        onData(hasToString(startsWith("Illini")))
                .inAdapterView(withId(R.id.alarmListView)).atPosition(0)
                .check(ViewAssertions.matches(
                        ViewMatchers.withText(Matchers.containsString("Illini Union"))));



    }


}
