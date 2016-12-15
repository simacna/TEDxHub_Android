package com.axero.communifire;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by raghav on 17-01-2015.
 */
public class AlarmHelper {
    public static void setAlarm(Context context)
    {
        GlobalApplication appInstance = GlobalApplication.getInstance();
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(Intent.ACTION_SEND);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int interval = appInstance.NotificationPingInterval;;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    public  static void CancelAlarm(Context context)
    {
        GlobalApplication appInstance = GlobalApplication.getInstance();
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(Intent.ACTION_SEND);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
    }
}
