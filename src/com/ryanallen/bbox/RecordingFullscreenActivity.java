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
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

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
public class RecordingFullscreenActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, 
GooglePlayServicesClient.OnConnectionFailedListener,
com.google.android.gms.location.LocationListener {
	private Camera myCamera;
	private CameraPreview mPreview;
	private FrameLayout mFrameLayoutPreview;
	private MediaRecorder mMediaRecorder;
	private SharedPreferences settings;
	private String videoFilePath = null;

	private ConnectionResult connectionResult;
	public static String location_interval;
	public static int location_update_interval = 0;
	// The fastest update frequency, in milliseconds
	private static final int FASTEST_INTERVAL = 1000;
	// Define an object that holds accuracy and frequency parameters
	LocationRequest mLocationRequest;
	LocationClient mLocationClient;
	boolean mUpdatesRequested;

	private MyDbOpenHelper mDbHelper;
	private SQLiteDatabase mSQLdb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_recording_fullscreen);
		// restore prefs
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		// location stuff
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		
		// Set the update interval with the options menu item
		location_interval = settings.getString("location_update_freq", "1000");
		location_update_interval = Integer.parseInt(location_interval);
		mLocationRequest.setInterval(location_update_interval);
		
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		//TODO use location update freq from settings
		mLocationClient = new LocationClient(this,this,this);

		// store the location data in the database
		mDbHelper = new MyDbOpenHelper(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Get the Camera instance as the activity achieves full user focus
		if (myCamera == null) {
			initializeCamera(); // Local method to handle camera init
		}
		//TODO look into why the integer array of values didn't work
		String quality = settings.getString("video_quality", String.valueOf(CamcorderProfile.QUALITY_HIGH));
		CamcorderProfile profile = CamcorderProfile.get(Integer.parseInt(quality));

		//set camera params - doesn't really seem to help much
		Camera.Parameters params = myCamera.getParameters();
		params.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
		myCamera.setParameters(params);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}
	
	private boolean initializeCamera() {
		// create an instance of camera
		myCamera = getCameraInstance();

		if (myCamera == null) return false;

		if (mPreview == null) {
			mPreview = new CameraPreview(this, myCamera);
			mPreview.setKeepScreenOn(true);
			mFrameLayoutPreview = (FrameLayout)findViewById(R.id.test);
			mFrameLayoutPreview.addView(mPreview,mFrameLayoutPreview.getChildCount()-1);
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();  // Always call the superclass method first
		
		// if video is recording, stop it
		if (mMediaRecorder != null) {
			try {
				// stop recording
				mMediaRecorder.stop();
				mMediaRecorder.reset();
				mMediaRecorder.release();
				myCamera.lock();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Release the Camera because we don't need it when paused
		// and other activities might need to use it.
		if (myCamera != null) {
			myCamera.release();
			myCamera = null;
		}
	}
	
	public void captureButton_Click(View v) {
		if (((ToggleButton)v).isChecked()) {	    	
			// start recording
			mMediaRecorder = new MediaRecorder();
			myCamera.unlock();
			mMediaRecorder.setCamera(myCamera);
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// set the recording quality based on the settings in options menu
			try {
				//TODO look into why the integer array of values didn't work
				String quality = settings.getString("video_quality", String.valueOf(CamcorderProfile.QUALITY_LOW));
				mMediaRecorder.setProfile(CamcorderProfile.get(Integer.parseInt(quality)));
				//Toast.makeText(this, "Recording quality: " + quality, Toast.LENGTH_SHORT).show();
			} catch (RuntimeException e) {
				// the selected quality is not available
				//Toast.makeText(this, "The selected video quality is not available.", Toast.LENGTH_SHORT).show();
				mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
			}
			videoFilePath = Media.getOutputMediaFile(Media.MEDIA_TYPE_VIDEO).toString();
			mMediaRecorder.setOutputFile(videoFilePath);
			mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

			// prepare the media recorder
			try {
				mMediaRecorder.prepare();
				mMediaRecorder.start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// stop recording
			mMediaRecorder.stop();
			mMediaRecorder.reset();
			mMediaRecorder.release();
			myCamera.lock();	    	
		}
	}


	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
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
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
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

		mSQLdb.insert(MyDbOpenHelper.TABLE_NAME, null, values);        
	}

	@Override
	protected void onStop() {
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
