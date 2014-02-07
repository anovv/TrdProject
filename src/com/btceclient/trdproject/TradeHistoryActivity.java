package com.btceclient.trdproject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TradeHistoryActivity extends BaseActivity {
	

	Map<String, Map<String, String>> history = null;
	ArrayList<TradeHistoryItem> tradeHistoryItems;
	TradeHistoryAdapter tradeHistoryAdapter;
	
	TextView header_type;
	TextView header_price;	
	TextView header_cur1;
	TextView header_cur2;
	
	TextView emptylist;
	
	ListView tradeHistory;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trade_history);	
		
		tradeHistory = (ListView) findViewById(R.id.trades);		
		emptylist = (TextView )findViewById(R.id.emptyTradesList);
		emptylist.setVisibility(View.GONE);
		
		header_type = (TextView) findViewById(R.id.trades_type);
		header_price = (TextView) findViewById(R.id.trades_price);		
		header_cur1 = (TextView) findViewById(R.id.trades_header_cur1);
		header_cur2 = (TextView) findViewById(R.id.trades_header_cur2);	
		
		header_cur1.setText(pair.split("_")[0].toUpperCase());
		header_cur2.setText(pair.split("_")[1].toUpperCase());
		
		tradeHistory.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
				TradeHistoryItem item = (TradeHistoryItem) tradeHistory.getItemAtPosition(position);

				AlertDialog alertDialog = new AlertDialog.Builder(TradeHistoryActivity.this).create();

	            alertDialog.setTitle("Trade Information");
	            String info = "Id: " + item.id + "\n" + 
        						"Type: " + item.type + "\n" +
	            				"Price: " + item.price + " " + pair.split("_")[1].toUpperCase() + "\n" +	            				
	            				"Amount: " + item.cur1 + " " + pair.split("_")[0].toUpperCase() + "\n" +
	            				"Total: " + item.cur2 + " " + pair.split("_")[1].toUpperCase() + "\n";
	            Date date = new Date(Long.parseLong(item.date));	            
	            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");	            
	            String time = "Date: " + sdf.format(date);	            
	            
	            alertDialog.setMessage(info + time);	            
	            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	                @Override
	            	public void onClick(DialogInterface dialog,int which){	            		
	            	}	                    	
	            });
	            alertDialog.show();	
			}
		});
	}
	
	public void fillData() {
		tradeHistoryItems = new ArrayList<TradeHistoryItem>();
		if(history != null){
			Iterator i = history.entrySet().iterator();
			while(i.hasNext()){
				Entry<String, Map<String, String>> entry = (Entry<String, Map<String, String>>) i.next();			
				
				String id = entry.getKey();
				Map<String, String> trade = entry.getValue();
				
				DecimalFormat df = new DecimalFormat("#.######");
			    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			    dfs.setDecimalSeparator('.');
			    df.setDecimalFormatSymbols(dfs);
			    
				String type = trade.get("type");
				String price = trade.get("rate");
				String cur1 = df.format(Double.parseDouble(trade.get("amount")));
				String cur2 = df.format(Double.parseDouble(price) * Double.parseDouble(cur1));
				String date = String.valueOf(Long.parseLong(trade.get("timestamp"))*1000);
				
				TradeHistoryItem item = new TradeHistoryItem(id, type, price, cur1, cur2, date);
				tradeHistoryItems.add(item);			
			}		
		}
	}
	
	class TradeHistoryItem{	

		String id;
		String type;
		String price;
		String cur1;
		String cur2;
		String date;
		
		TradeHistoryItem(String id, String type, String price, String cur1, String cur2, String date){
			this.id = id;
			this.type = type;
			this.price = price;
			this.cur1 = cur1;
			this.cur2 = cur2;
			this.date = date;
		}		
	}	

	class GetTradeHistory extends AsyncTask<String, String, String>{
		String pair;
		
		GetTradeHistory(String pair){
			this.pair = pair;
		}
		
		@Override
        protected void onPreExecute() {			
            super.onPreExecute();
            updating = true;
			if(!refreshing){
				startRefreshAnim();
			}
        }	
		
		@Override
		protected String doInBackground(String... params) {

			history = apiHandler.getTradeHistory(pair);	
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url){
			updating = false;
			if(!refreshing){
				stopRefreshAnim();
			}	
			
			fillData();
			tradeHistoryAdapter = new TradeHistoryAdapter(TradeHistoryActivity.this, tradeHistoryItems);			
			tradeHistory.setAdapter(tradeHistoryAdapter);			
			
			if(tradeHistoryItems.isEmpty()){
				emptylist.setVisibility(View.VISIBLE);
			}else{
				emptylist.setVisibility(View.GONE);
			}
		}
	}
	
	@Override	
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {	    
		case R.id.refresh_button:	    
			new GetTradeHistory(pair).execute();	
			new GetBalance().execute();
			return true;			
		default:	    			
			super.onOptionsItemSelected(item);
			if(item.getItemId() != mItem.getItemId() && item.getItemId() != android.R.id.home){
				
				header_cur1.setText(pair.split("_")[0].toUpperCase());
				header_cur2.setText(pair.split("_")[1].toUpperCase());
				
				new GetTradeHistory(pair).execute();
			}
			return true;
		}
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		new GetTradeHistory(pair).execute();
		return true;
	}
}
