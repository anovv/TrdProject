package com.btceclient.trdproject;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;

public class KeyActivity extends BaseActivity {

	TextView key;
	TextView secret;
	EditText new_key;
	EditText new_secret;
	Button submit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key);
		
		key = (TextView) findViewById(R.id.key);
		secret = (TextView) findViewById(R.id.secret);
		new_key = (EditText) findViewById(R.id.new_key);
		new_secret = (EditText) findViewById(R.id.new_secret);
		submit = (Button) findViewById(R.id.submit);
		
		key.setText(apiHandler._key);
		secret.setText(apiHandler._secret);
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				submitChanges();
			}			
		});
	}
		
	public void submitChanges(){
		
		String key = new_key.getText().toString();
		String secret = new_secret.getText().toString();
		if(!key.equals("") && !secret.equals("")){
			apiHandler.sPref = getSharedPreferences("keysinfo", MODE_PRIVATE);
		    Editor ed = apiHandler.sPref.edit();
		    ed.putString("key", key);
		    ed.putString("secret", secret);
		    ed.putString("default", "0");//TODO always 0
		    ed.commit();
		    apiHandler._key = key;
		    apiHandler._secret = secret;	    
		    this.key.setText(key);
		    this.secret.setText(secret);
			Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
		}
	}

	@Override	
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {	    
		case R.id.refresh_button:	    
			new GetBalance().execute();
			return true;			
		default:	    			
			super.onOptionsItemSelected(item);
			
			return true;
		}
	}
}
