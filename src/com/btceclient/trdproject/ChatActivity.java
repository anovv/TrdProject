package com.btceclient.trdproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ChatActivity extends BaseActivity {

	public static String lang = "ru";//cn, en
	public static String URL = "https://btc-e.com/setlocale/" + lang;	
	
	ArrayList<Pair> comments;
	TextView chat;
	String info = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		chat = (TextView) findViewById(R.id.chat_tv);
		comments = new ArrayList<Pair>();
	}
	
	class Pair{
		String name;
		String comment;
		Pair(String name, String comment){
			this.name = name;
			this.comment = comment;
		}
		
		@Override 
		public String toString(){			
			return name + ": " + comment + "\n";			
		}
	}
	
	public void setCookie(String error){
		String cookie = "";
		Document doc_temp = null;
		try {
			doc_temp = Jsoup.connect(URL + "exchange/" + pair).timeout(10000).get();
			//doc_temp = Jsoup.connect(URL).timeout(10000).get();
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
	
	class SetChat extends AsyncTask<String, String, String>{
		
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
					
					Elements chatElements = doc.select("div[id=nChat]");
					Element c = chatElements.get(0);
					Elements names = c.getElementsByTag("a");
					Elements strings = c.getElementsByTag("span");
					
					String s = "";
					
					int l1 = names.size();
					int l2 = strings.size();
					int length = (l1 > l2) ? l2 : l1;
					for(int i = 0; i < length; i++){
						String name = names.get(i).text();
						String comment = strings.get(i).text();
						comments.add(new Pair(name, comment));						
					}
					
					for(Pair p : comments){
						s += p.toString() + "\n";
					}
					
					info = s;
					/*Elements scriptElements = doc.getElementsByTag("script");
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
					
					//webView.loadData(result, "text/html", null);	*/
				}catch(Exception e){
					error = "Can not load data " + e.toString();
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
				Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
			}else{
				chat.setText(info);
			}
		}		
	}
	
	@Override	
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {	    
		case R.id.refresh_button:	    
			new SetChat().execute();	
			new GetBalance().execute();
			return true;			
		default:	    			
			super.onOptionsItemSelected(item);
			if(item.getItemId() != mItem.getItemId() && item.getItemId() != android.R.id.home){
				new SetChat().execute();
			}
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		new SetChat().execute();
		return true;
	}
}
