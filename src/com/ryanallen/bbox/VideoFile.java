package com.ryanallen.bbox;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VideoFile extends File {
	private static final long serialVersionUID = -6694017648316508704L;
	
	public VideoFile(String path) {
		super(path);
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

class DetailAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<VideoFile> videoFiles;
	private LayoutInflater inflater;
	
	public DetailAdapter(Context context, ArrayList<VideoFile> videoFiles) {
		this.context = context;
		this.videoFiles = videoFiles;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		if (convertView == null) {
            convertView = inflater.inflate(R.layout.video_file_item_layout, null);
        }
		
		// get handles on the TextViews
		TextView titleText = (TextView)convertView.findViewById(R.id.textViewTitle);
		TextView detailText = (TextView)convertView.findViewById(R.id.textViewDetail);
		
		// set the text on the TextViews
		VideoFile video = (VideoFile)getItem(position);
		titleText.setText(video.getName());
		Date date = new Date(video.lastModified());
		detailText.setText(DateFormat.format("EEEE, MMMM d, yyyy hh:mm A", date));
		return convertView;
	}
}
