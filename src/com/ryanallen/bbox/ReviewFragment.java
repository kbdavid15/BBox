package com.ryanallen.bbox;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class ReviewFragment extends ListFragment {
	private DetailAdapter mDetailAdapter;	
	ArrayList<VideoFile> videoFiles;
	private ListView reviewListView;
	
	public static final String SELECTED_VIDEO_FILE = "SELECTED_VIDEO_FILE";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.review_layout, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// populate the listview with flagged videos
		reviewListView = (ListView)getListView();
		videoFiles = VideoFile.getAllBboxVideos();
		
		mDetailAdapter = new DetailAdapter(getActivity(), videoFiles);		
		reviewListView.setAdapter(mDetailAdapter);
	}
	
	@Override
	/**
	 * Start the playback activity for the selected video
	 */
	public void onListItemClick(ListView l, View v, int position, long id) {
		// get the selected video file
		VideoFile selectedFile = (VideoFile)l.getItemAtPosition(position);
		
		// setup the playback activity
		Intent videoPlaybackIntent = new Intent(getActivity(), PlaybackActivity.class);
		videoPlaybackIntent.putExtra(SELECTED_VIDEO_FILE, selectedFile.getPath());
		
		// start the activity
		//startActivity(videoPlaybackIntent);
	}
}
