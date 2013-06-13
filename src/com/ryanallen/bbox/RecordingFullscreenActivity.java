package com.ryanallen.bbox;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class RecordingFullscreenActivity extends Activity implements 
SurfaceHolder.Callback, 
GooglePlayServicesClient.ConnectionCallbacks, 
GooglePlayServicesClient.OnConnectionFailedListener,
com.google.android.gms.location.LocationListener {
	private Camera mCamera;
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	public MediaRecorder mRecorder = new MediaRecorder();

	private SharedPreferences settings;

	private String videoFilePath = null;
	private long recordingStartTime;

	private ConnectionResult connectionResult;
	private int location_update_interval = 0;
	// The fastest update frequency, in milliseconds
	private static final int FASTEST_INTERVAL = 1000;
	// Define an object that holds accuracy and frequency parameters
	LocationRequest mLocationRequest;
	LocationClient mLocationClient;
	boolean mUpdatesRequested;

	private MyDbOpenHelper mDbHelper;
	private SQLiteDatabase mSQLdb;

	public final int VIDEO_LENGTH_MILLIS = 30000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording_fullscreen);

		// get UI element
		surfaceView = (SurfaceView)findViewById(R.id.surface_camera);
		mCamera = Camera.open();

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);

		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// restore prefs
		settings = PreferenceManager.getDefaultSharedPreferences(this);

		// location stuff
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the update interval with the options menu item
		String location_interval = settings.getString("location_update_freq", "1000");
		location_update_interval = Integer.parseInt(location_interval);
		mLocationRequest.setInterval(location_update_interval);

		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		//TODO use location update freq from settings
		mLocationClient = new LocationClient(this,this,this);

		// store the location data in the database
		mDbHelper = new MyDbOpenHelper(this);
	}

//	@Override
//	public void onResume() {
//		super.onResume();
//
//		// Get the Camera instance as the activity achieves full user focus
//		if (myCamera == null) {
//			initializeCamera(); // Local method to handle camera init
//		}
//		//TODO look into why the integer array of values didn't work
//		String quality = settings.getString("video_quality", String.valueOf(CamcorderProfile.QUALITY_HIGH));
//		CamcorderProfile profile = CamcorderProfile.get(Integer.parseInt(quality));
//
//		//set camera params - doesn't really seem to help much
//		Camera.Parameters params = myCamera.getParameters();
//		params.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
//		myCamera.setParameters(params);
//
//	}

	public void prepareAndStartRecording() {
		if (mCamera == null) {
			mCamera = Camera.open();
		}
		videoFilePath = Media.getOutputMediaFile(Media.MEDIA_TYPE_VIDEO).toString();

		mRecorder = new MediaRecorder();

		mCamera.lock();
		mCamera.unlock();

		mRecorder.setCamera(mCamera);
		mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);

		// set the recording quality based on the settings in options menu
		try {
			//TODO look into why the integer array of values didn't work
			String quality = settings.getString("video_quality", String.valueOf(CamcorderProfile.QUALITY_LOW));
			mRecorder.setProfile(CamcorderProfile.get(Integer.parseInt(quality)));
		} catch (RuntimeException e) {
			// the selected quality is not available
			mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
		}

		mRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		mRecorder.setOutputFile(videoFilePath);
		mRecorder.setMaxDuration(VIDEO_LENGTH_MILLIS);
		mRecorder.setOnInfoListener(stopListener);

		// prepare the media recorder
		try {
			mRecorder.prepare();
			mRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MediaRecorder.OnInfoListener stopListener = new MediaRecorder.OnInfoListener(){
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			switch (what) {
			case (MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED):
				mr.stop();
				mr.release();
				prepareAndStartRecording();
				break;
			}
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	protected void stopRecording() {
		if(mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mCamera.release();
			mCamera.lock();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();  // Always call the superclass method first

		// if video is recording, stop it
		if (mRecorder != null) {
			try {
				//stop recording
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Release the Camera because we don't need it when paused
		// and other activities might need to use it.
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera != null) {
			String quality = settings.getString("video_quality", String.valueOf(CamcorderProfile.QUALITY_HIGH));
			CamcorderProfile profile = CamcorderProfile.get(Integer.parseInt(quality));
			Parameters params = mCamera.getParameters();
			params.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
			mCamera.setParameters(params);
			
			prepareAndStartRecording();
		} else {
			Toast.makeText(getApplicationContext(), "Camera not available!",
                    Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
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
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		this.connectionResult = connectionResult;
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			showDialog(connectionResult.getErrorCode());
		}

	}
	/**
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		if(mLocationRequest != null){
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}
	/**
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		// store the location in the database
		mSQLdb = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MyDbOpenHelper.COLUMN_FILENAME, videoFilePath);
		values.put(MyDbOpenHelper.COLUMN_LATITUDE, location.getLatitude());
		values.put(MyDbOpenHelper.COLUMN_LONGITUDE, location.getLongitude());
		values.put(MyDbOpenHelper.COLUMN_SPEED, location.getSpeed());
		values.put(MyDbOpenHelper.COLUMN_ALTITUDE, location.getAltitude());
		values.put(MyDbOpenHelper.COLUMN_TIMESTAMP, location.getTime());
		// get the current location of the video recording
		long duration = System.currentTimeMillis() - recordingStartTime;

		long id = mSQLdb.insert(MyDbOpenHelper.TABLE_GPS_LOCATION_NAME, null, values);

		ContentValues vidValues = new ContentValues();
		vidValues.put(MyDbOpenHelper.COLUMN_ID, id);
		vidValues.put(MyDbOpenHelper.COLUMN_VIDEO_ELAPSED, duration);
		mSQLdb.insert(MyDbOpenHelper.TABLE_VIDEO_LOCATION, null, vidValues);
	}

	@Override
	protected void onStop() {
		if(mLocationRequest != null){
			// If the client is connected
			if (mLocationClient.isConnected()) {
				/*
				 * Remove location updates for a listener.
				 * The current Activity is the listener, so
				 * the argument is "this".
				 */
				mLocationClient.removeLocationUpdates(this);
			}
			/*
			 * After disconnect() is called, the client is
			 * considered "dead".
			 */
			mLocationClient.disconnect();
			mSQLdb.close();
			super.onStop();
		}
	}

	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;
		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST :
			/*
			 * If the result code is Activity.RESULT_OK, try
			 * to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK :
				/*
				 * Try the request again
				 */
				break;
			}
		}
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates",
					"Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			int errorCode = connectionResult.getErrorCode();
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					errorCode,
					this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);
			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment =
						new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(
						getFragmentManager(),
						"Location Updates");
			}
			return false;
		}
	}
}
