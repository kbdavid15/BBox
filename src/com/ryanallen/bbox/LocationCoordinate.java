package com.ryanallen.bbox;

public class LocationCoordinate {
	private long id;
	private String fileName;
	private double latitude, longitude, speed;
	private int unix_time;
	
	public LocationCoordinate(long id, String filename, double latitude, double longitude, double speed, int unix_time) {
		this.id = id;
		this.fileName = filename;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.unix_time = unix_time;
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
	public int getUnix_time() {
		return unix_time;
	}
	public void setUnix_time(int unix_time) {
		this.unix_time = unix_time;
	}

}
