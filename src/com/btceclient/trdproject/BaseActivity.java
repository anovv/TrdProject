package com.btceclient.trdproject;

import java.text.DecimalFormat;
import java.util.Map;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class BaseActivity extends SherlockFragmentActivity{

	public SlidingMenu menu;			
	ApiHandler apiHandler;
	String pair;
	String amount1 = "0";
	String amount2 = "0";
	MenuItem mItem;
	MenuItem rItem;
	
	TextView last_price;
	TextView high_price;
	TextView low_price;
	View menu_refresh_view;
	
	volatile boolean refreshing = false;
	volatile boolean updating = false;
	
	ActionBar actionBar;	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);    	
		
		apiHandler = ApiHandler.getInstance(getPreferences(MODE_PRIVATE));	
		setDefaultKeys();
		loadKeys();
		pair = apiHandler.pair;
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		
		menu.setBehindWidth(width*4/5);
		menu.setMenu(R.layout.menu_frame);
		
		last_price = (TextView) findViewById(R.id.last_price);	
		high_price = (TextView) findViewById(R.id.high_price);	
		low_price = (TextView) findViewById(R.id.low_price);	
		menu_refresh_view = (View) findViewById(R.id.menu_refresh_view); 
		
		new GetTicker().execute();//TODO
		
		actionBar = getSupportActionBar();
	    
	    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.White)));    	
	    	    
	    actionBar.setIcon(R.drawable.ic_left);  
	    actionBar.setHomeButtonEnabled(true);	    
	    actionBar.setTitle("");
	    
	    final LinearLayout menu_refresh = (LinearLayout) findViewById(R.id.menu_refresh);	    
	    menu_refresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				new GetTicker().execute();	
			}
	    	
	    });
	    
	    final LinearLayout graphButton = (LinearLayout) findViewById(R.id.graphButton);
	    graphButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {				
				Intent intent = new Intent(getApplicationContext(), GraphActivity.class);	
				finish();
				startActivity(intent);				
			}    	
	    });   

	    
	    final LinearLayout buyButton = (LinearLayout) findViewById(R.id.buyButton);
	    buyButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {				
				Intent intent = new Intent(getApplicationContext(), BuyActivity.class);	
				finish();	
				startActivity(intent);				
			}    	
	    });   
	    
	    final LinearLayout sellButton = (LinearLayout) findViewById(R.id.sellButton);
	    sellButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(getApplicationContext(), SellActivity.class);
				finish();		
				startActivity(intent);				
			}    	
	    });  
	    
	    final LinearLayout infoButton = (LinearLayout) findViewById(R.id.infoButton);
	    infoButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
				finish();		
				startActivity(intent);				
			}    	
	    });   

	    
	    final LinearLayout ordersButton = (LinearLayout) findViewById(R.id.ordersButton);
	    ordersButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(getApplicationContext(), OpenOrdersActivity.class);
				finish();		
				startActivity(intent);				
			}    	
	    });	 
	    
	    final LinearLayout tradeHistoryButton = (LinearLayout) findViewById(R.id.tradeHistoryButton);
	    tradeHistoryButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(getApplicationContext(), TradeHistoryActivity.class);
				finish();		
				startActivity(intent);				
			}    	
	    });
	    
	    final LinearLayout keyButton = (LinearLayout) findViewById(R.id.keyButton);
	    keyButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(getApplicationContext(), KeyActivity.class);	
				finish();	
				startActivity(intent);				
			}    	
	    });	 
	    
	    /*final LinearLayout settingsButton = (LinearLayout) findViewById(R.id.settingsButton);
	    settingsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);	
					
				startActivity(intent);				
			}    	
	    });	 */
	
	    final LinearLayout aboutButton = (LinearLayout) findViewById(R.id.aboutButton);
	    aboutButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
				finish();		
				startActivity(intent);				
			}    	
	    });	 
	}	
	
	public void setDefaultKeys(){
		
		SharedPreferences pref = getSharedPreferences("keysinfo", MODE_PRIVATE);
		String def = pref.getString("default", "1");
		if(def.equals("1")){
			Editor ed = pref.edit();
		    ed.putString("key", apiHandler._key);
		    ed.putString("secret", apiHandler._secret);
		    ed.commit();
		}
	    
	}
	
	public void loadKeys(){
		apiHandler.sPref = getSharedPreferences("keysinfo", MODE_PRIVATE);
		apiHandler._key = apiHandler.sPref.getString("key", "");
		apiHandler._secret = apiHandler.sPref.getString("secret", "");
	}


	class GetBalance extends AsyncTask<String, String, String>{
		
		ProgressDialog pDialog;
		Map<String, Double> result = null;
		
		@Override
        protected void onPreExecute() {			
            super.onPreExecute();
            refreshing = true;
            if(!updating){
            	startRefreshAnim();
            }
        }	
		
		@Override
		protected String doInBackground(String... params) {
			result = apiHandler.getBalance();
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url){
			refreshing = false;
			if(!updating){
				stopRefreshAnim();
			}
			if(result == null){
				if(apiHandler.error != null){
					Toast.makeText(getApplicationContext(), apiHandler.error, Toast.LENGTH_LONG).show();
				}
			}else{
				setBalanceInfo(result.get(pair.split("_")[0]), result.get(pair.split("_")[1]));				
			}			
		}
	}
	
	class GetTicker extends AsyncTask<String, String, String>{
		
		JSONObject result = null;
				
		@Override
        protected void onPreExecute() {			
            super.onPreExecute();
            startMenuRefreshAnim();
        }	
		
		@Override
		protected String doInBackground(String... params) {
			
			result = apiHandler.getTicker(pair);
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url){
			stopMenuRefreshAnim();
			if(result == null){
				if(apiHandler.error != null){
					Toast.makeText(getApplicationContext(), apiHandler.error, Toast.LENGTH_LONG).show();
				}
			}else{
				DecimalFormat df = new DecimalFormat("#.#####");
				
				String last_p = null;
				String high_p = null;
				String low_p = null;
				try {
					last_p = df.format(result.getJSONObject("ticker").getDouble("last")).replace(',', '.');
					high_p = df.format(result.getJSONObject("ticker").getDouble("high")).replace(',', '.');
					low_p = df.format(result.getJSONObject("ticker").getDouble("low")).replace(',', '.');
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();				
					
				}
				last_price.setText(last_p);
				high_price.setText(high_p);
				low_price.setText(low_p);
			}			
		}	
	}
	
	public void setBalanceInfo(double amount1, double amount2){
		this.amount1 = String.valueOf(amount1);
		this.amount2 = String.valueOf(amount2);
		DecimalFormat df = new DecimalFormat("#.###");
		String info = pair.split("_")[0].toUpperCase() + ": " + df.format(amount1).replace(',', '.') + " " + pair.split("_")[1].toUpperCase() + ": " + df.format(amount2).replace(',', '.');
		mItem.setTitle(info);		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.base, menu);		
		rItem = menu.findItem(R.id.refresh_button);
		mItem = menu.findItem(R.id.info_item);
		String info = pair.split("_")[0].toUpperCase() + ": " + amount1 + " " + pair.split("_")[1].toUpperCase() + ": " + amount2;
		mItem.setTitle(info);
		
		new GetBalance().execute();
		return true;
	}
	
	@Override	
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {				
		case android.R.id.home:	    
			menu.showMenu();	     
			return true;		
		case R.id.menu_btc_usd:
			pair = "btc_usd";
			apiHandler.pair = "btc_usd"; 
			new GetBalance().execute();
			new GetTicker().execute();
			return true;			
		case R.id.menu_btc_eur:
			pair = "btc_eur";
			apiHandler.pair = "btc_eur";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_btc_rur:
			pair = "btc_rur";
			apiHandler.pair = "btc_rur";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_ltc_btc:
			pair = "ltc_btc";
			apiHandler.pair = "ltc_btc";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_ltc_usd:
			pair = "ltc_usd";
			apiHandler.pair = "ltc_usd";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_ltc_rur:
			pair = "ltc_rur";
			apiHandler.pair = "ltc_rur";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_ltc_eur:
			pair = "ltc_eur";
			apiHandler.pair = "ltc_eur";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_nmc_btc:
			pair = "nmc_btc";
			apiHandler.pair = "nmc_btc";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_nmc_usd:
			pair = "nmc_usd";
			apiHandler.pair = "nmc_usd";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_nvc_btc:
			pair = "nvc_btc";
			apiHandler.pair = "nvc_btc";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_nvc_usd:
			pair = "nvc_usd";
			apiHandler.pair = "nvc_usd";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_usd_rur:
			pair = "usd_rur";
			apiHandler.pair = "usd_rur";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_eur_usd:
			pair = "eur_usd";
			apiHandler.pair = "eur_usd";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;		
		case R.id.menu_trc_btc:
			pair = "trc_btc";
			apiHandler.pair = "trc_btc";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;			
		case R.id.menu_ppc_btc:
			pair = "ppc_btc";
			apiHandler.pair = "ppc_btc";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;		
		case R.id.menu_ppc_usd:
			pair = "ppc_usd";
			apiHandler.pair = "ppc_usd";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;							
		case R.id.menu_ftc_btc:
			pair = "ftc_btc";
			apiHandler.pair = "ftc_btc";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;						
		case R.id.menu_xpm_btc:
			pair = "xpm_btc";
			apiHandler.pair = "xpm_btc";
			new GetBalance().execute();
			new GetTicker().execute();
			return true;
		}
		return true;
	}
	
	public void startRefreshAnim() {
	    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);

	    Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
	    rotation.setRepeatCount(Animation.INFINITE);
	    iv.startAnimation(rotation);
	    rItem.setActionView(iv);
	}
	
	public void stopRefreshAnim() {
		if(rItem != null){	
			if(rItem.getActionView() != null){
				rItem.getActionView().clearAnimation();
			}
		    rItem.setActionView(null);
		}
	}

	public void startMenuRefreshAnim() {
	    
	    Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
	    rotation.setRepeatCount(Animation.INFINITE);
	    menu_refresh_view.startAnimation(rotation);
	}
	
	public void stopMenuRefreshAnim(){
		menu_refresh_view.clearAnimation();
	}
	
	@Override
	public void onBackPressed(){
		if(menu.isMenuShowing()){   
			menu.toggle(true);  	
		}else{
			super.onBackPressed();
		}
	}
	
	@Override
    public void onDestroy()
    {
        super.onDestroy();
        //stopService(new Intent(this, UpdaterService.class));
    }
}
