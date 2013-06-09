package com.ryanallen.bbox;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 */
public class PlaybackActivity extends Activity {

	private String videoPath;
	private VideoView mVideoView;
	private FragmentManager fragmentManager;
	private MapFragment mapFragment;
	private GoogleMap map;
	private List<LocationCoordinate> allPoints;

	private MyDbOpenHelper mDbHelper;
	private SQLiteDatabase db;
	private String[] allColumns = { MyDbOpenHelper.COLUMN_ID, MyDbOpenHelper.COLUMN_FILENAME, MyDbOpenHelper.COLUMN_LATITUDE,
			MyDbOpenHelper.COLUMN_LONGITUDE, MyDbOpenHelper.COLUMN_SPEED, MyDbOpenHelper.COLUMN_TIMESTAMP };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_playback);

		// get the values from the parent activity
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			videoPath = extras.getString(ReviewFragment.SELECTED_VIDEO_FILE);
		}

		// set up the database helper
		mDbHelper = new MyDbOpenHelper(this);		

		fragmentManager = getFragmentManager();
		mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.mapFragment);

		allPoints = getAllPoints();
		
		// load the video
		mVideoView = (VideoView)findViewById(R.id.videoView1);
		
		// setup the video
		mVideoView.setVideoPath(videoPath);
		mVideoView.setMediaController(new MediaController(this));

		// start the video 
		mVideoView.start();
		
		configureMapFragment();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		switch (newConfig.orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			// go into fullscreen mode
		    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		    getWindow().setAttributes(attrs);
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			// go non-full screen
		    attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		    getWindow().setAttributes(attrs);
			break;
		}
	}
	
	@Override
	protected void onStop() {
		if (db != null) {
			db.close();
		}
		super.onStop();
	}

	public List<LocationCoordinate> getAllPoints() {
		List<LocationCoordinate> points = new ArrayList<LocationCoordinate>();
		db = mDbHelper.getReadableDatabase();

		// find the data associated with the chosen video
		String selectCriteria = MyDbOpenHelper.COLUMN_FILENAME + " = '" + videoPath + "'";
		Cursor cursor = db.query(MyDbOpenHelper.TABLE_NAME, allColumns, 
				selectCriteria, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			points.add(new LocationCoordinate(cursor));
			cursor.moveToNext();
		}
		return points;
	}

	private void configureMapFragment() {
		map = mapFragment.getMap();
		LatLng point = new LatLng(allPoints.get(0).getLatitude(), allPoints.get(0).getLongitude());
		map.addMarker(new MarkerOptions().position(point));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
