package com.btceclient.trdproject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider{
	
	public static volatile boolean isServiceRunning = false;
	
	public static final String BUTTON_UP = "buttonup";
	public static final String BUTTON_DOWN = "buttondown";
	
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);			    
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		if(!isServiceRunning){
			isServiceRunning = true;
			context.startService(new Intent(context, UpdaterService.class));			
		}		
		
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        ComponentName widget = new ComponentName(context, Widget.class);

        remoteViews.setOnClickPendingIntent(R.id.widget_up, getPendingSelfIntent(context, BUTTON_UP));
        remoteViews.setOnClickPendingIntent(R.id.widget_down, getPendingSelfIntent(context, BUTTON_DOWN));
        appWidgetManager.updateAppWidget(widget, remoteViews);
	}
	
	@Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (BUTTON_DOWN.equals(intent.getAction())) {

            /*AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            ComponentName widget = new ComponentName(context, Widget.class);

            remoteViews.setTextViewText(R.id.cur1_widget, "LTC");
            remoteViews.setTextViewText(R.id.cur2_widget, "USD");

            appWidgetManager.updateAppWidget(widget, remoteViews);*/
        	if(UpdaterService.index == UpdaterService.pairs.length - 1){
        		UpdaterService.index = 0;
        		UpdaterService.pair = UpdaterService.pairs[UpdaterService.index];
        	}else{
        		UpdaterService.index++;
        		UpdaterService.pair = UpdaterService.pairs[UpdaterService.index];
        	}
        	if(UpdaterService.updaterThread != null){
    			UpdaterService.updaterThread.interrupt();
    		}
        }
        
        if (BUTTON_UP.equals(intent.getAction())) {

            /*AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            ComponentName widget = new ComponentName(context, Widget.class);

            remoteViews.setTextViewText(R.id.cur1_widget, "NMC");
            remoteViews.setTextViewText(R.id.cur2_widget, "USD");

            appWidgetManager.updateAppWidget(widget, remoteViews);*/
        	if(UpdaterService.index == 0){
        		UpdaterService.index = UpdaterService.pairs.length - 1;
        		UpdaterService.pair = UpdaterService.pairs[UpdaterService.index];
        	}else{
        		UpdaterService.index--;
        		UpdaterService.pair = UpdaterService.pairs[UpdaterService.index];
        	}
        	if(UpdaterService.updaterThread != null){
    			UpdaterService.updaterThread.interrupt();
    		}
        }
    }

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		UpdaterService.updaterThread.interrupt();//??
		isServiceRunning = false;
		try {
			UpdaterService.updaterThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.stopService(new Intent(context, UpdaterService.class));		
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	  
	}
	
	protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


}
