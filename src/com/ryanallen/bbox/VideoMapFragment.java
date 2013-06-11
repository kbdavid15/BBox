package com.ryanallen.bbox;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * @author Kyle
 *
 */
public class VideoMapFragment extends MapFragment {
	private GoogleMap map;
	private ArrayList<LocationCoordinate> locations;
	private ArrayList<LatLng> latLngPoints = new ArrayList<LatLng>();
	private Polyline line;
	private VideoView videoView;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		map = getMap();
	}
	
	public void setVideoView(VideoView view) {
		videoView = view;
	}
	
	public void addPolyline(ArrayList<LocationCoordinate> allPoints) {
		this.locations = allPoints;
		for (LocationCoordinate coord : locations) {
			latLngPoints.add(coord.getLatLng());
		}
		line = map.addPolyline(new PolylineOptions().addAll(latLngPoints).width(5).color(Color.RED));
		// center the map on the last point
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPoints.get(latLngPoints.size() - 1), 16));
	}
}
