package com.ryanallen.bbox;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDbOpenHelper extends SQLiteOpenHelper {
	private SQLiteDatabase database;
	private String[] allColumns = { COLUMN_ID, COLUMN_FILENAME, COLUMN_LATITUDE,
			COLUMN_LONGITUDE, COLUMN_SPEED, COLUMN_ALTITUDE, COLUMN_TIMESTAMP };
	private static final String DB_NAME = "locations.db";
	private static final int DB_VERSION = 5;
	
	public static final String TABLE_GPS_LOCATION_NAME = "gpslocation";
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

	private static final String CREATE_GPS_LOCATION_TABLE = 
			"create table if not exists " + TABLE_GPS_LOCATION_NAME + "(" +
					COLUMN_ID + " integer primary key autoincrement, " +
					COLUMN_FILENAME + " text, " +
					COLUMN_LATITUDE + " real not null, " +
					COLUMN_LONGITUDE + " real not null, " +
					COLUMN_SPEED + " real not null, " +
					COLUMN_ALTITUDE + " real not null, " +
					COLUMN_TIMESTAMP + " integer not null);";
	
	// create a table to map the location timestamps to the video timestamps
	/** id from gpslocation corresponds to id in videolocation */
	public static final String TABLE_VIDEO_LOCATION = "videolocation";	
	public static final String COLUMN_VIDEO_ELAPSED = "elapsedtime";
	
	private static final String CREATE_VIDEO_LOCATION_TABLE = 
			"create table if not exists " + TABLE_VIDEO_LOCATION + "(" +
					COLUMN_ID + " integer primary key, " + 
					COLUMN_VIDEO_ELAPSED + " integer not null);";

	public MyDbOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_GPS_LOCATION_TABLE);
		db.execSQL(CREATE_VIDEO_LOCATION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MyDbOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS_LOCATION_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO_LOCATION);
		onCreate(db);
	}
	
	public ArrayList<LocationCoordinate> getAllPointsForFile(String filePath) {
		ArrayList<LocationCoordinate> points = new ArrayList<LocationCoordinate>();
		database = getReadableDatabase();
		
		// find the data associated with the chosen video
		String selectCriteria = COLUMN_FILENAME + " = '" + filePath + "'";
		Cursor cursor = database.query(TABLE_GPS_LOCATION_NAME, allColumns, selectCriteria, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String selectId = COLUMN_ID + " = " + cursor.getLong(0);
			Cursor vidCursor = database.query(TABLE_VIDEO_LOCATION, new String[] { COLUMN_VIDEO_ELAPSED }, selectId, null, null, null, null);
			vidCursor.moveToFirst();
			points.add(new LocationCoordinate(cursor, vidCursor.getInt(0)));
			cursor.moveToNext();
		}
		database.close();
		return points;
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}
}
