package com.davidtoh.helloworld;

import android.test.ActivityInstrumentationTestCase2;
<<<<<<< HEAD
import android.widget.ListView;
import android.widget.SearchView;
import com.davidtoh.helloworld.core_activities.SearchStopsActivity;
=======
import android.test.UiThreadTest;
import android.widget.ListView;
import android.widget.SearchView;
import com.davidtoh.helloworld.core_activities.SearchStopsActivity;
import android.util.Log;

import java.util.List;
>>>>>>> remotes/origin/groupthree

/**
 * Created by Steve on 3/17/2015.
 */
public class SearchStopsActivityTest extends ActivityInstrumentationTestCase2<SearchStopsActivity> {

    private SearchStopsActivity ssActivity;
<<<<<<< HEAD
    private SearchView searchView;
    private ListView listView;
=======
    private SearchView mSearchView;
    private ListView mListView;
>>>>>>> remotes/origin/groupthree

    public SearchStopsActivityTest() { super(SearchStopsActivity.class); }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ssActivity = getActivity();
<<<<<<< HEAD
        searchView = (SearchView) ssActivity.findViewById(R.id.action_search);
        listView = (ListView) ssActivity.findViewById(R.id.listView);
=======
        mSearchView = (SearchView) ssActivity.findViewById(R.id.action_search);
        mListView = (ListView) ssActivity.findViewById(R.id.listView);
>>>>>>> remotes/origin/groupthree
    }


    public void testPreconditions() {
        assertNotNull("SearchStopsActivity is null", ssActivity);
<<<<<<< HEAD
        assertNotNull("listView is null", listView);
=======
        assertNotNull("listView is null", mListView);
    }

    @UiThreadTest
    public void testSearchAll () {
        ssActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSearchView.setQuery("", false);
                //Log.d("**OUTPUT**: SEARCH ALL", mListView.getItemAtPosition(0).toString());
                assertTrue(mListView.getItemAtPosition(0).toString().equals("Activities and Recreation"));
            }
        });
    }

    @UiThreadTest
    public void testSearchSpecific () {
        ssActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSearchView.setQuery("first and", false);
                assertTrue(mListView.getItemAtPosition(0).toString().equals("First and Armory"));
                assertFalse(mListView.getItemAtPosition(0).toString().equals("Activities and Recreation"));
            }
        });
    }

    @UiThreadTest
    public void testSearchOne () {
        ssActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSearchView.setQuery("First and Daniel", false);
                assertTrue(mListView.getItemAtPosition(0).toString().equals("First and Daniel"));
            }
        });
>>>>>>> remotes/origin/groupthree
    }

}

