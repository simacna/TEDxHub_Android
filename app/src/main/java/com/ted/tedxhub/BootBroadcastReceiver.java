package com.ted.tedxhub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by raghav on 15-01-2015.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private GlobalApplication appInstance;
    /**
     * Listens for Android's BOOT_COMPLETED broadcast and then executes
     * the onReceive() method.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Autostart", "BOOT_COMPLETED broadcast received. Executing starter service.");
        appInstance = GlobalApplication.getInstance();
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            AlarmHelper.setAlarm(context);
        }
    }

}