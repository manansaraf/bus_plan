package com.davidtoh.helloworld;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.davidtoh.helloworld.database.BusStopsDAO;
import com.davidtoh.helloworld.database.FavoriteStopsDAO;
import com.davidtoh.helloworld.utils.BusStopInfo;

import java.io.File;
import java.util.List;

/**
 * Created by dylan on 3/16/15.
 * this is a test to make sure simple database functions are working
 */
public class DatabaseTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mainActivity;

	public DatabaseTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mainActivity = getActivity();
	}


	public void testPreconditions() {
		assertNotNull("mainActivity is null", mainActivity);
	}

	public void testDatabaseExists() {
		SQLiteDatabase checkDB = null;
		try {
			String dbPath = "//data/data/com.davidtoh.helloworld/databases/bus_plan.db";
			File file = new File(dbPath);
			if (file.exists() && !file.isDirectory()) {
				checkDB = SQLiteDatabase.openDatabase(dbPath, null,
						SQLiteDatabase.OPEN_READONLY);
				checkDB.close();
			}
		} catch (SQLiteException e) {
			Log.e("DB_TEST", "Database does not exist");
		}
		assertNotNull(checkDB);
	}

	/* tests for BusStopsDAO*/
	public void testGetAllStops() {
		BusStopsDAO busStopsDAO = new BusStopsDAO(mainActivity);
		busStopsDAO.open();
		List<BusStopInfo> stops = busStopsDAO.getAllStops();
		busStopsDAO.close();
		assertEquals("Activities and Recreation", stops.get(0).getStopName());
	}

	public void testGetStop() {
		BusStopsDAO busStopsDAO = new BusStopsDAO(mainActivity);
		busStopsDAO.open();
		BusStopInfo stop = busStopsDAO.getStop("Green and Mathews");
		busStopsDAO.close();
		assertEquals("Green and Mathews", stop.getStopName());
	}

	public void testSearchStops() {
		BusStopsDAO busStopsDAO = new BusStopsDAO(mainActivity);
		busStopsDAO.open();
		List<BusStopInfo> stops = busStopsDAO.searchStops("gg");
		busStopsDAO.close();
		assertEquals(3, stops.size());
	}

	/* tests for FavoriteStopsDAO */
	public void testFavorites() {
		FavoriteStopsDAO favoriteStopsDAO = new FavoriteStopsDAO(mainActivity);
		favoriteStopsDAO.open();
		List<BusStopInfo> stops = favoriteStopsDAO.getAllFavoriteStops();
		int numFavorites = stops.size();
		//add stop and see if numFavorites increased
		favoriteStopsDAO.createFavoriteStop("THIS IS A TEST STOP", "TEST STOP TEST");
		stops = favoriteStopsDAO.getAllFavoriteStops();
		assertTrue(numFavorites + 1 == stops.size());
		//removed stop and see if numFavorites decreased
		favoriteStopsDAO.deleteFavoriteStop("THIS IS A TEST STOP");
		stops = favoriteStopsDAO.getAllFavoriteStops();
		assertEquals(numFavorites, stops.size());
	}
}