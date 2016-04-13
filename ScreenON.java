package com.mecma.g_shock;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

            gshockAppWidget.oldMinute=-1;
            gshockAppWidget.oldMonth=-1;
            gshockAppWidget.oldDay=-1;
            gshockAppWidget.oldWeekDay="ER";
            gshockAppWidget.oldHour=-1;
            gshockAppWidget.oldSecond=-1;

            gshockAppWidget.oldFuncStr="ER";
            gshockAppWidget.oldChronoMinute=-1;
            gshockAppWidget.oldChronoHour=-1;
            gshockAppWidget.oldChronoSecond=-1;

            gshockAppWidget.oldAlHour=-1;
            gshockAppWidget.oldAlMinute=-1;
            gshockAppWidget.oldAlMonth=-1;
            gshockAppWidget.oldAlDay=-1;


            Intent it = new Intent(context, Alarm.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, it, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);

            //gshockAppWidget.clearApplicationData(context);
            //System.gc();

            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 500, 500, pi);
        }
    }
}
