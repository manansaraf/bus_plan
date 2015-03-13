package com.davidtoh.helloworld.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylan on 3/13/15.
 * this class interacts with the database and passes back objects to the caller
 */
public class BusStopsDAO {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = { SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_LAT, SQLiteHelper.COLUMN_LONG };

	public BusStopsDAO(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

    public void drop() { database.delete(SQLiteHelper.TABLE_BUSSTOPS,null,null);}
	public BusStopInfo createStop(String stopName, String stopID, double latitude, double longitude) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_NAME, stopName);
		values.put(SQLiteHelper.COLUMN_ID, stopID);
		values.put(SQLiteHelper.COLUMN_LAT, latitude);
		values.put(SQLiteHelper.COLUMN_LONG, longitude);

		database.insert(SQLiteHelper.TABLE_BUSSTOPS, null,
				values);

		Cursor cursor = database.query(SQLiteHelper.TABLE_BUSSTOPS,
				allColumns, SQLiteHelper.COLUMN_ID + " = " + "\"" + stopID + "\"",
				null, null, null, null);
		cursor.moveToFirst();

		BusStopInfo newStop = cursorToStop(cursor);
		cursor.close();
		return newStop;
	}

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
		// make sure to close the cursor
		cursor.close();
		return busStops;
	}

	public BusStopInfo getStop(String stopName) {
		Cursor cursor = database.query(SQLiteHelper.TABLE_BUSSTOPS,
				allColumns, SQLiteHelper.COLUMN_NAME + " = " + "\"" + stopName + "\"",
				null, null, null,null);
        cursor.moveToFirst();

		BusStopInfo stop = cursorToStop(cursor);
		cursor.close();
		return stop;
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
