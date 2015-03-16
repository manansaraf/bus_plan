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

	public static final String TABLE_VERSION = "version";
	public static final String COLUMN_DATE = "version_date";

	private static final String DATABASE_NAME = "bus_plan.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String STOPS_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_BUSSTOPS + "(" + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_ID +
			" TEXT NOT NULL PRIMARY KEY, " + COLUMN_LAT + " REAL, " + COLUMN_LONG + " REAL);";

	private static final String FAVORITE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES +
			"(" + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_ID + " TEXT NOT NULL PRIMARY KEY);";

	private static final String VERSION_CREATE = " CREATE TABLE " + TABLE_VERSION + "(" +
			COLUMN_DATE + " TEXT);";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d("DB CREATED", "Created");
		database.execSQL(STOPS_CREATE);
		database.execSQL(FAVORITE_CREATE);
		database.execSQL(VERSION_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUSSTOPS + ";");
		onCreate(db);
	}
}