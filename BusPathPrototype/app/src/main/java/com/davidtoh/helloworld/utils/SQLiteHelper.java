package com.davidtoh.helloworld.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dylan on 3/13/15.\
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_BUSSTOPS = "bus_stops";
	public static final String COLUMN_NAME = "stop_name";
	public static final String COLUMN_ID = "stop_id";
	public static final String COLUMN_LAT = "latitude";
	public static final String COLUMN_LONG = "longitude";

	public static final String TABLE_FAVORITES = "favorite_stops";

	private static final String DATABASE_NAME = "bus_plan.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_BUSSTOPS + "(" + COLUMN_NAME + " text not null, " + COLUMN_ID +
			" text not null primary key, " + COLUMN_LAT + " real, " + COLUMN_LONG + " real);" +
			" create table " + TABLE_FAVORITES + "(" + COLUMN_NAME + " text not null, " +
			COLUMN_ID + " text not null primary key);";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUSSTOPS + ", " + TABLE_FAVORITES + ";");
		onCreate(db);
	}
}