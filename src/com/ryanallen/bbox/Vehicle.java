package com.ryanallen.bbox;

public class Vehicle {
	
	private String make;
	private String model;
	
	public Vehicle(String make, String model) {
		this.make = make;
		this.model = model;
	}
	
	@Override
	public String toString(){
		
		return make + " " + model;
	}
}
