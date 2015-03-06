package com.davidtoh.helloworld;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by dylan on 2/20/15.
 *
 */
public class FavoriteStopsTest extends ActivityInstrumentationTestCase2<FavoriteStops> {

	private FavoriteStops favoriteStops;
	private TextView textView;
	private ListView listView;

	public FavoriteStopsTest() {
		super(FavoriteStops.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		favoriteStops = getActivity();
		textView = (TextView) favoriteStops.findViewById(R.id.textView);
		listView = (ListView) favoriteStops.findViewById(R.id.listView);
	}


	public void testPreconditions() {
		assertNotNull("favoriteStops is null", favoriteStops);
		assertNotNull("textView is null", textView);
		assertNotNull("listView is null", listView);
		assertEquals("Favorite Bus Stops", textView.getText());
	}
}
