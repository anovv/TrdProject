package com.btceclient.trdproject;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.btceclient.trdproject.BuyActivity.BuyItem;


public class BuyAdapter extends BaseAdapter{
	
	Context context;
	LayoutInflater lInflater;
	ArrayList<BuyItem> objects;
	
	BuyAdapter(Context context, ArrayList<BuyItem> buy_items) {
	    this.context = context;
	    objects = buy_items;
	    lInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.get(position);		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	BuyItem getBuyItem(int position) {
	    return ((BuyItem) getItem(position));
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  
		View view = convertView;
	    if (view == null) {
	      view = lInflater.inflate(R.layout.buy_item, parent, false);
	    }

	    BuyItem b = getBuyItem(position);

	    ((TextView) view.findViewById(R.id.buy_price)).setText(b.price);
	    ((TextView) view.findViewById(R.id.buy_price_cur1)).setText(b.price_cur1);
	    ((TextView) view.findViewById(R.id.buy_price_cur2)).setText(b.price_cur2);
	    
	    return view;
	}
}
