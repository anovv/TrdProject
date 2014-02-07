package com.btceclient.trdproject;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.btceclient.trdproject.TradeHistoryActivity.TradeHistoryItem;

public class TradeHistoryAdapter extends BaseAdapter{
	Context context;
	LayoutInflater lInflater;
	ArrayList<TradeHistoryItem> objects;
	
	TradeHistoryAdapter(Context context, ArrayList<TradeHistoryItem> trade_history_items) {
	    this.context = context;
	    objects = trade_history_items;
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
	
	TradeHistoryItem getTradeHistoryItem(int position) {
	    return ((TradeHistoryItem) getItem(position));
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  
		View view = convertView;
	    if (view == null) {
	      view = lInflater.inflate(R.layout.trades_item, parent, false);
	    }

	    TradeHistoryItem b = getTradeHistoryItem(position);

	    ((TextView) view.findViewById(R.id.trades_type)).setText(b.type);
	    ((TextView) view.findViewById(R.id.trades_price)).setText(b.price);
	    ((TextView) view.findViewById(R.id.trades_cur1)).setText(b.cur1);
	    ((TextView) view.findViewById(R.id.trades_cur2)).setText(b.cur2);
	    
	    return view;
	}
}
