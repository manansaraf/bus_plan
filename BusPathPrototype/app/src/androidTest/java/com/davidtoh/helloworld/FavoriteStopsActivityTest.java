package com.davidtoh.helloworld;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.davidtoh.helloworld.core_activities.FavoriteStopsActivity;

/**
 * Created by dylan on 2/20/15.
 *
 */
public class FavoriteStopsActivityTest extends ActivityInstrumentationTestCase2<FavoriteStopsActivity> {

	private FavoriteStopsActivity favoriteStopsActivity;
	private TextView textView;
	private ListView listView;

	public FavoriteStopsActivityTest() {
		super(FavoriteStopsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		favoriteStopsActivity = getActivity();
		textView = (TextView) favoriteStopsActivity.findViewById(R.id.textView);
		listView = (ListView) favoriteStopsActivity.findViewById(R.id.listView);
	}


	public void testPreconditions() {
		assertNotNull("favoriteStops is null", favoriteStopsActivity);
		assertNotNull("textView is null", textView);
		assertNotNull("listView is null", listView);
		assertEquals("Favorite Bus Stops", textView.getText());
	}
}
