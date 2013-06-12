package com.ryanallen.bbox;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VideoFile extends File {
	private static final long serialVersionUID = -6694017648316508704L;
	private String displayName = null;

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public VideoFile(String path) {
		super(path);
	}
	public VideoFile(File file) {
		super(file.getAbsolutePath());
	}

	public boolean delete(VideoFileHelper helper) {
		// remove the setting in shared preferences, if it exists
		helper.removeKeyFromPrefs(getAbsolutePath());
		helper.notifyDeletedFile(this);
		return super.delete();
	}
	
	@Override
	public String toString() {
		if (displayName == null) {
			return getName();
		} else {
			return displayName;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof File))
			return false;

		File rhs = (File)obj;
		return new EqualsBuilder()
		.appendSuper(super.equals(obj))
		.append(getAbsoluteFile(), rhs.getAbsolutePath())
		.isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,31)
		.appendSuper(super.hashCode())
		.append(getAbsolutePath())
		.toHashCode();
	}
}

class DetailAdapter extends BaseAdapter {
	private ArrayList<VideoFile> videoFiles;
	private LayoutInflater inflater;
	private VideoFileHelper fileHelper;

	public DetailAdapter(Context context, VideoFileHelper helper) {
		this.fileHelper = helper;
		this.videoFiles = fileHelper.getVideoFiles();
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
		titleText.setText(video.toString());
		Date date = new Date(video.lastModified());
		detailText.setText(DateFormat.format("EEEE, MMMM d, yyyy hh:mm A", date));
		return convertView;
	}
}
