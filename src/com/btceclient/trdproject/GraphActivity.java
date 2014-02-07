package com.btceclient.trdproject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class GraphActivity extends BaseActivity{
	
	public final static String URL = "https://btc-e.com/";	
		
	private WebView webView;
	public void setCookie(String error){
		String cookie = "";
		Document doc_temp = null;
		try {
			doc_temp = Jsoup.connect(URL + "exchange/" + pair).timeout(10000).get();
			Elements scriptElements = doc_temp.getElementsByTag("script");
			Element element = scriptElements.get(0);				
			cookie = element.toString().split("\"")[1];
			apiHandler.cookie = cookie;
			apiHandler.isCookieSet = true;
		} catch (IOException e) {
			error = "No connection";
			return;
		}
	}
	
	class SetGraph extends AsyncTask<String, String, String>{
		
		String error = null;
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            updating = true;
			if(!refreshing){
				startRefreshAnim();
			}
        }		

		@Override
		protected String doInBackground(String... arg0) {
			
			if(!apiHandler.isCookieSet){
				setCookie(error);
			}
			if(apiHandler.isCookieSet){
				Document doc = null;
				try{
					
					URLConnection con = null;			
					
					URL url = new URL(URL + "exchange/" + pair);
					
					con = url.openConnection();
					con.setDoOutput(true);
					con.setRequestProperty("Cookie", apiHandler.cookie);
					con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
					con.connect();
					
					StringBuffer answer = new StringBuffer();
			        BufferedReader reader = null;
					
			        reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						answer.append(line);
					}
					reader.close();		
					
					doc = Jsoup.parse(answer.toString());
					
					Elements scriptElements = doc.getElementsByTag("script");
					Element element = scriptElements.get(5);
				
					DataNode node = element.dataNodes().get(0);
					String jsFunc = node.getWholeData() + "\n";
					
					String template1 = "<html>" + "\n" +
							"<head>" + "\n" +
							"<script type='text/javascript' src='https://www.google.com/jsapi'></script>" + "\n" +
							"<script type='text/javascript' src='https://btc-e.com/js/gc_leak_fix.js'></script>" + "\n" +
							"<script type='text/javascript'>" + "\n";
					
					
					String template2 = "</script>" + "\n" +
							"</head>" + "\n" +
							"<body>" + "\n" +
							"<div id=\"chart_div\"></div>" + "\n" +
							"</body>" + "\n" +
							"</html>";
					String result = template1  + jsFunc + template2;
					
					webView.loadData(result, "text/html", null);	
				}catch(Exception e){
					error = "Can not load data";
					return null;				
				}
			}

			return null;
		}
		
		 
		@Override
		protected void onPostExecute(String file_url) {
			updating = false;
			if(!refreshing){
				stopRefreshAnim();
			}			
			if(error != null){
				Toast.makeText(GraphActivity.this, error, Toast.LENGTH_LONG).show();
			}else{
				String name = pair.split("_")[0].toUpperCase() + "/" + pair.split("_")[1].toUpperCase();			
				Toast.makeText(GraphActivity.this, name, Toast.LENGTH_LONG).show();
			}
		}		
	}
	
	public void keyAlert(){
		String demoKey = "E22LCZC7";
		if(apiHandler.alertCounter == false && apiHandler._key.contains(demoKey)){
	
			AlertDialog.Builder builder = new AlertDialog.Builder(GraphActivity.this);
			
			builder.setTitle("Demo API Key");
			builder.setMessage("You are using demo API Key for this application. In order to be able to make operations you need to get API Key from your account on btc-e.com");
							
			builder.setPositiveButton("Change Key", new DialogInterface.OnClickListener() {				          
				@Override
				public void onClick(DialogInterface dialog, int id) {
					Intent intent = new Intent(getApplicationContext(), KeyActivity.class);	
					finish();
					startActivity(intent);
				}
			    
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			          
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			    
			});
	
			AlertDialog alert = builder.create();
	
			alert.show();
			apiHandler.alertCounter = true;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		

		setContentView(R.layout.activity_graph);
		
	    webView = (WebView) findViewById(R.id.webView1);
	    webView.getSettings().setJavaScriptEnabled(true);
	    webView.getSettings().setBuiltInZoomControls(true);	    
	    Bundle extras = getIntent().getExtras();		
		if (extras != null) {
		    boolean counter = extras.getBoolean("counter");
		    if(!counter){
			    apiHandler.alertCounter = counter;
		    	extras.putBoolean("counter", !counter);
		    }
		}
		keyAlert();
	}
		
	@Override	
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {	    
		case R.id.refresh_button:	    
			new SetGraph().execute();	
			new GetBalance().execute();
			return true;			
		default:	    			
			super.onOptionsItemSelected(item);
			if(item.getItemId() != mItem.getItemId() && item.getItemId() != android.R.id.home){
				new SetGraph().execute();
			}
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		new SetGraph().execute();
		return true;
	}

	
}