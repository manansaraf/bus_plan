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
 * This dao is responsible for interacting with the database with the Favorites table
 */
public class FavoriteStopsDAO {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = {SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_ID};

	public FavoriteStopsDAO(Context context) {
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

	/**
	 * Used to add a favorite stop to the database
	 *
	 * @param stopName - name of stop to add
	 * @param stopID   - id of stop to add
	 */
	public void createFavoriteStop(String stopName, String stopID) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_NAME, stopName);
		values.put(SQLiteHelper.COLUMN_ID, stopID);

		database.insert(SQLiteHelper.TABLE_FAVORITES, null,
				values);
	}

	/**
	 * Used to return all favorite stops in the database
	 *
	 * @return - list of all stops found in the favorite table
	 */
	public List<BusStopInfo> getAllFavoriteStops() {
		List<BusStopInfo> busStops = new ArrayList<>();

		Cursor cursor = database.query(SQLiteHelper.TABLE_FAVORITES,
				allColumns, null, null, null, null, SQLiteHelper.COLUMN_NAME + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BusStopInfo stop = cursorToStop(cursor);
			busStops.add(stop);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return busStops;
	}

	/**
	 * Used to delete a stop from favorites
	 *
	 * @param stopName - name of stop to be deleted
	 */
	public void deleteFavoriteStop(String stopName) {
		database.delete(SQLiteHelper.TABLE_FAVORITES, SQLiteHelper.COLUMN_NAME +
				" = " + "\"" + stopName + "\"", null);
	}

	private BusStopInfo cursorToStop(Cursor cursor) {
		BusStopInfo busStopInfo = new BusStopInfo();
		busStopInfo.setStopName(cursor.getString(0));
		busStopInfo.setStopID(cursor.getString(1));
		busStopInfo.setLatitude(0);
		busStopInfo.setLongitude(0);
		return busStopInfo;
	}
}
