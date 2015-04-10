package com.davidtoh.helloworld.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.davidtoh.helloworld.utils.AlarmInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylan on 4/3/15.
 * this dao is responsible for interacting with the database with Alarms
 */
public class AlarmDAO {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = {SQLiteHelper.COLUMN_ALARM_ID, SQLiteHelper.COLUMN_DESTINATION,
			SQLiteHelper.COLUMN_TIME, SQLiteHelper.COLUMN_REMIND_DAY, SQLiteHelper.COLUMN_REPEAT};

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

	public void createAlarm(String destination, String time, String day, String repeat) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_DESTINATION, destination);
		values.put(SQLiteHelper.COLUMN_TIME, time);
		values.put(SQLiteHelper.COLUMN_REMIND_DAY, day);
		values.put(SQLiteHelper.COLUMN_REPEAT, repeat);

		database.insert(SQLiteHelper.TABLE_ALARM, null,
				values);
	}

	public List<AlarmInfo> getAllAlarms() {
		List<AlarmInfo> alarms = new ArrayList<>();

		Cursor cursor = database.query(SQLiteHelper.TABLE_ALARM,
				allColumns, null, null, null, null, SQLiteHelper.COLUMN_ALARM_ID + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AlarmInfo alarmInfo = cursorToAlarm(cursor);
			alarms.add(alarmInfo);
			cursor.moveToNext();
		}
		cursor.close();
		return alarms;
	}

	public AlarmInfo getAlarm(int pos) {
		Log.d("pos = ", Integer.toString(pos));
		Cursor cursor = database.query(SQLiteHelper.TABLE_ALARM,
				allColumns, null, null, null, null, SQLiteHelper.COLUMN_ALARM_ID + " ASC", (pos) + ", 1");
		cursor.moveToFirst();
		AlarmInfo alarm = cursorToAlarm(cursor);
		cursor.close();
		return alarm;
	}

	public void editAlarm(int id, String destination, String time, String day,
						  String repeat) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_DESTINATION, destination);
		values.put(SQLiteHelper.COLUMN_TIME, time);
		values.put(SQLiteHelper.COLUMN_REMIND_DAY, day);
		values.put(SQLiteHelper.COLUMN_REPEAT, repeat);

		database.update(SQLiteHelper.TABLE_ALARM, values, SQLiteHelper.COLUMN_ALARM_ID + " = " +
				id, null);
	}

	public void deleteAlarm(int id) {
		database.delete(SQLiteHelper.TABLE_ALARM, SQLiteHelper.COLUMN_ALARM_ID +
				" = " + "\"" + id + "\"", null);
	}

	private AlarmInfo cursorToAlarm(Cursor cursor) {
		AlarmInfo alarmInfo = new AlarmInfo();
		alarmInfo.setId(cursor.getInt(0));
		alarmInfo.setDestination(cursor.getString(1));
		alarmInfo.setTime(cursor.getString(2));
		alarmInfo.setDay(cursor.getString(3));
		alarmInfo.setRepeat(cursor.getString(4));
		return alarmInfo;
	}
}
