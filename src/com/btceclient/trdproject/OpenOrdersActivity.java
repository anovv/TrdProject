package com.btceclient.trdproject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.btceclient.trdproject.BaseActivity.GetBalance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OpenOrdersActivity extends BaseActivity {
	
	private static ProgressDialog pDialog;
	
	Map<String, Map<String, String>> orders = null;	
	ArrayList<OpenOrdersItem> openOrdersItems;
	OpenOrdersAdapter openOrdersAdapter;
	
	ListView openOrders;
	
	TextView header_type;
	TextView header_price;	
	TextView header_cur1;
	TextView header_cur2;
	
	TextView emptylist;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_orders);
		
		
		openOrders = (ListView) findViewById(R.id.open_orders);		
		emptylist = (TextView )findViewById(R.id.emptyList);
		emptylist.setVisibility(View.GONE);
		
		header_type = (TextView) findViewById(R.id.open_orders_type);
		header_price = (TextView) findViewById(R.id.open_orders_price);		
		header_cur1 = (TextView) findViewById(R.id.open_orders_header_cur1);
		header_cur2 = (TextView) findViewById(R.id.open_orders_header_cur2);	
		
		header_cur1.setText(pair.split("_")[0].toUpperCase());
		header_cur2.setText(pair.split("_")[1].toUpperCase());
		
		openOrders.setLongClickable(true);
		
		openOrders.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
				
				OpenOrdersItem openOrdersItem = (OpenOrdersItem) openOrders.getItemAtPosition(position);

				final String iden = openOrdersItem.id;
				
				AlertDialog.Builder builder = new AlertDialog.Builder(OpenOrdersActivity.this);
				
				builder.setTitle("Are you sure?");
				builder.setMessage("Cancel this order ?");
				
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {				          
					@Override
					public void onClick(DialogInterface dialog, int id) {
						if(!updating){
							new CancelOrder(iden).execute();
						}
					}
				    
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				          
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				    
				});

				AlertDialog alert = builder.create();

				alert.show();

			}
		});
		
		openOrders.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){ 
                 
			@SuppressWarnings("deprecation")
			@Override             
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				OpenOrdersItem item = (OpenOrdersItem) openOrders.getItemAtPosition(position);

				AlertDialog alertDialog = new AlertDialog.Builder(OpenOrdersActivity.this).create();

	            alertDialog.setTitle("Order Information");
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
				return true;      
			} 
		}); 
		
	}
	
	class OpenOrdersItem{	

		String id;
		String type;
		String price;
		String cur1;
		String cur2;
		String date;
		
		OpenOrdersItem(String id, String type, String price, String cur1, String cur2, String date){
			this.id = id;
			this.type = type;
			this.price = price;
			this.cur1 = cur1;
			this.cur2 = cur2;
			this.date = date;
		}		
	}
	
	public void fillData() {
		openOrdersItems = new ArrayList<OpenOrdersItem>();
		if(orders != null){
			Iterator i = orders.entrySet().iterator();
			while(i.hasNext()){
				Entry<String, Map<String, String>> entry = (Entry<String, Map<String, String>>) i.next();			
				
				String id = entry.getKey();
				Map<String, String> order = entry.getValue();
				
				String type = order.get("type");
				String price = order.get("rate");
				String cur1 = order.get("amount");
				String cur2 = String.valueOf(Double.parseDouble(price) * Double.parseDouble(cur1));
				String date = String.valueOf(Long.parseLong(order.get("timestamp_created"))*1000);
				
				OpenOrdersItem item = new OpenOrdersItem(id, type, price, cur1, cur2, date);
				openOrdersItems.add(item);			
			}	
		}
	}
	
	private class GetOpenOrdersList extends AsyncTask<String, String, String>{
		
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

			orders = apiHandler.getOpenOrdersForPair(pair);	
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url){
			updating = false;
			if(!refreshing){
				stopRefreshAnim();
			}
			
			fillData();
			openOrdersAdapter = new OpenOrdersAdapter(OpenOrdersActivity.this, openOrdersItems);			
			openOrders.setAdapter(openOrdersAdapter);			
			
			if(openOrdersItems.isEmpty()){
				emptylist.setVisibility(View.VISIBLE);
			}else{
				emptylist.setVisibility(View.GONE);
			}
		}		
	}
	
	class CancelOrder extends AsyncTask<String, String, String>{

		JSONObject result = null;
		String id;
		
		CancelOrder(String id){
			this.id = id;
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
			result = apiHandler.cancelOrder(id);
			
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
				
				for(int i = 0; i < openOrdersItems.size(); i++){
					if(openOrdersItems.get(i).id.equals(id)){
						openOrdersItems.remove(i);
						break;
					}
				}			
				openOrdersAdapter.notifyDataSetChanged();
				openOrdersAdapter.notifyDataSetInvalidated();				

				String message = "Order is canceled";
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				
				if(openOrdersItems.isEmpty()){
					emptylist.setVisibility(View.VISIBLE);
				}else{
					emptylist.setVisibility(View.GONE);
				}
			}			
		}
	}
	
	@Override	
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {	    
		case R.id.refresh_button:	    
			new GetOpenOrdersList().execute();	
			new GetBalance().execute();
			return true;			
		default:	    			
			super.onOptionsItemSelected(item);
			if(item.getItemId() != mItem.getItemId() && item.getItemId() != android.R.id.home){
				
				header_cur1.setText(pair.split("_")[0].toUpperCase());
				header_cur2.setText(pair.split("_")[1].toUpperCase());
				
				new GetOpenOrdersList().execute();
			}
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		new GetOpenOrdersList().execute();
		return true;
	}
}
