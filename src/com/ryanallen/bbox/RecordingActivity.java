package com.ryanallen.bbox;

import java.io.IOException;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.CameraPosition;
import com.ryanallen.bbox.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class RecordingActivity extends Activity {
	private Camera myCamera;
	private CameraPreview mPreview;
	private FrameLayout mFrameLayoutPreview;
	private MediaRecorder mMediaRecorder;
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// restore prefs
		settings = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();		
		// Get the Camera instance as the activity achieves full user focus
	    if (myCamera == null) {
	        initializeCamera(); // Local method to handle camera init
	    }
	}
	
	private boolean initializeCamera() {
		// create an instance of camera
		myCamera = getCameraInstance();
		
		if (myCamera == null) return false;
		
		if (mPreview == null) {
			mPreview = new CameraPreview(this, myCamera);
			mFrameLayoutPreview = (FrameLayout)findViewById(R.id.camera_preview);
			mFrameLayoutPreview.addView(mPreview);
		}
		return true;
	}

	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    
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
				String quality = settings.getString("video_quality", String.valueOf(CamcorderProfile.QUALITY_HIGH));
				mMediaRecorder.setProfile(CamcorderProfile.get(Integer.parseInt(quality)));
				Toast.makeText(this, "Recording quality: " + quality, Toast.LENGTH_SHORT).show();
			} catch (RuntimeException e) {
				// the selected quality is not available
				Toast.makeText(this, "The selected video quality is not available. Using highest available quality.", Toast.LENGTH_LONG).show();
				mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			}
			
			mMediaRecorder.setOutputFile(Media.getOutputMediaFile(Media.MEDIA_TYPE_VIDEO).toString());
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
}
