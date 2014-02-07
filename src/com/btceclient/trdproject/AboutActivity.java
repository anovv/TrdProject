package com.btceclient.trdproject;

import com.actionbarsherlock.view.MenuItem;
import com.btceclient.trdproject.BaseActivity.GetBalance;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}
	
	@Override	
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {	    
		case R.id.refresh_button:	    
			new GetBalance().execute();
			return true;			
		default:	    			
			super.onOptionsItemSelected(item);			
			return true;
		}
	}
}
