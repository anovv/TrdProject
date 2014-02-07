package com.btceclient.trdproject;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.btceclient.trdproject.OpenOrdersActivity.OpenOrdersItem;

public class OpenOrdersAdapter extends BaseAdapter{
	Context context;
	LayoutInflater lInflater;
	ArrayList<OpenOrdersItem> objects;
	
	OpenOrdersAdapter(Context context, ArrayList<OpenOrdersItem> open_orders_items) {
	    this.context = context;
	    objects = open_orders_items;
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
	
	OpenOrdersItem getOpenOrdersItem(int position) {
	    return ((OpenOrdersItem) getItem(position));
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  
		View view = convertView;
	    if (view == null) {
	      view = lInflater.inflate(R.layout.open_orders_item, parent, false);
	    }

	    OpenOrdersItem b = getOpenOrdersItem(position);

	    ((TextView) view.findViewById(R.id.open_orders_type)).setText(b.type);
	    ((TextView) view.findViewById(R.id.open_orders_price)).setText(b.price);
	    ((TextView) view.findViewById(R.id.open_orders_cur1)).setText(b.cur1);
	    ((TextView) view.findViewById(R.id.open_orders_cur2)).setText(b.cur2);
	    
	    return view;
	}
}
