package com.busplan.helloworld;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.SearchView;
import com.busplan.R;


import com.busplan.core_activities.SearchStopsActivity;

import android.test.UiThreadTest;


/**
 * Created by Steve on 3/17/2015.
 * test of the functionality of the SearchStopsActivity
 */
public class SearchStopsActivityTest extends ActivityInstrumentationTestCase2<SearchStopsActivity> {

	private SearchStopsActivity ssActivity;
	private SearchView mSearchView;
	private ListView mListView;

	public SearchStopsActivityTest() {
		super(SearchStopsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ssActivity = getActivity();
		mSearchView = (SearchView) ssActivity.findViewById(R.id.action_search);
		mListView = (ListView) ssActivity.findViewById(R.id.listView);
	}


	public void testPreconditions() {
		assertNotNull("SearchStopsActivity is null", ssActivity);
		assertNotNull("listView is null", mListView);
	}

	@UiThreadTest
	public void testSearchAll() {
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
	public void testSearchSpecific() {
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
	public void testSearchOne() {
		ssActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mSearchView.setQuery("First and Daniel", false);
				assertTrue(mListView.getItemAtPosition(0).toString().equals("First and Daniel"));
			}
		});
	}
}

