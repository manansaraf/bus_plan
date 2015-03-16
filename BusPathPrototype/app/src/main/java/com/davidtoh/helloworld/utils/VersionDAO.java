package com.davidtoh.helloworld.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by dylan on 3/16/15.
 */
public class VersionDAO {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = { SQLiteHelper.COLUMN_DATE};

	public VersionDAO(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public String getDate() {
		Cursor cursor = database.query(SQLiteHelper.TABLE_VERSION,
				allColumns, null, null, null, null,null);
		if(cursor == null) {
			return "";
		}
		cursor.moveToFirst();

		String date = cursor.getString(0);
		cursor.close();
		return date;
	}

	public void setDate(String newDate) {
		database.delete(SQLiteHelper.TABLE_VERSION, null, null);

		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_DATE, newDate);

		database.insert(SQLiteHelper.TABLE_BUSSTOPS, null,values);
	}
}
