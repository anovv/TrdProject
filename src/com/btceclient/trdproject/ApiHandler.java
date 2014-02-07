package com.btceclient.trdproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;


public class ApiHandler{
	
	public static double fee = 0.002;
	public static volatile long _nonce;
	private static String DOMAIN = "btc-e.com";	
	
	
	public String _key = "E22LCZC7-PV06H59K-IXQXYFDT-V7KYZFYM-AOBB5T0U";
	public String _secret = "b749c228d55f250ad892fbe6f26a2cac8b3a64a2d68260011e333a50d66a00d5";
		
	public String cookie = "";
	public boolean isCookieSet = false;	

	public boolean alertCounter = false;	
	
	public String pair = "btc_usd";
	
	protected static ApiHandler instance = null;
	
	public String error = null;
	
	public SharedPreferences sPref;		
	
	
	public ApiHandler(SharedPreferences sPref){
		_nonce = System.currentTimeMillis()/1000;
		this.sPref = sPref;
	}
	
	public static ApiHandler getInstance(SharedPreferences sPref){
		if(instance == null){
			instance = new ApiHandler(sPref);
		}
		return instance;
	}
	
	public String getHash(String secret, String input){

		Mac mac;
        String _sign = "";
        try {
            byte[] bytesKey = secret.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(bytesKey, "HmacSHA512" );
            mac = Mac.getInstance( "HmacSHA512" );
            mac.init( secretKey );
            final byte[] macData = mac.doFinal(input.getBytes());
            byte[] hex = new Hex().encode(macData);
            _sign = new String( hex, "ISO-8859-1" );
        } catch(Exception e){
        	//System.out.println("Hashing exception" + e.toString());
        	//Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        	error = e.toString();
        	return null;
        }
        return _sign;       
	}
	
	public JSONObject getResponseFromServerForPost(String method, Map<String, String> arguments){	
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		if( arguments == null) {    
			arguments = new HashMap<String, String>();	        
		}	       
		arguments.put( "nonce",  "" + ++_nonce); 
		//_nonce = System.currentTimeMillis()/1000;
		//arguments.put( "nonce",  "" + _nonce);
		arguments.put( "method", method); 	    
		
		String postData = "";	 
	    
		for( Iterator argumentIterator = arguments.entrySet().iterator(); argumentIterator.hasNext(); ) {	    
			Map.Entry argument = (Map.Entry)argumentIterator.next();     
	        
			if( postData.length() > 0) {	        
				postData += "&";	            
			}	        
			postData += argument.getKey() + "=" + argument.getValue();	
			params.add(new BasicNameValuePair((String)argument.getKey(), (String)argument.getValue()));
		}
		
		String _sign = getHash(_secret, postData);
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://" + DOMAIN + "/tapi");
		
		httppost.addHeader("Key", _key);
		httppost.addHeader("Sign", _sign);
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params));

		} catch (Exception e) {	
			error = "No connection";
		}
		

		
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);

		} catch (Exception e) {	
			error = "No connection";
			return null;			
		}		
		
		String requestResult = null;
		try {
			requestResult = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {	
			error = e.toString();
			return null;
		}	
		
		if( requestResult != null) {      
			JSONObject jsonResult = null;			
			try {
				jsonResult = new JSONObject(requestResult);
				if (jsonResult.getInt("success") == 0){
					
					String errorMessage = jsonResult.getString("error");  					
					if(!errorMessage.contains("no orders")){
						if(errorMessage.contains("nonce")){
							error = null;
							_nonce = System.currentTimeMillis()/1000;
							return new JSONObject("{}");							
						}else{
							error = errorMessage;
						}
						return null;
					}
					return new JSONObject("{}");
					
				}else{
					error = null;				
					return new JSONObject(jsonResult.getString("return"));					
				}
			} catch (Exception e) {				
				error = e.toString();
				return null;		
			}
		}			
		return null;
	}
	
	public JSONObject getResponseFromPublicServerUrl(String url){
		if(error != null){
			if(error.equals("invalid api key")){
				return null;
			}
		}
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (Exception e) {	
			error = "No connection";
			return null;
		}
		
		String requestResult = null;
		try {
			requestResult = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {	
			error = "No connection";
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
	
	public JSONObject activeOrders(String pair){
		Map<String, String> params = new HashMap<String,String>();
		if(!pair.equals("")) {params.put("pair", pair);}		
		
		return getResponseFromServerForPost("ActiveOrders", params);
	}
	
	public JSONObject cancelOrder(String order_id){
		
		Map<String, String> params = new HashMap<String,String>();		
		
		params.put("order_id", order_id);
		
		return 	getResponseFromServerForPost("CancelOrder", params);				
	}
	
	public JSONObject TradeHistory(long from, long count, long from_id, long end_id, String order, long since, long end, String pair){
		
		Map<String, String> params = new HashMap<String,String>();
		
		if(from != -1){ params.put("from", String.valueOf(from));}
		if(count != -1){ params.put("count", String.valueOf(count));}
		if(from_id != -1){ params.put("from_id", String.valueOf(from_id));}
		if(end_id != -1){ params.put("end_id", String.valueOf(end_id));}
		if(!order.equals("")) {params.put("order", order);}
		if(since != -1){ params.put("since", String.valueOf(since));}
		if(end != -1){ params.put("end", String.valueOf(end));}
		if(!pair.equals("")) {params.put("pair", pair);}
		
		return getResponseFromServerForPost("TradeHistory", params);		
	}
	
	public JSONObject getInfo(){
		
		return getResponseFromServerForPost("getInfo", null);
	}


	public Map<String, Map<String, String>> getOpenOrdersForPair(String pair){		
		
		JSONObject jsonResult = activeOrders(pair);
		if(jsonResult == null)
			return null;
		
		Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();		
		Iterator keys = jsonResult.keys();
		while(keys.hasNext()){
			String key = (String) keys.next();
			JSONObject value = null;
			try {
				value = jsonResult.getJSONObject(key);
			} catch (Exception e) {
				//System.out.println(e.toString());				
				return null;
			}
			Map<String, String> map = new LinkedHashMap<String, String>();
			Iterator iterator = value.keys();
			while(iterator.hasNext()){
				String k = (String) iterator.next();
				String v = "";
				try {
					v = value.getString(k);
				} catch (Exception e) {					
					return null;
				}
				map.put(k, v);
			}
			result.put(key, map);
		}
		return result;
	}
	
	public Map<String, Map<Double, Double>> getOrdersForPair(String pair){
		
		int depth = 500;
		String url = "https://btc-e.com/api/2/" + pair + "/depth/" + String.valueOf(depth);
		
		JSONObject jsonResult = getResponseFromPublicServerUrl(url);
		JSONArray jsonAsks = null;
		JSONArray jsonBids = null;
				
		try {
			jsonAsks = jsonResult.getJSONArray("asks");
			jsonBids = jsonResult.getJSONArray("bids");		
			
		} catch (Exception e) {			
			return null;
		}
		
		Map<Double, Double> asks = new LinkedHashMap<Double, Double>();
		Map<Double, Double> bids = new LinkedHashMap<Double, Double>();
		
		for(int i = 0; i < jsonAsks.length(); i++){
			JSONArray values = null;
			try {
				values = jsonAsks.getJSONArray(i);
				asks.put(Double.valueOf(values.getString(0)), Double.valueOf(values.getString(1)));				
			} catch (Exception e) {				
				return null;
			}
		}
		
		for(int i = 0; i < jsonBids.length(); i++){
			JSONArray values = null;
			try {
				values = jsonBids.getJSONArray(i);
				bids.put(Double.valueOf(values.getString(0)), Double.valueOf(values.getString(1)));				
			} catch (Exception e) {				
				return null;
			}
		}	
		
		Map<String, Map<Double, Double>> result = new HashMap<String, Map<Double, Double>>();
		result.put("asks", asks);
		result.put("bids", bids);
		
		return result;
	}
	
	public JSONObject getTicker(String pair){
		
		String url = "https://btc-e.com/api/2/" + pair + "/ticker";
		
		JSONObject jsonResult = getResponseFromPublicServerUrl(url);
	
		return jsonResult;
	}
	
	public Map<String, Double> getBalance(){
		
		Map<String, Double> result = new HashMap<String, Double>();
		JSONObject jsonResult = null;
		try {
			jsonResult = getInfo().getJSONObject("funds");
		} catch (Exception e) {
			//System.out.println(e.toString());
			return null;
		}
		
		Iterator keys = jsonResult.keys();
		while(keys.hasNext()){
			String key = (String) keys.next();
			Double value = null;
			try {
				value = jsonResult.getDouble(key);
			} catch (Exception e) {				
				return null;
			}
			result.put(key, value);
		}		
		return result;
	}	
	
	
	public Map<String, Map<String, String>> getTradeHistory(String pair){
		
		int count = 2000;
		
		JSONObject jsonResult = TradeHistory(-1, count, -1, -1, "", -1, -1, pair);
		if(jsonResult == null)			
			return null;
		
		Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();		
		Iterator keys = jsonResult.keys();
		while(keys.hasNext()){
			String key = (String) keys.next();
			JSONObject value = null;
			try {
				value = jsonResult.getJSONObject(key);
			} catch (Exception e) {				
				return null;
			}
			Map<String, String> map = new LinkedHashMap<String, String>();
			Iterator iterator = value.keys();
			boolean isYourOrder = false;
			while(iterator.hasNext()){
				String k = (String) iterator.next();
				String v = "";
				try {
					v = value.getString(k);
				} catch (Exception e) {					
					return null;
				}
				if(k.equals("is_your_order") && v.equals("1")){
					isYourOrder = true;
				}
				map.put(k, v);
			}
			if(isYourOrder){
				result.put(key, map);
			}
		}
		return sortByTimestamp(result);	
	}
	
	public Map<String, Map<String, String>> sortByTimestamp(Map<String, Map<String, String>> map){
		
		Map<String, Map<String, String>> sorted = new LinkedHashMap<String, Map<String, String>>();
		while(!map.isEmpty()){
			Iterator i = map.keySet().iterator();
			String temp_timestamp = "0";
			String temp_key = "";
			while(i.hasNext()){
				String key = (String)i.next();
				Map<String, String> m = map.get(key);
				String timestamp = m.get("timestamp");
				if(Long.parseLong(timestamp) >= Long.parseLong(temp_timestamp)){
					temp_timestamp = timestamp;
					temp_key = key;
				}
			}
			Map<String, String> t = map.get(temp_key);
			map.remove(temp_key);
			sorted.put(temp_key, t);
		}
		
		return sorted;
		
	}

	
	public JSONObject trade(String pair, String type, double rate, double amount){
		
		Map<String, String> params = new HashMap<String,String>();		
		
		params.put("pair", pair); 
		params.put("type", type); 
		params.put("rate", String.valueOf(rate));
		params.put("amount", String.valueOf(amount));  		
		
		return getResponseFromServerForPost("Trade", params); 
	}
		
	private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++){
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }    
    
    public static String decrypt(String message, String key){
        SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(key), "AES");
        Cipher cipher = null;
        byte[] decrypted = null;
        try{
	        cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.DECRYPT_MODE, sks);
	        decrypted = cipher.doFinal(hexStringToByteArray(message));
        }catch(Exception e){        	
        }
        return new String(decrypted);
    }    
}
