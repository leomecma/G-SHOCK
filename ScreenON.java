package com.mecma.g_shock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by leonardo on 10/04/2016.
 */
public class ScreenON extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        if(gshockAppWidget.SCREEN_ON.equals(intent.getAction())){
            Log.i("LEO","Screen ON");
            gshockAppWidget.clearApplicationData(context);
            System.gc();
        }
    }
}
