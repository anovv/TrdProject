package com.btceclient.trdproject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SellActivity extends BaseActivity {
	
	private static ProgressDialog pDialog;
	
	Map<String, Map<Double, Double>> orders = null;	
	ArrayList<SellItem> sellItems;
	SellAdapter sellAdapter;
	
	ListView bids;
	
	TextView sell;
	TextView calculate;
	EditText amount;
	EditText price;
	TextView fee;
	TextView total;
	
	TextView amount_tv;
	TextView price_tv;
	TextView header_cur1;
	TextView header_cur2;	

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sell);
		
		bids = (ListView) findViewById(R.id.bids);		
		
		sell = (TextView) findViewById(R.id.sell_button);
		calculate = (TextView) findViewById(R.id.sell_calculate);
		amount = (EditText) findViewById(R.id.sell_amount);
		price = (EditText) findViewById(R.id.sell_price_et);
		fee = (TextView) findViewById(R.id.sell_fee);
		total = (TextView) findViewById(R.id.sell_total);
				
		amount_tv = (TextView) findViewById(R.id.sell_amount_textview);
		price_tv = (TextView) findViewById(R.id.sell_price_textview);
		header_cur1 = (TextView) findViewById(R.id.sell_header_cur1);
		header_cur2 = (TextView) findViewById(R.id.sell_header_cur2);		
		
		amount_tv.setText("Amount " + pair.split("_")[0].toUpperCase());
		price_tv.setText("Price per " + pair.split("_")[0].toUpperCase());
		header_cur1.setText(pair.split("_")[0].toUpperCase());
		header_cur2.setText(pair.split("_")[1].toUpperCase());
		sell.setText("Sell " + pair.split("_")[0].toUpperCase());
		
		bids.setLongClickable(true);

		bids.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
					SellItem sellItem = (SellItem) bids.getItemAtPosition(position);
					price.setText(sellItem.price);
					amount.setText(sellItem.price_cur1);
			}
		});
		
		bids.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){ 
                 
			@Override             
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				SellItem sellItem = (SellItem) bids.getItemAtPosition(position);

				final double price = Double.parseDouble(sellItem.price);
				final double amount = Double.parseDouble(sellItem.price_cur1);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(SellActivity.this);
				
				builder.setTitle("Are you sure?");
				builder.setMessage("Sell " + amount + " " + pair.split("_")[0].toUpperCase() + " for " + price + " " + pair.split("_")[1].toUpperCase() + " ?");
								
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {				          
					@Override
					public void onClick(DialogInterface dialog, int id) {
						if(!updating){
							new Sell(pair, price, amount).execute();
						}
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
				
				return true;      
			} 
		}); 
		
		calculate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				calculate();				
			}
		});		
		
		sell.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//buy
				double am = Double.parseDouble(amount.getText().toString());
				double rt = Double.parseDouble(price.getText().toString());
				if(!updating){
					new Sell(pair, rt, am).execute();
				}
			}			
		});
		
		
	}
	
	class SellItem{
		String price;
		String price_cur1;
		String price_cur2;
		
		SellItem(String price, String price_cur1, String price_cur2){
			
			this.price = new DecimalFormat("#0.000000").format(new BigDecimal(Double.parseDouble(price)).setScale(6, RoundingMode.UP).doubleValue()).replace(',', '.');
			this.price_cur1 = String.valueOf(new BigDecimal(Double.parseDouble(price_cur1)).setScale(6, RoundingMode.UP).doubleValue());
			this.price_cur2 = String.valueOf(new BigDecimal(Double.parseDouble(price_cur2)).setScale(6, RoundingMode.UP).doubleValue());
		}
	}
	
	public void fillData() {
		sellItems = new ArrayList<SellItem>();
		if(orders != null){
			Map<Double, Double> bids = orders.get("bids");
			Iterator i = bids.entrySet().iterator();
			while(i.hasNext()){
	
				Entry<Double, Double> entry = (Entry<Double, Double>) i.next();
				
				double sell_price = entry.getKey();
				double sell_price_cur1 = entry.getValue();
				double sell_price_cur2 = sell_price*sell_price_cur1;
				
				SellItem item = new SellItem(sell_price + "", sell_price_cur1 + "", sell_price_cur2 + "");
				sellItems.add(item);
			}		
		}
	}
	
	private class GetOrdersList extends AsyncTask<String, String, String>{
		
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

			orders = apiHandler.getOrdersForPair(pair);	
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url){
			updating = false;
			if(!refreshing){
				stopRefreshAnim();
			}
			fillData();
			sellAdapter = new SellAdapter(SellActivity.this, sellItems);
			
			bids.setAdapter(sellAdapter);
			if(!sellItems.isEmpty()){
				price.setText(sellItems.get(0).price);	
			}
		}			
	}
	
	public void calculate(){
		String amount_string = this.amount.getText().toString();
		String price_string = this.price.getText().toString();
				
		if(Pattern.matches(Regexp.regexp, amount_string) && Pattern.matches(Regexp.regexp, price_string)){
			
			double amount = Double.parseDouble(amount_string);
			double price = Double.parseDouble(price_string);
			double fee = amount * ApiHandler.fee * price;
			double total = amount * price;			
			
			String fee_string = new DecimalFormat("#.#####").format(fee);
			String total_string = new DecimalFormat("#.#####").format(total);
			
			this.fee.setText(fee_string.replace(',', '.') + " " + pair.split("_")[1].toUpperCase());
			this.total.setText(total_string.replace(',', '.') + " " + pair.split("_")[1].toUpperCase());
			
		}else{
			Toast.makeText(getApplicationContext(), "Insert a value", Toast.LENGTH_SHORT).show();
		}
	}
	
	class Sell extends AsyncTask<String, String, String>{

		JSONObject result = null;
		String pair;
		double rate;
		double amount;
		
		Sell(String pair, double rate, double amount){
			this.pair = pair;
			this.rate = rate;
			this.amount = amount;
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
			result = apiHandler.trade(pair, "sell", rate, amount);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url){
			updating = false;
			if(!refreshing){
				stopRefreshAnim();
			}
			if(result == null){
				if(apiHandler.error != null){
					Toast.makeText(getApplicationContext(), apiHandler.error, Toast.LENGTH_LONG).show();
				}
			}else{
				JSONObject funds = null;
				try {
					funds = result.getJSONObject("funds");
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					return;
				}
				double amount1;
				double amount2;
				try{
					amount1 = funds.getDouble(pair.split("_")[0]);
					amount2 = funds.getDouble(pair.split("_")[1]);
				}catch(Exception e){
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					return;
				}
				setBalanceInfo(amount1, amount2);				
				String message = "Created sell order " + amount + " " + pair.split("_")[0].toUpperCase() + " by price " + rate + " " + pair.split("_")[1].toUpperCase();
				Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();	
				new GetOrdersList().execute();
			}			
		}
	}
	
	@Override	
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {	    
		case R.id.refresh_button:	    
			new GetOrdersList().execute();	
			new GetBalance().execute();
			return true;			
		default:	    			
			super.onOptionsItemSelected(item);
			if(item.getItemId() != mItem.getItemId() && item.getItemId() != android.R.id.home){
				
				amount_tv.setText("Amount " + pair.split("_")[0].toUpperCase());
				price_tv.setText("Price per " + pair.split("_")[0].toUpperCase());
				header_cur1.setText(pair.split("_")[0].toUpperCase());
				header_cur2.setText(pair.split("_")[1].toUpperCase());
				sell.setText("Sell " + pair.split("_")[0].toUpperCase());			

				new GetOrdersList().execute();
			}
			return true;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		new GetOrdersList().execute();
		return true;
	}	
}
