package com.ryanallen.bbox;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 *
 */
public class PlaybackActivity extends Activity {

	private static final String VIDEO_POSITION = "video_position";
	private String videoPath;
	private VideoView mVideoView;
	private FragmentManager fragmentManager;
	private VideoMapFragment mapFragment;
	private ArrayList<LocationCoordinate> allPoints;

	private MyDbOpenHelper mDbHelper;
	private SQLiteDatabase db;
	private String[] allColumns = { MyDbOpenHelper.COLUMN_ID, MyDbOpenHelper.COLUMN_FILENAME, MyDbOpenHelper.COLUMN_LATITUDE,
			MyDbOpenHelper.COLUMN_LONGITUDE, MyDbOpenHelper.COLUMN_SPEED, MyDbOpenHelper.COLUMN_ALTITUDE, MyDbOpenHelper.COLUMN_TIMESTAMP };

	private int videoPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			// restore variables from saved instance
			if (savedInstanceState.containsKey(VIDEO_POSITION)) {
				videoPosition = savedInstanceState.getInt(VIDEO_POSITION);
			}
		}
		// check the orientation
		Configuration config = getResources().getConfiguration();
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		switch (config.orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			// go into fullscreen mode
		    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		    //requestWindowFeature(Window.FEATURE_NO_TITLE);
		    getWindow().setAttributes(attrs);
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			// go non-full screen
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attrs);
			break;
		}

		setContentView(R.layout.activity_playback);

		// get the values from the parent activity
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			videoPath = extras.getString(ReviewFragment.SELECTED_VIDEO_FILE);
		}

		// set up the database helper
		mDbHelper = new MyDbOpenHelper(this);		

		fragmentManager = getFragmentManager();
		mapFragment = (VideoMapFragment)fragmentManager.findFragmentById(R.id.mapFragment);

		allPoints = getAllPoints();

		// load the video
		mVideoView = (VideoView)findViewById(R.id.videoView1);

		// setup the video
		mVideoView.setVideoPath(videoPath);
		mVideoView.setMediaController(new MediaController(this));

		// link the video to the mapfragment
		mapFragment.setVideoView(mVideoView);

		// start the video 
		mVideoView.start();

		// add polyline
		mapFragment.addPolyline(allPoints);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (videoPosition != 0) {
			mVideoView.seekTo(videoPosition);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (db != null) {
			db.close();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// save the current position of the video
		videoPosition = mVideoView.getCurrentPosition();
	}

	public ArrayList<LocationCoordinate> getAllPoints() {
		ArrayList<LocationCoordinate> points = new ArrayList<LocationCoordinate>();
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

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}
	
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(VIDEO_POSITION, videoPosition);
		super.onSaveInstanceState(outState);
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
