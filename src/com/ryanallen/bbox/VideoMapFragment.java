package com.ryanallen.bbox;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
	
	public static final double METERSSEC_2_MPH = 2.23694;
	public static final double METERSSEC_2_KPH = 3.6;
	
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
		if (allPoints.size() == 0) {
			return;
		}
		this.locations = allPoints;
		final LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LocationCoordinate coord : locations) {
			LatLng point = coord.getLatLng();
			latLngPoints.add(point);
			builder.include(point);
		}
		line = map.addPolyline(new PolylineOptions().addAll(latLngPoints).width(5).color(Color.RED));
		// center the map on the last point
		map.setOnCameraChangeListener(new OnCameraChangeListener() {		
			@Override
			public void onCameraChange(CameraPosition arg0) {
				// Move camera.
		        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
		        // Remove listener to prevent position reset on camera move.
		        map.setOnCameraChangeListener(null);
			}
		});
	}
}
