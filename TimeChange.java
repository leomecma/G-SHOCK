package com.mecma.g_shock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by leonardo on 13/03/2016.
 */
public class TimeChange extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Intent it = new Intent(context, Alarm.class);
        //PendingIntent pi = PendingIntent.getBroadcast(context, 0, it, 0);
        //AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //alarmManager.cancel(pi);

        //After after 3 seconds
        //alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis()+1000, 1000, pi);
    }

}