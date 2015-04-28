package com.busplan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by dylan on 3/16/15.
 * This dao is responsible for interacting with the database with the Version table and keeps track
 * of the version returned from the API of the data in the Bus Stops table
 */
public class VersionDAO {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = {SQLiteHelper.COLUMN_DATE};

	public VersionDAO(Context context) {
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
	 * Used to return the date of the current version in the database
	 *
	 * @return - string containing the date
	 */
	public String getDate() {
		Cursor cursor = database.query(SQLiteHelper.TABLE_VERSION,
				allColumns, null, null, null, null, null);

		if (cursor.getCount() == 0)
			return "";
		cursor.moveToFirst();

		String date = cursor.getString(0);
		cursor.close();
		return date;
	}

	/**
	 * Used to set the date of the version
	 *
	 * @param newDate - string containing the date of the new data
	 */
	public void setDate(String newDate) {
		database.delete(SQLiteHelper.TABLE_VERSION, null, null);

		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_DATE, newDate);

		database.insert(SQLiteHelper.TABLE_VERSION, null, values);
	}
}
