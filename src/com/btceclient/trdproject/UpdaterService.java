package com.btceclient.trdproject;

import java.text.DecimalFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdaterService extends Service {
	
	final String LOG_TAG = "myLogs";
	public static Thread updaterThread;
	
	public static String pair = "btc_usd";
	public static int index = 0;
	public static long REFRESH_TIME = 10000;//default
	
	public static String[] pairs = {"btc_usd", "btc_rur", "btc_eur", "ltc_btc", "ltc_usd", "ltc_rur", "ltc_eur", "nmc_btc", "nmc_usd", "nvc_usd", "usd_rur", "eur_usd", "trc_btc", "ppc_btc", "ppc_usd", "ftc_btc", "xpm_btc"};
	public void onCreate() {
		super.onCreate();
	   	Log.d(LOG_TAG, "onCreate");
	}
	  
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "onStartCommand");
		update();
	    return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
	    Log.d(LOG_TAG, "onDestroy");
	}

	public IBinder onBind(Intent intent) {
	    Log.d(LOG_TAG, "onBind");
	    return null;
	}
	
	
	public void update() {
		
		SharedPreferences sharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(UpdaterService.this);
	
		REFRESH_TIME = Long.parseLong(sharedPrefs.getString("prefRefreshTime", "10000"));
		
		updaterThread = new Thread(new Runnable(){					
			
			@Override
			public void run() {
				while(Widget.isServiceRunning){					
					
					JSONObject result = getTicker(pair);

					String last_p = "";
					
					if(result != null){						
					
						DecimalFormat df = new DecimalFormat("#.#####");
						
						try {
							last_p = df.format(result.getJSONObject("ticker").getDouble("last")).replace(',', '.');
						}catch (Exception e) {}
						
					}else{
						last_p = "...";
					}
						
					RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_layout);
					view.setTextViewText(R.id.widget_tv, last_p);
					
					ComponentName thisWidget = new ComponentName(UpdaterService.this, Widget.class);
				    AppWidgetManager manager = AppWidgetManager.getInstance(UpdaterService.this);
				    
				    RemoteViews view_cur1 = new RemoteViews(getPackageName(), R.layout.widget_layout);
					view.setTextViewText(R.id.cur1_widget, pair.split("_")[0].toUpperCase());					
					
					RemoteViews view_cur2 = new RemoteViews(getPackageName(), R.layout.widget_layout);
					view.setTextViewText(R.id.cur2_widget, pair.split("_")[1].toUpperCase());
					
					manager.updateAppWidget(thisWidget, view);
					manager.updateAppWidget(thisWidget, view_cur1);					
					manager.updateAppWidget(thisWidget, view_cur2);
					
							
				    Log.d(LOG_TAG, last_p + " " + REFRESH_TIME);						 
					
					try {
						Thread.sleep(REFRESH_TIME);
					} catch (Exception e) {}
				}
			}
			
		});
		updaterThread.start();
	}
	
	public JSONObject getResponseFromPublicServerUrl(String url){
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (Exception e) {	
			return null;
		}
		
		String requestResult = null;
		try {
			requestResult = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {	
			return null;
		}					
		
		if( requestResult != null) {       
			JSONObject jsonResult = null;			
			try {				
				return new JSONObject(requestResult);					
			} catch (Exception e) {					
				return null;		
			}
		}			
		return null;	
	}
	
	public JSONObject getTicker(String pair){
		
		String url = "https://btc-e.com/api/2/" + pair + "/ticker";
		
		JSONObject jsonResult = getResponseFromPublicServerUrl(url);
	
		return jsonResult;
	}
	
	
}
