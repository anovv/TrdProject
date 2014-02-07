package com.btceclient.trdproject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class InfoActivity extends BaseActivity {	

	TextView rights;	
	TextView trans;
	TextView server_time;
	TextView open_orders;
	TextView funds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		rights = (TextView) findViewById(R.id.rights);
		trans = (TextView) findViewById(R.id.trans);
		server_time = (TextView) findViewById(R.id.server_time);
		open_orders = (TextView) findViewById(R.id.op_orders);
		funds = (TextView) findViewById(R.id.funds);
	}
	
	class GetInfo extends AsyncTask<String, String, String>{
		
		JSONObject result = null;
		
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

			result = apiHandler.getInfo();	
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url){
			
			updating = false;
			if(!refreshing){
				stopRefreshAnim();
			}
			if(result != null){
				fillInfo(result);
			}else{
				if(apiHandler.error != null){
					Toast.makeText(getApplicationContext(), apiHandler.error, Toast.LENGTH_LONG).show();
				}
			}
		}	
	}
	
	public void fillInfo(JSONObject jsonResult){

		String rights_info = "Api Key Rights: ";
		String trans_info = "Transaction Count: ";
		String server_time_info = "Server Time: ";
		String open_orders_info = "Open Orders: ";	
		String funds_info = "";
		
		JSONObject rights = null;
		JSONObject funds = null;
		try {
			rights = jsonResult.getJSONObject("rights");
			funds = jsonResult.getJSONObject("funds");
			trans_info = trans_info + jsonResult.getInt("transaction_count");
			open_orders_info = open_orders_info + jsonResult.getInt("open_orders");
			
			Date date = new Date(jsonResult.getLong("server_time") * 1000);	            
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm"); 
			
			server_time_info = server_time_info + sdf.format(date); 
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			return;
		}
		
		Iterator ri = rights.keys();
		while(ri.hasNext()){
			String key = (String) ri.next();
			int value;
			try {
				value =  rights.getInt(key);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				return;
			}
			if(value == 1){
				rights_info = rights_info + key + " "; 
			}
		}
		
		Iterator fi = funds.keys();
		while(fi.hasNext()){
			String key = (String) fi.next();
			double value;
			try {
				value = funds.getDouble(key);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				return;
			}
			funds_info = funds_info + key + ": " + value + "\n";
		}
		
		this.rights.setText(rights_info);
		this.trans.setText(trans_info);
		this.server_time.setText(server_time_info);
		this.open_orders.setText(open_orders_info);
		this.funds.setText(funds_info);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		new GetInfo().execute();
		return true;
	}
	
	@Override	
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {	    
		case R.id.refresh_button:	    
			new GetInfo().execute();	
			new GetBalance().execute();
			return true;			
		default:	    			
			super.onOptionsItemSelected(item);
			if(item.getItemId() != mItem.getItemId() && item.getItemId() != android.R.id.home){								
				new GetInfo().execute();
			}
			return true;
		}
	}
}
