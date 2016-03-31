package com.mecma.g_shock;

/**
 * Created by leonardo on 25/12/2015.
 */
import android.content.Context;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
    public static String getCurrentTime(String timeformat){
        Format formatter = new SimpleDateFormat(timeformat);
        return formatter.format(new Date());
    }

    public static String getDayOfWeek (){
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.MONDAY) return "MO";
        else if (day == Calendar.TUESDAY) return "TU";
        else if (day == Calendar.WEDNESDAY) return "WE";
        else if (day == Calendar.THURSDAY) return "TH";
        else if (day == Calendar.FRIDAY) return "F'A";
        else if (day == Calendar.SATURDAY) return "SA";
        else if (day == Calendar.SUNDAY) return "SU";
        return "ER";
    }

}
