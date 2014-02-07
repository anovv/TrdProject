package com.btceclient.trdproject;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;


public class SettingsActivity extends SherlockPreferenceActivity {
	
	ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);//
		
		actionBar = getSupportActionBar();
	    
	    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.White)));    	
	    	    
	    actionBar.setIcon(R.drawable.ic_left);  
	    actionBar.setHomeButtonEnabled(true);	    
	    actionBar.setTitle("");		
	    
	    
		
		final Preference prefRefreshTime = findPreference("prefRefreshTime");
		
		prefRefreshTime.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	        @Override
	        public boolean onPreferenceChange(Preference preference, Object newVal) {
	            if(UpdaterService.updaterThread != null){
	            	
	            	UpdaterService.REFRESH_TIME = Long.parseLong((String) newVal);
	            	
	            	UpdaterService.updaterThread.interrupt();
	            }
	            return true;
	        }
	    });
	}
	
	@Override	
	public boolean onOptionsItemSelected(MenuItem item){	
		switch (item.getItemId()){				
		case android.R.id.home:
			finish();
			return true;
		}
		return true;
	}
	
	
}
