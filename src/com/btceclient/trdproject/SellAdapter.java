package com.btceclient.trdproject;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.btceclient.trdproject.BuyActivity.BuyItem;
import com.btceclient.trdproject.SellActivity.SellItem;

public class SellAdapter extends BaseAdapter {

	Context context;
	LayoutInflater lInflater;
	ArrayList<SellItem> objects;
	
	SellAdapter(Context context, ArrayList<SellItem> sell_items) {
	    this.context = context;
	    objects = sell_items;
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
	
	SellItem getSellItem(int position) {
	    return ((SellItem) getItem(position));
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  
		View view = convertView;
	    if (view == null) {
	      view = lInflater.inflate(R.layout.sell_item, parent, false);
	    }

	    SellItem b = getSellItem(position);

	    ((TextView) view.findViewById(R.id.sell_price)).setText(b.price);
	    ((TextView) view.findViewById(R.id.sell_price_cur1)).setText(b.price_cur1);
	    ((TextView) view.findViewById(R.id.sell_price_cur2)).setText(b.price_cur2);
	    
	    return view;
	}
}
