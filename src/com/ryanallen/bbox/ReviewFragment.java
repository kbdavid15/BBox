package com.ryanallen.bbox;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ReviewFragment extends ListFragment implements OnItemLongClickListener {
	private DetailAdapter mDetailAdapter;	
	private ArrayList<VideoFile> videoFiles;
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

		// if there are no video files, tell the user
		if (videoFiles.size() != 0) {
			mDetailAdapter = new DetailAdapter(getActivity(), videoFiles);		
			reviewListView.setAdapter(mDetailAdapter);
			// add listener (don't add the listener if there are no files)
			reviewListView.setOnItemLongClickListener(this);
		} else {
			String[] errorText = { "No videos found" };
			reviewListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, errorText));
		}
	}

	@Override
	/**
	 * Start the playback activity for the selected video
	 */
	public void onListItemClick(ListView l, View v, int position, long id) {
		// get the selected video file
		VideoFile selectedFile = null;
		try {
			selectedFile = (VideoFile)l.getItemAtPosition(position);
		} catch (ClassCastException e) {
			return;
		}
		// setup the playback activity
		Intent videoPlaybackIntent = new Intent(getActivity(), PlaybackActivity.class);
		videoPlaybackIntent.putExtra(SELECTED_VIDEO_FILE, selectedFile.getPath());

		// start the activity
		startActivity(videoPlaybackIntent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		final ListView listView = (ListView)parent;
		// get the selected video file
		final VideoFile selectedFile;
		try {
			selectedFile = (VideoFile)parent.getItemAtPosition(position);
		} catch (ClassCastException e) {
			return false;
		}
		
		// show a dialog to either edit or delete the video file
		DialogFragment dialog = new DialogFragment() {
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				return new AlertDialog.Builder(getActivity())
				.setItems(R.array.alert_edit_or_delete, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							// edit the video name
							
							break;
						case 1:
							// delete the selected video
							selectedFile.delete();
							((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
//							Intent intent = getActivity().getIntent();
//							getActivity().finish();
//							startActivity(intent);
							break;
						default:
							
						}
					}
				}).create();
			}
		};
		dialog.show(getFragmentManager(), "dialog");
		return true;
	}
	
	public static class EditVideoNameDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			LayoutInflater factory = LayoutInflater.from(getActivity());
            final View textEntryView = factory.inflate(R.layout.edit_video_dialog, null);
            return new AlertDialog.Builder(getActivity())
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.alert_dialog_text_entry)
                .setView(textEntryView)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                        /* User clicked OK so do some stuff */
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
		}
	}
	
}
