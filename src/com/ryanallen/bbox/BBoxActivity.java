package com.ryanallen.bbox;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class BBoxActivity extends Activity {
	
	private static final int REQUEST_START_RECORD = 1;
	private static final int REQUEST_SHOW_VEHICLES = 2;
	private static final int REQUEST_SHOW_OPTIONS = 3;
	
	// ui elements
	private View fragmentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bbox_activity_layout);
		
		fragmentView = (View)findViewById(R.id.fragmentView);
		
		// setup action bar for tabs
	    ActionBar actionBar = getActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    actionBar.setDisplayShowTitleEnabled(false);	
		
		Tab tab = actionBar.newTab().setText("Home").setTabListener(new TabListener<DashboardFragment>(this, "Dashboard", DashboardFragment.class));
		actionBar.addTab(tab);
		tab = actionBar.newTab().setText("Vehicles").setTabListener(new TabListener<VehiclesFragment>(this, "Vehicles", VehiclesFragment.class));
		actionBar.addTab(tab);
		tab = actionBar.newTab().setText("Options").setTabListener(new TabListener<OptionsFragment>(this, "Options", OptionsFragment.class));
		actionBar.addTab(tab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		getMenuInflater().inflate(R.menu.bbox, menu);
		return true;
	}
	
	public void onStartRecord(View v) {
		Intent record = new Intent(this, RecordingActivity.class);
		startActivityForResult(record, REQUEST_START_RECORD);
	}
	
	public void showVehicles(View v){
		Intent record = new Intent(this, VehicleActivity.class);
		startActivityForResult(record, REQUEST_SHOW_VEHICLES);
	}
	
	public void showOptions(View v){
		Intent record = new Intent(this, OptionsActivity.class);
		startActivityForResult(record, REQUEST_SHOW_OPTIONS);
	}
	
	
	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
	    private Fragment mFragment;
	    private final Activity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;

	    /** Constructor used each time a new tab is created.
	      * @param activity  The host Activity, used to instantiate the fragment
	      * @param tag  The identifier tag for the fragment
	      * @param clz  The fragment's Class, used to instantiate the fragment
	      */
	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }

	    /* The following are each of the ActionBar.TabListener callbacks */

	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // Check if the fragment is already initialized
	        if (mFragment == null) {
	            // If not, instantiate and add it to the activity
	            mFragment = Fragment.instantiate(mActivity, mClass.getName());
	            ft.add(android.R.id.content, mFragment, mTag);
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(mFragment);
	        }
	    }

	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            // Detach the fragment, because another one is being attached
	            ft.detach(mFragment);
	        }
	    }

	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }
	}
	
}
