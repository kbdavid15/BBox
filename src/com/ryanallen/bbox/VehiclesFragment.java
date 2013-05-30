package com.ryanallen.bbox;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VehiclesFragment extends Fragment {
	private ArrayAdapter<Vehicle> myArrayAdapter;
	private ListView vehicleListView;
	private Vehicle[] vehicles = new Vehicle[2];
	
	private OnItemClickListener selectVehicleListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Toast.makeText(getActivity(), myArrayAdapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	public void onStart() {
		super.onStart();
		setupListView();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.vehicles_layout, container, false);
	}	
	
	private void setupListView() {
		// add dummy vehicles
		vehicles[0] = new Vehicle("Chevy", "Cruze");
		vehicles[1] = new Vehicle("Chevy", "Camaro");		
		
		vehicleListView = (ListView)getView().findViewById(R.id.listViewVehicles);
		
		// create an array adapter for the listview
		myArrayAdapter = new ArrayAdapter<Vehicle>(getActivity(), android.R.layout.simple_list_item_1, vehicles);		
		vehicleListView.setAdapter(myArrayAdapter);
        
        // add onClick listener for listview
		vehicleListView.setOnItemClickListener(selectVehicleListener); 
	}
}
