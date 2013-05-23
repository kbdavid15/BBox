package com.ryanallen.bbox;

import java.io.IOException;

import android.app.Activity;
import android.gesture.Prediction;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.ryanallen.bbox.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class RecordingActivity extends Activity {
	
	private Camera myCamera;
//	private SurfaceView cameraView;
	private CameraPreview mPreview;
	private FrameLayout mFrameLayoutPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);
		
		// create an instance of camera
		myCamera = getCameraInstance();
		
		mPreview = new CameraPreview(this, myCamera);
		mFrameLayoutPreview = (FrameLayout)findViewById(R.id.camera_preview);
		mFrameLayoutPreview.addView(mPreview);
		
//		myCamera = Camera.open();
//		// get existing default paramaters
//		Camera.Parameters cameraParameters = myCamera.getParameters();
//		
//		// modify the paramaters, if desired
//		
//		// set the new paramaters
//		myCamera.setParameters(cameraParameters);
//		
//		// get the surface view for the camera preview
//		//cameraView = (SurfaceView) findViewById(R.id.cameraView);
//		
//		SurfaceHolder holder = cameraView.getHolder();
//		holder.addCallback(new Callback() {
//			
//			@Override
//			public void surfaceDestroyed(SurfaceHolder holder) {
//				// TODO Auto-generated method stub
//				// release the camera
//				myCamera.release();
//			}
//			
//			@Override
//			public void surfaceCreated(SurfaceHolder holder) {
//				// TODO Auto-generated method stub
//				try {
//					myCamera.setPreviewDisplay(holder);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				myCamera.startPreview();
////				myCamera.unlock();
////				
////				MediaRecorder medRecorder = new MediaRecorder();
////				medRecorder.setCamera(myCamera);
////				medRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
////				medRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
////				medRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
////				medRecorder.setOutputFile(Media.getOutputMediaFile(Media.MEDIA_TYPE_VIDEO).toString());
////				medRecorder.setPreviewDisplay(holder.getSurface());
////				try {
////					medRecorder.prepare();
////				} catch (IllegalStateException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				medRecorder.start();
//
//			}
//			
//			@Override
//			public void surfaceChanged(SurfaceHolder holder, int format, int width,
//					int height) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
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
