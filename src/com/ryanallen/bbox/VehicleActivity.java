package com.ryanallen.bbox;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class VehicleActivity extends Activity {
	
	private ArrayAdapter<Vehicle> myArrayAdapter;
	private ListView vehicleListView;
	private Vehicle[] vehicles = new Vehicle[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicles);
		
		// add dummy vehicles
		vehicles[0] = new Vehicle("Chevy", "Cruze");
		vehicles[1] = new Vehicle("Chevy", "Camero");
		
		
		vehicleListView = (ListView) findViewById(R.id.listViewVehicles);
		
		// create an array adapter for the listview
		myArrayAdapter = new ArrayAdapter<Vehicle>(this, android.R.layout.simple_list_item_1, vehicles);
		
		vehicleListView.setAdapter(myArrayAdapter);
        
        // add onClick listener for listview
		vehicleListView.setOnItemClickListener(selectVehicleListener); 
	}
	
	private OnItemClickListener selectVehicleListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Toast.makeText(getBaseContext(), myArrayAdapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vehicles, menu);
		return true;
	}

}
