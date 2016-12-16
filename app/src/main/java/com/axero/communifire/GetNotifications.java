package com.axero.communifire;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by raghav on 29-01-2015.
 */
public class GetNotifications extends AsyncTask<Void, Void, Void> {

    private GlobalApplication appInstance;
    private SessionManager session;
    private ArrayList<NotificationDto> notificationArrayList;
    private String notificationUrl;
    private Context Context;

    public GetNotifications(Context context) {
        Context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(Void... params) {

        appInstance = GlobalApplication.getInstance();
        notificationArrayList = new ArrayList<NotificationDto>();

        ServiceHandler serviceHandler = new ServiceHandler();

        Integer lastNotificationId = 0;
        session = new SessionManager(Context.getApplicationContext());
        String lnid = session.getLastNotificationID();
        lastNotificationId = Utils.parseInteger(lnid);
        Integer pageLength = 10;
        if (lastNotificationId > 0) {
            pageLength = 10;
        }
        pageLength = 10;
        String nurl = String.format(appInstance.NotificationsUrl, lastNotificationId, 1, pageLength);
        String getNotificationUrl = String.format("%s%s", session.getAppUrl(), nurl);
        String token = session.getApiKey();
        if (token == null || token == "") {
            return null;
        }

        notificationUrl = Utils.appendApiKey(getNotificationUrl, session.getApiKey());
        notificationUrl = Utils.appendJsonFormatKey(notificationUrl);
        String xml = serviceHandler.makeServiceCall(notificationUrl, ServiceHandler.GET);

        Log.v("AlarmReceiver", "notificationUrl:" + notificationUrl);

        try {
            JSONObject reader = new JSONObject(xml);

            JSONArray array = reader.getJSONArray(GlobalNames.ResponseData);
            if (array != null) {

            Integer count = array.length();
            Integer numberOfNotifications = count;
            if (numberOfNotifications == 0) {
                return null;
            }
            notificationArrayList.clear();


            PendingIntent pi = getPendingIntent();


            Notification.Builder nbuilder = new Notification.Builder(Context)
                    .setContentTitle(GlobalNames.NewNotifications)
                    .setContentText(session.getAppName())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(Context.getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pi);


            Notification.InboxStyle style = new Notification.InboxStyle(nbuilder);


            String lastId = "0";
            for (int i = 0; i < numberOfNotifications; i++) {
                NotificationDto dto = Utils.getNotification(array.getJSONObject(i));
                //NotificationDto dto=getNotification(i, doc);
                style.addLine(dto.getFromUser() + " " + dto.getActionText());
            }
            Notification notification = style.build();

            // Put the auto cancel notification flag
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.sound = uri;
            NotificationManager notificationManager = getNotificationManager();
            notificationManager.notify(0, notification);

            lastId = Utils.getNotification(array.getJSONObject(0)).getNotificationId();
            session.setLastNotificationID(lastId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(Context, 0, new Intent(Context, NotificationActivity.class), PendingIntent.FLAG_UPDATE_CURRENT
                | PendingIntent.FLAG_ONE_SHOT);
    }

    public NotificationManager getNotificationManager() {
        return (NotificationManager) Context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }


}