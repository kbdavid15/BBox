package com.ryanallen.bbox;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TwoLineListItem;

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
	
	class TwoLineAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<VideoFile> videoFiles;
		
		public TwoLineAdapter(Context context, ArrayList<VideoFile> videoFiles) {
			this.context = context;
			this.videoFiles = videoFiles;
		}

		@Override
		public int getCount() {
			return videoFiles.size();
		}
		@Override
		public Object getItem(int position) {
			return videoFiles.get(position);
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout twoLineLayout;
			
			if (convertView == null) {
				twoLineLayout = new LinearLayout(context);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				//TODO make a template in xml and use layout inflater
			} else {
				twoLineLayout = (LinearLayout)convertView;
			}
			
			return twoLineLayout;
		}
	}
	
	
	
	
	
	
}
