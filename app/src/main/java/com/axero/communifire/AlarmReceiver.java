package com.axero.communifire;

/**
 * Created by raghav on 15-01-2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Utils.isNetworkAvailable(context)) {
            new GetNotifications(context).execute();
            new GetMessages(context).execute();
        }
    }
}
