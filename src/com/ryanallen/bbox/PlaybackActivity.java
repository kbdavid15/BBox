package com.ryanallen.bbox;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
//import tigerbeer;
/**
 *
 */
public class PlaybackActivity extends Activity implements MediaPlayer.OnCompletionListener {

	private static final String VIDEO_POSITION = "video_position";
	private String videoPath;
	private VideoView mVideoView;
	private TextView speedTextView;
	private FragmentManager fragmentManager;
	private VideoMapFragment mapFragment;
	private ArrayList<LocationCoordinate> locationCoordinateList;
	private MyDbOpenHelper mDbHelper;
	private SharedPreferences settings;
	private boolean show_map;
	private long playbackStartTime;
	
	private Handler mHandler;

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
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		show_map = settings.getBoolean("show_map", true);
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
		fragmentManager = getFragmentManager();
		mapFragment = (VideoMapFragment)fragmentManager.findFragmentById(R.id.mapFragment);
		speedTextView = (TextView)findViewById(R.id.textViewSpeedDisplay);
		
		if (!show_map) {
			mapFragment.getView().setVisibility(View.GONE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		// get the values from the parent activity
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			videoPath = extras.getString(ReviewFragment.SELECTED_VIDEO_FILE);
		}
		
		// set up handler
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				speedTextView.setText((String)msg.obj);
				Bundle b = msg.getData();
				LatLng latlng = new LatLng(b.getDouble("latitude"), b.getDouble("longitude"));
				mapFragment.updateMarkerPosition(latlng);
			}
		};

		// set up the database helper
		mDbHelper = new MyDbOpenHelper(this);

		locationCoordinateList = mDbHelper.getAllPointsForFile(videoPath);

		// load the video
		mVideoView = (VideoView)findViewById(R.id.videoView1);
		
		// set listeners
		mVideoView.setOnCompletionListener(this);
		//mVideoView.setOnSeekCompletedListener(this);
		
		// setup the video
		mVideoView.setVideoPath(videoPath);
		mVideoView.setMediaController(new MediaController(this));

		// start the video 
		mVideoView.start();		
		playbackStartTime = System.currentTimeMillis();
		
		// start the scheduler
		setupExecutorService();

		// add polyline
		mapFragment.addPolyline(locationCoordinateList);
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
		if (mDbHelper.getDatabase() != null) {
			mDbHelper.close();
		}
		scheduler.shutdownNow();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// save the current position of the video
		videoPosition = mVideoView.getCurrentPosition();
		scheduler.shutdownNow();
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
	
	/**
	 * Schedule a callback to run every second while the video is playing
	 * Update the speed textview and the location on the map
	 */
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private int index = 0;
	public void setupExecutorService() {
		final Runnable runner = new Runnable() {
			long duration;
			@Override
			public void run() {
				// this is called every 1 second, while the video is playing
				// get the speed that corresponds with the current video time
				duration = System.currentTimeMillis() - playbackStartTime;
//				int loc = locationCoordinateList.get(index).getVideoElapsedTime();
//				Log.d("Duration", "Getcurrentposition: " + String.valueOf(duration));
//				Log.d("Duration", "Location:  " + String.valueOf(loc));
//				Log.d("Duration", "Index:  " + String.valueOf(index));
				if (duration > locationCoordinateList.get(index).getVideoElapsedTime()) {
					LocationCoordinate coord = locationCoordinateList.get(index);
					Message msg = new Message();
					msg.obj = String.format("%.1f", coord.getSpeed() * LocationCoordinate.METERSSEC_2_MPH);
					Bundle b = new Bundle();
					b.putDouble("latitude", coord.getLatitude());
					b.putDouble("longitude", coord.getLongitude());
					msg.setData(b);
					mHandler.sendMessage(msg);
					index++;
				}
			}
		};
		scheduler.scheduleAtFixedRate(runner, 0, 1500, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		// TODO Auto-generated method stub
		scheduler.shutdown();
	}
}
