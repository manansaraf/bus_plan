package com.busplan.helloworld;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.busplan.MainActivity;
import com.busplan.database.AlarmDAO;
import com.busplan.database.BusStopsDAO;
import com.busplan.database.FavoriteStopsDAO;
import com.busplan.utils.AlarmInfo;
import com.busplan.utils.BusStopInfo;

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

	/* tests for AlarmDAO */
	public void testAlarms() {
		AlarmDAO alarmDAO = new AlarmDAO(mainActivity);
		alarmDAO.open();
		List<AlarmInfo> alarms = alarmDAO.getAllAlarms();
		int numAlarms = alarms.size();
		//add custom alarm
		alarmDAO.createAlarm("Test", "00:00", "monday ", "false");
		alarms = alarmDAO.getAllAlarms();
		assertEquals(numAlarms + 1, alarms.size());
		//check that alarm was added
		assertEquals("Test", alarms.get(alarms.size() - 1).getDestination());
		//delete alarm
		alarmDAO.deleteAlarm(alarms.get(alarms.size() - 1).getID());
		alarms = alarmDAO.getAllAlarms();
		assertEquals(numAlarms, alarms.size());
		if (alarms.size() > 0)
			assertNotSame("Test", alarms.get(alarms.size() - 1).getDestination());
	}
}