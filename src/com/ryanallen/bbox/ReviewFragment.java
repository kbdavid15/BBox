package com.ryanallen.bbox;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ReviewFragment extends ListFragment {
	private ArrayAdapter<VideoFile> mArrayAdapter;
	ArrayList<VideoFile> videoFiles;
	private ListView reviewListView;
	
	private static final String SELECTED_VIDEO_FILE = "SELECTED_VIDEO_FILE";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.review_layout, container, false);
	}
	
//	@Override
//	public void onStart(){
//		super.onStart();
//		
//		// populate the listview with flagged videos
//		reviewListView = (ListView)getListView();
//	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// populate the listview with flagged videos
		reviewListView = (ListView)getListView();
		videoFiles = VideoFile.getAllBboxVideos();
		
		mArrayAdapter = new ArrayAdapter<VideoFile>(getActivity(), android.R.layout.simple_list_item_1, videoFiles);
		reviewListView.setAdapter(mArrayAdapter);
	}
	
	@Override
	/**
	 * Start the playback activity for the selected video
	 */
	public void onListItemClick(ListView l, View v, int position, long id) {
		// get the selected video file
		VideoFile selectedFile = (VideoFile)l.getItemAtPosition(position);
		
		Toast.makeText(getActivity(), selectedFile.getPath(), Toast.LENGTH_SHORT).show();
		
//		// setup the playback activity
//		Intent videoPlaybackIntent = new Intent(getActivity(), PlaybackActivity.class);
//		videoPlaybackIntent.putExtra(SELECTED_VIDEO_FILE, selectedFile.filePath);
	}
}
