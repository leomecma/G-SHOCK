package com.mecma.g_shock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import java.util.Calendar;


/**
 * Implementation of App Widget functionality.
 */
public class gshockAppWidget extends AppWidgetProvider {
    private static int co=0;

    private static int numTelas=2;

    private static boolean adjustClicked=false;

    // Aramezanamento interno permanente
    private static boolean light=false;
    private static boolean sample=false;
    private static int TelaAtual = 0;
    private static int estadoBip = 0;
    private static int AlMonth=0;
    private static int Alday=0;
    private static int AlHour=12;
    private static int AlMin=0;
    private static boolean AlPM=false;
    private static boolean Al24H=false;

    public static int estadoAlarme=0;

    public static String BUTTON_LIGHT   = "android.appwidget.action.buttonLight";
    public static String BUTTON_REM     = "android.appwidget.action.buttonRem";
    public static String BUTTON_ADJUST  = "android.appwidget.action.buttonAdjust";
    public static String BUTTON_MODE    = "android.appwidget.action.buttonMode";

    public static String USER_PRESENT   = "android.intent.action.USER_PRESENT";


    ///////////////////////////////////////////////////////////////////////////////////////////

    private static boolean isPM=false;
    private static boolean is24H=false;

    private static int coBip=0;
    private static boolean startBip=false;

    private static Intent intentRem                    = null;
    private static Intent intentAdjust                 = null;
    private static Intent intentMode                   = null;
    private static Intent intentLight                  = null;
    private static PendingIntent pendingIntentRem      = null;
    private static PendingIntent pendingIntentAdjust   = null;
    private static PendingIntent pendingIntentMode     = null;
    private static PendingIntent pendingIntentLight    = null;

    private static int Hour;
    private static int Minute;
    private static int Second;
    private static int Day;
    private static int Month;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
       updateScreen(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //am.cancel(pi);
        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pi);
    }

    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
        super.onDisabled(context);
    }

    public static int convertDiptoPix(Context context, float dip) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return value;
    }

    public static Bitmap getFontBitmap(Context context, String text, int color, float fontSizeSP, int fontType) {
        float aux=0.75f;
        int fontSizePX = convertDiptoPix(context, fontSizeSP);
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/DS-DIGIB.TTF");
        if (fontType == 0)
            ;
        if (fontType == 1)
            typeface = Typeface.defaultFromStyle(0);
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setTextSize(fontSizePX);


        int textWidth = (int) (paint.measureText(text) + pad * 2);

        int height = (int) (fontSizePX / aux);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(text, xOriginal, fontSizePX, paint);

        return bitmap;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gshock_app_widget);
        ComponentName thiswidget = new ComponentName(context, gshockAppWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        SharedPreferences prefs = context.getSharedPreferences("persistent", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (BUTTON_LIGHT.equals(intent.getAction())) {

            final MediaPlayer mp = MediaPlayer.create(context, R.raw.beepgshock);
            mp.start();

            TelaAtual = prefs.getInt("TelaAtual", 0);
            light = prefs.getBoolean("light", false);

            if (TelaAtual==0) {

                if (light == true) {
                    views.setImageViewResource(R.id.imageView, R.drawable.gshock);

                    editor.putBoolean("light", false);
                    editor.commit();
                }
                else {
                    editor.putBoolean("light", true);
                    editor.commit();
                    views.setImageViewResource(R.id.imageView, R.drawable.gshock_light);
                }
            }
            else if (TelaAtual==1){ // Alarme

                estadoBip = prefs.getInt("estadoBip", 0);

                estadoBip++;
                if (estadoBip>3) estadoBip=0;

                editor.putInt("estadoBip", estadoBip);
                editor.commit();


                if (estadoBip==0) { // Ambos ligados
                    views.setViewVisibility(R.id.imageViewBip1, View.VISIBLE);
                    views.setViewVisibility(R.id.imageViewBip2, View.VISIBLE);
                }
                else if (estadoBip==1) { // Ambos desligados
                    views.setViewVisibility(R.id.imageViewBip1, View.INVISIBLE);
                    views.setViewVisibility(R.id.imageViewBip2, View.INVISIBLE);
                }
                else if (estadoBip==2) { // Apenas bip1 ligado
                    views.setViewVisibility(R.id.imageViewBip1, View.VISIBLE);
                    views.setViewVisibility(R.id.imageViewBip2, View.INVISIBLE);
                }
                else if (estadoBip==3) { // Apenas bip2 ligado
                    views.setViewVisibility(R.id.imageViewBip1, View.INVISIBLE);
                    views.setViewVisibility(R.id.imageViewBip2, View.VISIBLE);
                }
            }
        }

        else if (BUTTON_MODE.equals(intent.getAction())) {

            final MediaPlayer mp = MediaPlayer.create(context, R.raw.beepgshock);
            mp.start();

            TelaAtual = prefs.getInt("TelaAtual", 0);
            sample = prefs.getBoolean("sample", false);
            adjustClicked = prefs.getBoolean("adjustClicked", false);
            estadoAlarme = prefs.getInt("estadoAlarme", 0);


            if (!adjustClicked) {
                TelaAtual++;

                if (TelaAtual > numTelas - 1) TelaAtual = 0;

                editor.putInt("TelaAtual", TelaAtual);
                editor.commit();
            }
            else{
                if (TelaAtual==0) { // Tela de alarme
                    editor.putBoolean("adjustClicked", adjustClicked = false);
                    editor.commit();
                }


                else if (TelaAtual==1) { // Tela de alarme
                    estadoAlarme++;
                    if (estadoAlarme>3)
                        estadoAlarme = 0;

                    editor.putInt("estadoAlarme", estadoAlarme);
                    editor.commit();

                }
            }
        }

        else if (BUTTON_ADJUST.equals(intent.getAction())) {

            final MediaPlayer mp = MediaPlayer.create(context, R.raw.beepgshock);
            mp.start();

            TelaAtual = prefs.getInt("TelaAtual", 0);
            sample = prefs.getBoolean("sample", false);
            adjustClicked = prefs.getBoolean("adjustClicked", false);

            if(!adjustClicked){
                if (TelaAtual != 0) {
                    adjustClicked = true;
                }
                else{
                    adjustClicked=false;
                }
            }
            else{
                adjustClicked=false;
                estadoAlarme = 0;
            }

            editor.putBoolean("adjustClicked", adjustClicked);
            editor.commit();

        }
        else if (BUTTON_REM.equals(intent.getAction())) {
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.beepgshock);
            mp.start();


            TelaAtual = prefs.getInt("TelaAtual", 0);
            sample = prefs.getBoolean("sample", false);
            adjustClicked = prefs.getBoolean("adjustClicked", false);

            if (adjustClicked){
                if (TelaAtual == 1) { // Tela de alarme
                    AlMonth = prefs.getInt("AlMonth", 0);
                    Alday = prefs.getInt("Alday", 0);
                    AlHour = prefs.getInt("AlHour", 12);
                    AlMin = prefs.getInt("AlMin", 0);
                    AlPM = prefs.getBoolean("AlPM", false);
                    Al24H = prefs.getBoolean("Al24H", false);
                    estadoAlarme = prefs.getInt("estadoAlarme",0);

                    if (estadoAlarme == 0) {
                        AlHour++;
                        if (is24H) {
                            if (AlPM) AlHour += 12;
                            if (AlHour > 23) AlHour = 0;
                            Al24H = true;
                            AlPM = false;
                        } else {
                            if (Al24H) {
                                if (AlHour>12) {
                                    AlHour -= 12;
                                    AlPM = true;
                                }
                            }
                            Al24H = false;

                            if (AlHour > 12) {

                                AlHour = 1;
                            }
                            else if (AlHour==12) {
                                if (!AlPM) {
                                    AlPM = true;
                                    //AlHour = 1;
                                } else {
                                    AlPM = false;
                                    //AlHour = 0;
                                }
                            }
                        }


                            editor.putInt("AlHour", AlHour);
                            editor.putBoolean("AlPM", AlPM);
                            editor.putBoolean("Al24H", Al24H);

                    }
                    else if (estadoAlarme == 1) {
                        AlMin++;
                        if (AlMin > 59) AlMin = 0;

                        editor.putInt("AlMin", AlMin);
                        editor.commit();

                    }
                    else if (estadoAlarme == 2) {
                        AlMonth++;
                        if (AlMonth > 12) AlMonth = 0;

                        editor.putInt("AlMonth", AlMonth);
                        editor.commit();

                    }
                    else if (estadoAlarme == 3) {
                        Alday++;
                        if ((AlMonth % 2) == 0) {
                            if (AlMonth == 2) {
                                Calendar c = Calendar.getInstance();
                                int year = c.get(Calendar.YEAR);
                                if ((year % 4) == 0) {
                                    if (Alday > 29) Alday = 0;
                                } else if (Alday > 28) Alday = 0;
                            } else if (Alday > 30) Alday = 0;
                        } else if (Alday > 31) Alday = 0;

                        editor.putInt("Alday", Alday);
                        editor.commit();
                    }
                }
            }
            else{
                if (TelaAtual == 0){ // Nesse caso vai mudar para 5 segundos
                }

            }


        }
        else if (USER_PRESENT.equals(intent.getAction())){
            Log.i("LEO","User present");
        }

        manager.updateAppWidget(thiswidget, views);

        int[] appWidgetIds = manager.getAppWidgetIds(thiswidget);
        updateScreen(context,manager,appWidgetIds);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////


    static void updateScreen (Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        ComponentName thiswidget = new ComponentName(context, gshockAppWidget.class);
        //You can do the processing here update the widget/remote views.
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.gshock_app_widget);

        appWidgetIds = appWidgetManager.getAppWidgetIds(thiswidget);

        for (int widgetId : appWidgetIds) {

            // Le dados persistentes

            SharedPreferences prefs = context.getSharedPreferences("persistent", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            light = prefs.getBoolean("light", false);
            sample = prefs.getBoolean("sample", false);
            TelaAtual = prefs.getInt("TelaAtual", 0);
            estadoBip = prefs.getInt("estadoBip", 0);
            AlMonth = prefs.getInt("AlMonth", 0);
            Alday = prefs.getInt("Alday", 0);
            AlHour = prefs.getInt("AlHour", 12);
            AlMin = prefs.getInt("AlMin", 0);
            AlPM = prefs.getBoolean("AlPM", false);
            Al24H = prefs.getBoolean("Al24H", false);
            adjustClicked = prefs.getBoolean("adjustClicked", false);
            estadoAlarme = prefs.getInt("estadoAlarme", 0);


            // Le data e hora
            Calendar c = Calendar.getInstance();

            Hour = c.get(Calendar.HOUR_OF_DAY);

            Minute = c.get(Calendar.MINUTE);
            Second = c.get(Calendar.SECOND);
            Day = c.get(Calendar.DAY_OF_MONTH);
            Month = c.get(Calendar.MONTH) + 1;

            if (c.get(Calendar.AM_PM) == Calendar.PM) {
                isPM = true;
            } else {
                isPM = false;
            }

            if (DateFormat.is24HourFormat(context))
                is24H = true;
            else {
                is24H = false;
                if (Hour > 12) Hour -= 12;
                else if (Hour == 0) Hour = 12;
            }


            ////////////////////////////////////////////////////////////////////////////////////////////
            // Verifica se tem que bipar de hora em hora

            if ((estadoBip == 0) || (estadoBip == 3)) {
                if (Minute == 0) {
                    //int seconds = c.get(Calendar.SECOND);
                    if ((Second == 0)/*||(seconds==1)*/) {
                        final MediaPlayer mp = MediaPlayer.create(context, R.raw.beepgshock);
                        mp.start();
                    }
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////////

            ////////////////////////////////////////////////////////////////////////////////////////////
            // Verifica se tem alarme
            if ((estadoBip == 0) || (estadoBip == 2)) {
                int localAlHour;
                localAlHour = AlHour;

                if (Al24H) {
                    if (is24H) {

                    } else {
                        if (AlHour == 0) localAlHour = 12;
                        if (AlHour > 12) {
                            localAlHour = AlHour - 12;
                        }
                    }
                } else {
                    if (AlPM) {
                        if (is24H) {
                            localAlHour = AlHour + 12;
                            if (AlHour == 24) localAlHour = 0;
                        }
                    }

                }

                // Nesse caso só vai bpar nos dias preestabelecidos
                if ((Alday != 0) || (AlMonth != 0)) {
                    if (((Month == AlMonth) || (AlMonth == 0)) && ((Day == Alday) || (Alday == 0))) {
                        if (((Hour == localAlHour) && ((Al24H) ? true : (isPM && AlPM))) && (Minute == AlMin)) {
                            startBip = true;
                        }
                    }
                } else {
                    if (((Hour == localAlHour) && ((Al24H) ? true : (isPM && AlPM))) && (Minute == AlMin)) {
                        startBip = true;
                    }
                }

                if (startBip) {

                    if (coBip == 0) {

                    }
                    if (coBip < 20) {
                        final MediaPlayer mp = MediaPlayer.create(context, R.raw.beepgshock);
                        mp.start();
                    } else if (coBip > 100) {
                        startBip = false;
                    }
                    coBip++;
                } else {
                    coBip = 0;
                }
            }


            //remoteViews.setTextViewText(R.id.textView,"adj = " + adjustClicked + " pisca = " + pisca + "\n" + "coBip=" + coBip);


            // REM
            remoteViews.setImageViewBitmap(R.id.imageViewREM, gshockAppWidget.getFontBitmap(context, "REM", Color.BLACK, 4, 1));

            if (TelaAtual == 0) {
                // Relogio
                if (is24H) {
                    remoteViews.setViewVisibility(R.id.imageView24H, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.imageViewAMPM, View.INVISIBLE);
                    remoteViews.setImageViewBitmap(R.id.imageView24H, gshockAppWidget.getFontBitmap(context, "24H", Color.BLACK, 7, 1));
                } else {
                    remoteViews.setViewVisibility(R.id.imageViewAMPM, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.imageView24H, View.INVISIBLE);
                    if (isPM)
                        remoteViews.setImageViewBitmap(R.id.imageViewAMPM, gshockAppWidget.getFontBitmap(context, "PM", Color.BLACK, 6, 1));
                    else
                        remoteViews.setImageViewBitmap(R.id.imageViewAMPM, gshockAppWidget.getFontBitmap(context, "AM", Color.BLACK, 6, 1));
                }


                // Horas
                remoteViews.setImageViewBitmap(R.id.imageViewClockH1, gshockAppWidget.getFontBitmap(context, Hour / 10 == 0 ? "" : String.format("%d", Hour / 10), Color.BLACK, 28, 0));
                remoteViews.setImageViewBitmap(R.id.imageViewClockH2, gshockAppWidget.getFontBitmap(context, String.format("%d", Hour % 10), Color.BLACK, 28, 0));


                // Minutos

                // Dois pontos
                remoteViews.setImageViewBitmap(R.id.imageViewClock2P, gshockAppWidget.getFontBitmap(context, ":", Color.BLACK, 28, 0));

                remoteViews.setImageViewBitmap(R.id.imageViewClockM1, gshockAppWidget.getFontBitmap(context, String.format("%d", Minute / 10), Color.BLACK, 28, 0));
                remoteViews.setImageViewBitmap(R.id.imageViewClockM2, gshockAppWidget.getFontBitmap(context, String.format("%d", Minute % 10), Color.BLACK, 28, 0));


                // Segundos

                if (!sample) {
                    remoteViews.setImageViewBitmap(R.id.imageViewSec1, gshockAppWidget.getFontBitmap(context, String.format("%d", Second / 10), Color.BLACK, 20, 0));
                    remoteViews.setImageViewBitmap(R.id.imageViewSec2, gshockAppWidget.getFontBitmap(context, String.format("%d", Second % 10), Color.BLACK, 20, 0));
                } else {
                    remoteViews.setImageViewBitmap(R.id.imageViewSec1, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 20, 0));
                    remoteViews.setImageViewBitmap(R.id.imageViewSec2, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 20, 0));
                }


                // Data
                //Mes

                remoteViews.setImageViewBitmap(R.id.imageViewMes1, gshockAppWidget.getFontBitmap(context, Month / 10 == 0 ? "" : String.format("%d", Month / 10), Color.BLACK, 18, 0));
                remoteViews.setImageViewBitmap(R.id.imageViewMes2, gshockAppWidget.getFontBitmap(context, String.format("%d", Month % 10), Color.BLACK, 18, 0));


                //Dia

                //Traco
                remoteViews.setImageViewBitmap(R.id.imageViewTraco, gshockAppWidget.getFontBitmap(context, "-", Color.BLACK, 18, 0));


                remoteViews.setImageViewBitmap(R.id.imageViewDia1, gshockAppWidget.getFontBitmap(context, Day / 10 == 0 ? "" : String.format("%d", Day / 10), Color.BLACK, 18, 0));
                remoteViews.setImageViewBitmap(R.id.imageViewDia2, gshockAppWidget.getFontBitmap(context, String.format("%d", Day % 10), Color.BLACK, 18, 0));


                // Dia da semana
                remoteViews.setImageViewBitmap(R.id.imageViewWeekDay, gshockAppWidget.getFontBitmap(context, Utility.getDayOfWeek(), Color.BLACK, 18, 0));

            }


            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Constroe a tela de alarme
            else if (TelaAtual == 1) {

                int localHour = AlHour;

                // AL
                remoteViews.setImageViewBitmap(R.id.imageViewWeekDay, gshockAppWidget.getFontBitmap(context, "AL", Color.BLACK, 18, 0));

                // Campo de alarme
                if (is24H) {
                    remoteViews.setViewVisibility(R.id.imageView24H, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.imageViewAMPM, View.INVISIBLE);
                    remoteViews.setImageViewBitmap(R.id.imageView24H, gshockAppWidget.getFontBitmap(context, "24H", Color.BLACK, 7, 1));
                } else {
                    remoteViews.setViewVisibility(R.id.imageViewAMPM, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.imageView24H, View.INVISIBLE);

                    if ((AlPM) || (Al24H) && ((AlHour > 12) || AlHour == 0)) {
                        remoteViews.setImageViewBitmap(R.id.imageViewAMPM, gshockAppWidget.getFontBitmap(context, "PM", Color.BLACK, 6, 1));
                    } else {
                        remoteViews.setImageViewBitmap(R.id.imageViewAMPM, gshockAppWidget.getFontBitmap(context, "AM", Color.BLACK, 6, 1));
                    }
                }


                //Indicacao alarme
                if (!adjustClicked || ((Second % 2) != 0)) {

                    // O alarme foi salvo em formato 24 horas
                    if (Al24H) {
                        if (!is24H) {     // Mas a configuracao corrente não é a mesma
                            if (AlHour == 0) localHour = 12;
                            else {
                                if (AlHour > 12) {
                                    localHour = AlHour - 12;
                                }
                            }
                        }
                    } else {  // Foi salvo no formato AM/PM
                        if (is24H) { // Mas a config corrente é 24 horas
                            if (AlPM) { // Se foi na parte da tarde, soma 12
                                localHour = AlHour + 12;
                                if (localHour == 24) localHour = 0;
                            }
                        }
                    }

                    // Horas
                    remoteViews.setImageViewBitmap(R.id.imageViewClockH1, gshockAppWidget.getFontBitmap(context, localHour / 10 == 0 ? "" : String.format("%d", localHour / 10), Color.BLACK, 28, 0));
                    remoteViews.setImageViewBitmap(R.id.imageViewClockH2, gshockAppWidget.getFontBitmap(context, String.format("%d", localHour % 10), Color.BLACK, 28, 0));

                    // Dois pontos
                    remoteViews.setImageViewBitmap(R.id.imageViewClock2P, gshockAppWidget.getFontBitmap(context, ":", Color.BLACK, 28, 0));

                    // Minutos
                    remoteViews.setImageViewBitmap(R.id.imageViewClockM1, gshockAppWidget.getFontBitmap(context, String.format("%d", AlMin / 10), Color.BLACK, 28, 0));
                    remoteViews.setImageViewBitmap(R.id.imageViewClockM2, gshockAppWidget.getFontBitmap(context, String.format("%d", AlMin % 10), Color.BLACK, 28, 0));


                    // Data
                    //Mes
                    if (AlMonth == 0) {
                        remoteViews.setImageViewBitmap(R.id.imageViewMes1, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 18, 0));
                        remoteViews.setImageViewBitmap(R.id.imageViewMes2, gshockAppWidget.getFontBitmap(context, "-", Color.BLACK, 18, 0));
                    } else {
                        remoteViews.setImageViewBitmap(R.id.imageViewMes1, gshockAppWidget.getFontBitmap(context, AlMonth / 10 == 0 ? "" : String.format("%d", AlMonth / 10), Color.BLACK, 18, 0));
                        remoteViews.setImageViewBitmap(R.id.imageViewMes2, gshockAppWidget.getFontBitmap(context, String.format("%d", AlMonth % 10), Color.BLACK, 18, 0));
                    }
                    //Traco
                    remoteViews.setImageViewBitmap(R.id.imageViewTraco, gshockAppWidget.getFontBitmap(context, "-", Color.BLACK, 18, 0));
                    //Dia
                    if (Alday == 0) {
                        remoteViews.setImageViewBitmap(R.id.imageViewDia1, gshockAppWidget.getFontBitmap(context, "-", Color.BLACK, 18, 0));
                        remoteViews.setImageViewBitmap(R.id.imageViewDia2, gshockAppWidget.getFontBitmap(context, "-", Color.BLACK, 18, 0));
                    } else {
                        remoteViews.setImageViewBitmap(R.id.imageViewDia1, gshockAppWidget.getFontBitmap(context, Alday / 10 == 0 ? "" : String.format("%d", Alday / 10), Color.BLACK, 18, 0));
                        remoteViews.setImageViewBitmap(R.id.imageViewDia2, gshockAppWidget.getFontBitmap(context, String.format("%d", Alday % 10), Color.BLACK, 18, 0));
                    }

                } else {
                    if ((Second % 2) == 0) {
                        if (estadoAlarme == 0) {
                            remoteViews.setImageViewBitmap(R.id.imageViewClockH1, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 28, 0));
                            remoteViews.setImageViewBitmap(R.id.imageViewClockH2, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 28, 0));
                        } else if (estadoAlarme == 1) {
                            // Minutos
                            remoteViews.setImageViewBitmap(R.id.imageViewClockM1, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 28, 0));
                            remoteViews.setImageViewBitmap(R.id.imageViewClockM2, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 28, 0));
                        } else if (estadoAlarme == 2) {
                            remoteViews.setImageViewBitmap(R.id.imageViewMes1, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 18, 0));
                            remoteViews.setImageViewBitmap(R.id.imageViewMes2, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 18, 0));
                        } else if (estadoAlarme == 3) {
                            remoteViews.setImageViewBitmap(R.id.imageViewDia1, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 18, 0));
                            remoteViews.setImageViewBitmap(R.id.imageViewDia2, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 18, 0));

                        }
                    }
                }

                // Segundos
                remoteViews.setImageViewBitmap(R.id.imageViewSec1, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 20, 0));
                remoteViews.setImageViewBitmap(R.id.imageViewSec2, gshockAppWidget.getFontBitmap(context, "", Color.BLACK, 20, 0));

            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Constroe a tela de double time
        /*else if (TelaAtual==2) {

            // DT
            remoteViews.setImageViewBitmap(R.id.imageViewWeekDay, gshockAppWidget.getFontBitmap(context, "DT", Color.BLACK, 18, 0));


            // Relogio
            if (DateFormat.is24HourFormat(context)) {

                remoteViews.setViewVisibility(R.id.imageView24H, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.imageViewAMPM, View.INVISIBLE);

                if ((pisca%2)==0) {
                    remoteViews.setImageViewBitmap(R.id.imageViewDate, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("H:mm"), Color.BLACK, 18, 0));
                }
                else {
                    remoteViews.setImageViewBitmap(R.id.imageViewDate, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("H mm"), Color.BLACK, 18, 0));
                }

                remoteViews.setImageViewBitmap(R.id.imageViewClock, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("H:mm"), Color.BLACK, 28, 0));
                remoteViews.setImageViewBitmap(R.id.imageView24H, gshockAppWidget.getFontBitmap(context, "24H", Color.BLACK, 7, 1));
            } else {
                remoteViews.setViewVisibility(R.id.imageViewAMPM, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.imageView24H, View.INVISIBLE);

                if ((pisca%2)==0) {
                    remoteViews.setImageViewBitmap(R.id.imageViewDate, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("h:mm"), Color.BLACK, 18, 0));
                }
                else {
                    remoteViews.setImageViewBitmap(R.id.imageViewDate, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("h mm"), Color.BLACK, 18, 0));
                }
                remoteViews.setImageViewBitmap(R.id.imageViewClock, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("h:mm"), Color.BLACK, 28, 0));
                if (Utility.getCurrentTime("a").equals("PM"))
                    remoteViews.setImageViewBitmap(R.id.imageViewAMPM, gshockAppWidget.getFontBitmap(context, "PM", Color.BLACK, 6, 1));
                else
                    remoteViews.setImageViewBitmap(R.id.imageViewAMPM, gshockAppWidget.getFontBitmap(context, "AM", Color.BLACK, 6, 1));
            }

            remoteViews.setImageViewBitmap(R.id.imageViewSec, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("ss"), Color.BLACK, 20, 0));
        }
        else if(TelaAtual==3){
            // TA
            remoteViews.setImageViewBitmap(R.id.imageViewWeekDay, gshockAppWidget.getFontBitmap(context, "T'A", Color.BLACK, 18, 0));

            // Cronometro regressivo
            remoteViews.setImageViewBitmap(R.id.imageViewClock, gshockAppWidget.getFontBitmap(context,"0:00'", Color.BLACK, 28, 0));

            // Relogio
            if (DateFormat.is24HourFormat(context)) {

                remoteViews.setViewVisibility(R.id.imageView24H, View.INVISIBLE);
                remoteViews.setViewVisibility(R.id.imageViewAMPM, View.INVISIBLE);

                if ((pisca%2)==0) {
                    remoteViews.setImageViewBitmap(R.id.imageViewDate, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("H:mm"), Color.BLACK, 18, 0));
                }
                else {
                    remoteViews.setImageViewBitmap(R.id.imageViewDate, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("H mm"), Color.BLACK, 18, 0));
                }

            } else {
                remoteViews.setViewVisibility(R.id.imageViewAMPM, View.INVISIBLE);
                remoteViews.setViewVisibility(R.id.imageView24H, View.INVISIBLE);

                if ((pisca%2)==0) {
                    remoteViews.setImageViewBitmap(R.id.imageViewDate, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("h:mm"), Color.BLACK, 18, 0));
                }
                else {
                    remoteViews.setImageViewBitmap(R.id.imageViewDate, gshockAppWidget.getFontBitmap(context, Utility.getCurrentTime("h mm"), Color.BLACK, 18, 0));
                }
            }


            remoteViews.setImageViewBitmap(R.id.imageViewSec, gshockAppWidget.getFontBitmap(context, "00", Color.BLACK, 20, 0));
        }
        else if(TelaAtual==4){
            // ST
            remoteViews.setImageViewBitmap(R.id.imageViewWeekDay, gshockAppWidget.getFontBitmap(context, "ST", Color.BLACK, 18, 0));

            remoteViews.setViewVisibility(R.id.imageViewAMPM, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.imageView24H, View.INVISIBLE);

            // Cronometro progressivo
            remoteViews.setImageViewBitmap(R.id.imageViewClock, gshockAppWidget.getFontBitmap(context,"00'00", Color.BLACK, 28, 0));

            remoteViews.setImageViewBitmap(R.id.imageViewDate, gshockAppWidget.getFontBitmap(context, "  0H  ", Color.BLACK, 18, 0));

            remoteViews.setImageViewBitmap(R.id.imageViewSec, gshockAppWidget.getFontBitmap(context, "00", Color.BLACK, 20, 0));
        }*/



            /////////////////////////////////////////////////

            //Botao REM
            if (intentRem == null) intentRem = new Intent(gshockAppWidget.BUTTON_REM);
            if (pendingIntentRem == null)
                pendingIntentRem = PendingIntent.getBroadcast(context, 0, intentRem, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.buttonRem, pendingIntentRem);

            if (intentLight == null) intentLight = new Intent(gshockAppWidget.BUTTON_LIGHT);
            if (pendingIntentLight == null)
                pendingIntentLight = PendingIntent.getBroadcast(context, 0, intentLight, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.buttonLight, pendingIntentLight);

            //Botao ADJUST
            if (intentAdjust == null) intentAdjust = new Intent(gshockAppWidget.BUTTON_ADJUST);
            if (pendingIntentAdjust == null)
                pendingIntentAdjust = PendingIntent.getBroadcast(context, 0, intentAdjust, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.buttonAdjust, pendingIntentAdjust);

            //Botao MODE
            if (intentMode == null) intentMode = new Intent(gshockAppWidget.BUTTON_MODE);
            if (pendingIntentMode == null)
                pendingIntentMode = PendingIntent.getBroadcast(context, 0, intentMode, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.buttonMode, pendingIntentMode);



            ///////////////////////////////////////////////////////////////////////////////////////////
            //
            // Verifica se as imagens estao atualizadas
            if (light == false) {
                remoteViews.setImageViewResource(R.id.imageView, R.drawable.gshock);
            } else {
                remoteViews.setImageViewResource(R.id.imageView, R.drawable.gshock_light);
            }

            // Inicia imagem dos alarmes
            if (estadoBip == 0) { // Ambos ligados
                remoteViews.setViewVisibility(R.id.imageViewBip1, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.imageViewBip2, View.VISIBLE);
            } else if (estadoBip == 1) { // Ambos desligados
                remoteViews.setViewVisibility(R.id.imageViewBip1, View.INVISIBLE);
                remoteViews.setViewVisibility(R.id.imageViewBip2, View.INVISIBLE);
            } else if (estadoBip == 2) { // Apenas bip1 ligado
                remoteViews.setViewVisibility(R.id.imageViewBip1, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.imageViewBip2, View.INVISIBLE);
            } else if (estadoBip == 3) { // Apenas bip2 ligado
                remoteViews.setViewVisibility(R.id.imageViewBip1, View.INVISIBLE);
                remoteViews.setViewVisibility(R.id.imageViewBip2, View.VISIBLE);
            }

            appWidgetManager.updateAppWidget(thiswidget, remoteViews);

            Intent it = new Intent(context, Alarm.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, it, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pi);
        }
    }
}

