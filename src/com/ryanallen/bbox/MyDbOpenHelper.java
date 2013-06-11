package com.ryanallen.bbox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDbOpenHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "locations.db";
	private static final int DB_VERSION = 3;
	public static final String TABLE_NAME = "videolocation";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FILENAME = "filename";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	/** Speed in meters/second */
	public static final String COLUMN_SPEED = "speed";
	/** Altitude if available, in meters above sea level. If this location does not have an altitude then 0.0 is returned. */
	public static final String COLUMN_ALTITUDE = "altitude";
	/** UTC time of this fix, in milliseconds since January 1, 1970 */
	public static final String COLUMN_TIMESTAMP = "timestamp";

	private static final String CREATE_TABLE = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
					COLUMN_ID + " integer primary key autoincrement, " +
					COLUMN_FILENAME + " text, " +
					COLUMN_LATITUDE + " real not null, " +
					COLUMN_LONGITUDE + " real not null, " +
					COLUMN_SPEED + " real not null, " +
					COLUMN_ALTITUDE + " real not null, " +
					COLUMN_TIMESTAMP + " integer not null);";

	public MyDbOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MyDbOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}
