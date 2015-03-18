package com.davidtoh.helloworld;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.SearchView;
import com.davidtoh.helloworld.core_activities.SearchStopsActivity;

/**
 * Created by Steve on 3/17/2015.
 */
public class SearchStopsActivityTest extends ActivityInstrumentationTestCase2<SearchStopsActivity> {

    private SearchStopsActivity ssActivity;
    private SearchView searchView;
    private ListView listView;

    public SearchStopsActivityTest() { super(SearchStopsActivity.class); }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ssActivity = getActivity();
        searchView = (SearchView) ssActivity.findViewById(R.id.action_search);
        listView = (ListView) ssActivity.findViewById(R.id.listView);
    }


    public void testPreconditions() {
        assertNotNull("SearchStopsActivity is null", ssActivity);
        assertNotNull("listView is null", listView);
    }

}

