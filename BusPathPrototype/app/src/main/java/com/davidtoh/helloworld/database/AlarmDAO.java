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
 * Created by dylan on 4/3/15.
 * this dao is responsible for interacting with the database with Alarms
 */
public class AlarmDAO {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = {SQLiteHelper.COLUMN_DESTINATION, SQLiteHelper.COLUMN_TIME,
			SQLiteHelper.COLUMN_DEPART_DATE, SQLiteHelper.COLUMN_RECURRING};

	public AlarmDAO(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void drop() {
		database.delete(SQLiteHelper.TABLE_ALARM, null, null);
	}
/*

	public BusStopInfo createAlarm(String destination, String time, String date, String recurirng) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_NAME, destination);
		values.put(SQLiteHelper.COLUMN_ID, time);
		values.put(SQLiteHelper.COLUMN_LAT, date);
		values.put(SQLiteHelper.COLUMN_LONG, recurirng);

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

	public BusStopInfo getStop(String stopName) {
		Cursor cursor = database.query(SQLiteHelper.TABLE_BUSSTOPS,
				allColumns, SQLiteHelper.COLUMN_NAME + " = " + "\"" + stopName + "\"",
				null, null, null, null);
		cursor.moveToFirst();

		BusStopInfo stop = cursorToStop(cursor);
		cursor.close();
		return stop;
	}

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
