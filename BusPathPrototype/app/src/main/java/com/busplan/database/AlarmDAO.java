package com.busplan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.busplan.utils.AlarmInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylan on 4/3/15.
 * This dao is responsible for interacting with the database with the Alarms table
 */
public class AlarmDAO {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = {SQLiteHelper.COLUMN_ALARM_ID, SQLiteHelper.COLUMN_DESTINATION,
			SQLiteHelper.COLUMN_TIME, SQLiteHelper.COLUMN_REMIND_DAY, SQLiteHelper.COLUMN_REPEAT};

	/**
	 * creates a DAO to work with
	 *
	 * @param context
	 */
	public AlarmDAO(Context context) {
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
	 * Used to add an alarm to the database
	 *
	 * @param destination - destination stop name
	 * @param time        - time in HH:mm format that user wants to arrive
	 * @param day         - string of days that the alarm should happen on
	 * @param repeat      - string value representing if alarm should happen repeatedly
	 */
	public void createAlarm(String destination, String time, String day, String repeat) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_DESTINATION, destination);
		values.put(SQLiteHelper.COLUMN_TIME, time);
		values.put(SQLiteHelper.COLUMN_REMIND_DAY, day);
		values.put(SQLiteHelper.COLUMN_REPEAT, repeat);

		database.insert(SQLiteHelper.TABLE_ALARM, null,
				values);
	}

	/**
	 * Used to return all alarms currently in the user's database
	 *
	 * @return - list of alarms in the database
	 */
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

	/**
	 * Used to get the alarm corresponding to a specific position in the listView on the main
	 * scheduler activity
	 *
	 * @param pos - position of alarm in listView
	 * @return - alarm corresponding to given position
	 */
	public AlarmInfo getAlarm(int pos) {
		Log.d("pos = ", Integer.toString(pos));
		Cursor cursor = database.query(SQLiteHelper.TABLE_ALARM,
				allColumns, null, null, null, null, SQLiteHelper.COLUMN_ALARM_ID + " ASC", (pos) + ", 1");
		cursor.moveToFirst();
		AlarmInfo alarm = cursorToAlarm(cursor);
		cursor.close();
		return alarm;
	}

	/**
	 * Used to return the very last alarm added to the database
	 *
	 * @return - information of the last alarm
	 */
	public AlarmInfo getLastAlarm() {
		Cursor cursor = database.query(SQLiteHelper.TABLE_ALARM,
				allColumns, null, null, null, null, SQLiteHelper.COLUMN_ALARM_ID + " DESC", "1");
		cursor.moveToFirst();
		AlarmInfo alarm = cursorToAlarm(cursor);
		cursor.close();
		return alarm;
	}

	/**
	 * Used to return a specific alarm provided the id of it is known
	 *
	 * @param id - the id of the alarm
	 * @return - information about the alarm
	 */
	public AlarmInfo getAlarmById(int id) {

		Cursor cursor = database.query(SQLiteHelper.TABLE_ALARM,
				allColumns, SQLiteHelper.COLUMN_ALARM_ID + " = '" + id + "'", null, null, null, SQLiteHelper.COLUMN_ALARM_ID + " ASC");
		cursor.moveToFirst();
		AlarmInfo alarm = cursorToAlarm(cursor);
		cursor.close();
		return alarm;
	}

	/**
	 * Used to get all alarms that have the specified day in their day string
	 *
	 * @param day - the day that each returned alarm should have
	 * @return - list of alarms that have day in their days attribute
	 */
	public List<AlarmInfo> getAlarmsByDay(String day) {
		List<AlarmInfo> alarms = new ArrayList<>();

		Cursor cursor = database.query(SQLiteHelper.TABLE_ALARM,
				allColumns, SQLiteHelper.COLUMN_REMIND_DAY + " Like '%" + day + "%'", null, null, null, SQLiteHelper.COLUMN_ALARM_ID + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AlarmInfo alarmInfo = cursorToAlarm(cursor);
			alarms.add(alarmInfo);
			cursor.moveToNext();
		}
		cursor.close();
		return alarms;
	}

	/**
	 * Used to edit an alarm already existing in the database
	 *
	 * @param id          - current id of alarm
	 * @param destination - new or old destination
	 * @param time        - new or old time
	 * @param day         - new or old days
	 * @param repeat      - new or old repeat value
	 */
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

	/**
	 * Used to delete a specific alarm from the database
	 *
	 * @param id - the id of the alarm to be deleted
	 */
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
