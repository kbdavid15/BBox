package com.ryanallen.bbox;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

public class VideoFileHelper {
	private ArrayList<VideoFile> videoFiles;
	private SharedPreferences settings;
	private static final String VIDEO_PREFERENCES = "videoprefs";

	public VideoFileHelper(Context context) {
		settings = context.getSharedPreferences(VIDEO_PREFERENCES, Context.MODE_PRIVATE);
		getAllBboxVideos();
	}
	
	public void getAllBboxVideos() {
		// get the save directory for the black box recordings
		File videoStorageDir = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), 
				"BlackBox");
		
		// get a list of files
		if (videoStorageDir.exists()) {	
			File[] fileArray = videoStorageDir.listFiles();
			// sort by date modified
			Arrays.sort(fileArray, Collections.reverseOrder(new Comparator<File>() {
				public int compare(File f1, File f2)
				{
					return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
				}
			}));
			for (File file : fileArray) {
				if (!getVideoFiles().contains(file)) {
					VideoFile video = new VideoFile(file);
					// check the settings for the display name
					if (settings.contains(video.getAbsolutePath())) {
						video.setDisplayName(settings.getString(video.getAbsolutePath(), null));
					}
					videoFiles.add(video);
				}
			}
		}
	}
	
	public void notifyDeletedFile(VideoFile video) {
		videoFiles.remove(video);
	}
	
	/**
	 * 
	 * @param displayName	The new name to display to the user
	 * @param position		The position of the VideoFile in the array
	 */
	public void setVideoDisplayName(String displayName, int position) {
		// make sure to update the name in the settings file
		VideoFile video = videoFiles.get(position);
		video.setDisplayName(displayName);
		Editor editor = settings.edit();
		editor.putString(video.getAbsolutePath(), displayName);
		editor.apply();
	}
	
	public void removeKeyFromPrefs(String key) {
		if (settings.contains(key)) {
			Editor editor = settings.edit();
			editor.remove(key);
			editor.apply();
		}
	}
	
	public ArrayList<VideoFile> getVideoFiles() {
		// initialize, if necessary
		if (videoFiles == null) {
			videoFiles = new ArrayList<VideoFile>();
		}
		return videoFiles;
	}

	public void setVideoFiles(ArrayList<VideoFile> videoFiles) {
		this.videoFiles = videoFiles;
	}
}
