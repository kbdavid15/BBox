package com.ryanallen.bbox;

import java.io.File;
import java.util.ArrayList;
import android.os.Environment;

public class VideoFile extends File {
	private static final long serialVersionUID = -6694017648316508704L;
	public String filePath;
	
	public VideoFile(String path) {
		super(path);
		filePath = path;
		// TODO Auto-generated constructor stub
	}
	
	public static ArrayList<VideoFile> getAllBboxVideos() {
		// get the save directory for the black box recordings
		File videoStorageDir = new File(
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), 
			"BlackBox");
		
		// get a list of files
		if (videoStorageDir.exists()) {
			ArrayList<VideoFile> videoFileList = new ArrayList<VideoFile>();
			File[] fileArray = videoStorageDir.listFiles();
			for (File file : fileArray) {
				videoFileList.add(new VideoFile(file.getAbsolutePath()));
			}
			return videoFileList;
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
