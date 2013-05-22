package com.ryanallen.bbox;

import java.util.Currency;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.view.Menu;
import android.view.View;


public class BBoxActivity extends Activity {
	
	private static final int REQUEST_START_RECORD = 1;
	private static final int REQUEST_SHOW_VEHICLES = 2;
	private static final int REQUEST_SHOW_OPTIONS = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbox);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		getMenuInflater().inflate(R.menu.bbox, menu);
		return true;
	}
	
	public void onStartRecord(View v) {
		Intent record = new Intent(this, RecordingActivity.class);
		startActivityForResult(record, REQUEST_START_RECORD);
	}
	
	public void showVehicles(View v){
		Intent record = new Intent(this, VehicleActivity.class);
		startActivityForResult(record, REQUEST_SHOW_VEHICLES);
	}
	
	public void showOptions(View v){
		Intent record = new Intent(this, OptionsActivity.class);
		startActivityForResult(record, REQUEST_SHOW_OPTIONS);
	}
	
}
