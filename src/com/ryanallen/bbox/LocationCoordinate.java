package com.ryanallen.bbox;

import java.util.Date;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

public class LocationCoordinate {
	private long id;
	private String fileName;
	private double latitude, longitude, speed, altitude;
	private long unix_time;
	private int videoElapsedTime;
	
	public static final double METERSSEC_2_MPH = 2.23694;
	public static final double METERSSEC_2_KPH = 3.6;

	/**
	 * Creates a LocationCoordinate based on cursor.
	 * @param cursor	Assumes a query returning all columns
	 */
	public LocationCoordinate(Cursor cursor, int videoElapsedTime) {
		id = cursor.getLong(0);
		fileName = cursor.getString(1);
		latitude = cursor.getDouble(2);
		longitude = cursor.getDouble(3);
		speed = cursor.getDouble(4);
		altitude = cursor.getDouble(5);
		unix_time = cursor.getInt(6);
		this.videoElapsedTime = videoElapsedTime;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public long getUnix_time() {
		return unix_time;
	}
	public void setUnix_time(int unix_time) {
		this.unix_time = unix_time;
	}
	public Date getDateTime() {
		return new Date(unix_time);
	}
	public void setDateTime(Date date) {
		this.unix_time = date.getTime();
	}
	public LatLng getLatLng() {
		return new LatLng(latitude, longitude);
	}
	public int getVideoElapsedTime() {
		return videoElapsedTime;
	}
	public void setVideoElapsedTime(int videoElapsedTime) {
		this.videoElapsedTime = videoElapsedTime;
	}
}
