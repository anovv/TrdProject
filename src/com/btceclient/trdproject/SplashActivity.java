package com.btceclient.trdproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashActivity extends Activity {
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
		TextView splash = (TextView) findViewById(R.id.splash);
		splash.setTypeface(tf);
		
		int secondsDelayed = 1;
	    new Handler().postDelayed(new Runnable() {
	        @Override
			public void run() {
	        	
	    		Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
				intent.putExtra("counter", false);
				startActivity(intent);
				finish();
	        }
	    }, secondsDelayed * 1000);
	}	
}
