package com.busplan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.busplan.utils.BusStopInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylan on 3/13/15.
 * This dao is responsible for interacting with the database with the Bus Stops table
 */
public class BusStopsDAO {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = {SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_LAT, SQLiteHelper.COLUMN_LONG};

	public BusStopsDAO(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	/**
	 * opens the database, MUST be called before any other function of the DAO is used
	 *
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Used to drop the current table, useful when current information is out of date and needs to
	 * be updated from the API
	 */
	public void drop() {
		database.delete(SQLiteHelper.TABLE_BUSSTOPS, null, null);
	}

	/**
	 * Used to add a bus stop to the database
	 *
	 * @param stopName  - name of the bus stop
	 * @param stopID    - id of the bus stop
	 * @param latitude  - latitude of the bus stop
	 * @param longitude - longitude of the bus stop
	 */
	public void createStop(String stopName, String stopID, double latitude, double longitude) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_NAME, stopName);
		values.put(SQLiteHelper.COLUMN_ID, stopID);
		values.put(SQLiteHelper.COLUMN_LAT, latitude);
		values.put(SQLiteHelper.COLUMN_LONG, longitude);

		database.insert(SQLiteHelper.TABLE_BUSSTOPS, null,
				values);
	}

	/**
	 * Used to get all stops currently in the database
	 *
	 * @return - list of all stops in the database
	 */
	public List<BusStopInfo> getAllStops() {
		List<BusStopInfo> busStops = new ArrayList<>();

		Cursor cursor = database.query(SQLiteHelper.TABLE_BUSSTOPS,
				allColumns, null, null, null, null, SQLiteHelper.COLUMN_NAME + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BusStopInfo stop = cursorToStop(cursor);
			busStops.add(stop);
			cursor.moveToNext();
		}
		cursor.close();
		return busStops;
	}

	/**
	 * Used to get a specific stop by name
	 *
	 * @param stopName - name of the stop tp be returned
	 * @return - information about the stop
	 */
	public BusStopInfo getStop(String stopName) {
		Cursor cursor = database.query(SQLiteHelper.TABLE_BUSSTOPS,
				allColumns, SQLiteHelper.COLUMN_NAME + " = " + "\"" + stopName + "\"",
				null, null, null, null);
		cursor.moveToFirst();

		BusStopInfo stop = cursorToStop(cursor);
		cursor.close();
		return stop;
	}

	/**
	 * Used to return list of stops based on a substring search
	 *
	 * @param stopSubString - substring that user wants to search by
	 * @return - list of all stops that need to be displayed
	 */
	public List<BusStopInfo> searchStops(String stopSubString) {
		List<BusStopInfo> busStops = new ArrayList<>();

		Cursor cursor = database.query(SQLiteHelper.TABLE_BUSSTOPS,
				allColumns, SQLiteHelper.COLUMN_NAME + " LIKE " + "\'%" + stopSubString + "%\'",
				null, null, null, SQLiteHelper.COLUMN_NAME + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BusStopInfo stop = cursorToStop(cursor);
			busStops.add(stop);
			cursor.moveToNext();
		}
		cursor.close();
		return busStops;
	}

	private BusStopInfo cursorToStop(Cursor cursor) {
		BusStopInfo busStopInfo = new BusStopInfo();
		busStopInfo.setStopName(cursor.getString(0));
		busStopInfo.setStopID(cursor.getString(1));
		busStopInfo.setLatitude(cursor.getDouble(2));
		busStopInfo.setLongitude(cursor.getDouble(3));
		return busStopInfo;
	}
}
