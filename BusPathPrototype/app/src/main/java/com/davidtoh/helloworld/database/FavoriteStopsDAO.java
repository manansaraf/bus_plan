package com.davidtoh.helloworld.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.davidtoh.helloworld.utils.BusStopInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylan on 3/13/15.
 * this class interacts with the database and passes back objects to the caller
 */
public class FavoriteStopsDAO {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = { SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_ID};

	public FavoriteStopsDAO(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public BusStopInfo createFavoriteStop(String stopName, String stopID) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_NAME, stopName);
		values.put(SQLiteHelper.COLUMN_ID, stopID);

		database.insert(SQLiteHelper.TABLE_FAVORITES, null,
				values);

		Cursor cursor = database.query(SQLiteHelper.TABLE_FAVORITES,
				allColumns, SQLiteHelper.COLUMN_ID + " = " + "\"" + stopID + "\"",
				null, null, null, null);
		cursor.moveToFirst();

		BusStopInfo newStop = cursorToStop(cursor);
		cursor.close();
		return newStop;
	}

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
