package com.mecma.g_shock;

/**
 * Created by leonardo on 25/12/2015.
 */

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class Alarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        int[] appWidgetIds = {0};
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        gshockAppWidget.updateScreen(context,manager,appWidgetIds);
    }
}
