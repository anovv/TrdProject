package com.btceclient.trdproject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.btceclient.trdproject.BaseActivity.GetBalance;
import com.btceclient.trdproject.GraphActivity.SetGraph;

import android.app.Activity;
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

public class BuyActivity extends BaseActivity {
	
	private static ProgressDialog pDialog;
	
	Map<String, Map<Double, Double>> orders = null;	
	ArrayList<BuyItem> buyItems;
	BuyAdapter buyAdapter;
	
	ListView asks;

	TextView buy;
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
		setContentView(R.layout.activity_buy);		
		
		asks = (ListView) findViewById(R.id.asks);		
		
		buy = (TextView) findViewById(R.id.buy_button);
		calculate = (TextView) findViewById(R.id.buy_calculate);
		amount = (EditText) findViewById(R.id.buy_amount);
		price = (EditText) findViewById(R.id.buy_price_et);
		fee = (TextView) findViewById(R.id.buy_fee);
		total = (TextView) findViewById(R.id.buy_total);
				
		amount_tv = (TextView) findViewById(R.id.buy_amount_textview);
		price_tv = (TextView) findViewById(R.id.buy_price_textview);
		header_cur1 = (TextView) findViewById(R.id.buy_header_cur1);
		header_cur2 = (TextView) findViewById(R.id.buy_header_cur2);		
		
		amount_tv.setText("Amount " + pair.split("_")[0].toUpperCase());
		price_tv.setText("Price per " + pair.split("_")[0].toUpperCase());
		header_cur1.setText(pair.split("_")[0].toUpperCase());
		header_cur2.setText(pair.split("_")[1].toUpperCase());
		buy.setText("Buy " + pair.split("_")[0].toUpperCase());
		
		asks.setLongClickable(true);
		
		asks.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
					BuyItem buyItem = (BuyItem) asks.getItemAtPosition(position);
					price.setText(buyItem.price);
					amount.setText(buyItem.price_cur1);
			}
		});
		
		asks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){ 
                 
			@Override             
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				BuyItem buyItem = (BuyItem) asks.getItemAtPosition(position);

				final double price = Double.parseDouble(buyItem.price);
				final double amount = Double.parseDouble(buyItem.price_cur1);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(BuyActivity.this);
				
				builder.setTitle("Are you sure?");
				builder.setMessage("Buy " + amount + " " + pair.split("_")[0].toUpperCase() + " for " + price + " " + pair.split("_")[1].toUpperCase() + " ?");
								
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {				          
					@Override
					public void onClick(DialogInterface dialog, int id) {
						if(!updating){
							new Buy(pair, price, amount).execute();
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
		
		buy.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				double am = Double.parseDouble(amount.getText().toString());
				double rt = Double.parseDouble(price.getText().toString());
				if(!updating){
					new Buy(pair, rt, am).execute();
				}
			}			
		});				
	}
	
	class BuyItem{
		String price;
		String price_cur1;
		String price_cur2;
		
		BuyItem(String price, String price_cur1, String price_cur2){
		
			this.price = new DecimalFormat("#0.000000").format(new BigDecimal(Double.parseDouble(price)).setScale(6, RoundingMode.UP).doubleValue()).replace(',', '.');
			this.price_cur1 = String.valueOf(new BigDecimal(Double.parseDouble(price_cur1)).setScale(6, RoundingMode.UP).doubleValue());
			this.price_cur2 = String.valueOf(new BigDecimal(Double.parseDouble(price_cur2)).setScale(6, RoundingMode.UP).doubleValue());
		}
	}
	
	public void fillData() {
		buyItems = new ArrayList<BuyItem>();
		if(orders != null){
			Map<Double, Double> asks = orders.get("asks");
			Iterator i = asks.entrySet().iterator();
			while(i.hasNext()){
	
				Entry<Double, Double> entry = (Entry<Double, Double>) i.next();
				
				double buy_price = entry.getKey();
				double buy_price_cur1 = entry.getValue();
				double buy_price_cur2 = buy_price*buy_price_cur1;
				
				BuyItem item = new BuyItem(buy_price + "", buy_price_cur1 + "", buy_price_cur2 + "");
				buyItems.add(item);
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
			buyAdapter = new BuyAdapter(BuyActivity.this, buyItems);
			
			asks.setAdapter(buyAdapter);
			if(!buyItems.isEmpty()){
				price.setText(buyItems.get(0).price);	
			}
		}
	}
	
	public void calculate(){
		String amount_string = this.amount.getText().toString();
		String price_string = this.price.getText().toString();
				
		if(Pattern.matches(Regexp.regexp, amount_string) && Pattern.matches(Regexp.regexp, price_string)){
			
			double amount = Double.parseDouble(amount_string);
			double price = Double.parseDouble(price_string);
			
			double fee = amount * ApiHandler.fee;
			double total = amount * price;			
			
			String fee_string = new DecimalFormat("#.#####").format(fee);
			String total_string = new DecimalFormat("#.#####").format(total);
			
			this.fee.setText(fee_string.replace(',', '.') + " " + pair.split("_")[0].toUpperCase());
			this.total.setText(total_string.replace(',', '.') + " " + pair.split("_")[1].toUpperCase());
			
		}else{
			Toast.makeText(getApplicationContext(), "Insert a value", Toast.LENGTH_SHORT).show();
		}
	}	
	
	class Buy extends AsyncTask<String, String, String>{

		JSONObject result = null;
		String pair;
		double rate;
		double amount;
		
		Buy(String pair, double rate, double amount){
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
			result = apiHandler.trade(pair, "buy", rate, amount);
			
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
				String message = "Created buy order " + amount + " " + pair.split("_")[0].toUpperCase() + " by price " + rate + " " + pair.split("_")[1].toUpperCase();
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
				buy.setText("Buy " + pair.split("_")[0].toUpperCase());			

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
