package com.ryanallen.bbox;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReviewFragment extends ListFragment {
	private ArrayAdapter<VideoFile> mArrayAdapter;
	private ListView reviewListView;
	
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
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
	}
}
