package com.ryanallen.bbox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbOpenHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "locations.db";
	private static final int DB_VERSION = 1;
	
	private static final String CREATE_TABLE = 
			"CREATE TABLE IF NOT EXISTS `videolocation` (" +
			  "`id` int(10) unsigned NOT NULL AUTO_INCREMENT, " +
			  "`filename` varchar(25) NOT NULL," +
			  "`latitude` double NOT NULL," +
			  "`longitude` double NOT NULL," +
			  "`time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
			  "PRIMARY KEY (`id`)," +
			  "KEY `filename` (`filename`));";

	public MyDbOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
