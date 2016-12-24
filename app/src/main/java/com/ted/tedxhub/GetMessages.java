package com.ted.tedxhub;

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
public class GetMessages extends AsyncTask<Void, Void, Void> {

    private GlobalApplication appInstance;
    private SessionManager session;
    private ArrayList<MessageDto> messagesArrayList;
    private String messageUrl;
    private Context Context;

    public String threadID;

    public GetMessages(Context context) {
        Context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        appInstance = GlobalApplication.getInstance();
        messagesArrayList = new ArrayList<MessageDto>();

        ServiceHandler serviceHandler = new ServiceHandler();
        session = new SessionManager(Context.getApplicationContext());
        Integer lastMessageID = 0;
        String lnid = session.getLastMessageID();
        lastMessageID = Utils.parseInteger(lnid);
        Integer pageLength = 10;
        if (lastMessageID > 0) {
            pageLength = 10;
        }
        pageLength = 10;
        String nurl = String.format(appInstance.UnreadMessagesUrl, lastMessageID, 1, pageLength);
        messageUrl = String.format("%s%s", MainActivity.domain, nurl);

        String xml = serviceHandler.makeServiceCall(messageUrl, ServiceHandler.GET);

        Log.v("AlarmReceiver", "messageUrl:" + messageUrl);

        try {
            JSONObject reader = new JSONObject(xml);

            JSONArray array = reader.getJSONArray(GlobalNames.ResponseData);
            if (array != null) {
                Integer count = array.length();

                Integer numberOfMessages = count;
                if (numberOfMessages == 0) {
                    return null;
                }
                messagesArrayList.clear();


                PendingIntent pi = getPendingIntent();


                Notification.Builder nbuilder = new Notification.Builder(Context)
                        .setContentTitle(GlobalNames.NewMessages)
                        .setContentText(MainActivity.domain)
                        .setSmallIcon(R.drawable.ic_tab)
                        .setLargeIcon(BitmapFactory.decodeResource(Context.getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(pi);


                Notification.InboxStyle style = new Notification.InboxStyle(nbuilder);

                //get current thread ID
                JSONObject objForThreadID = array.getJSONObject(0);
                threadID = objForThreadID.getString(GlobalNames.ThreadID);
                session.setRecentThreadID(threadID);

                String lastID = "0";
                for (int i = 0; i < numberOfMessages; i++) {
                    MessageDto dto = Utils.getMessage(array.getJSONObject(i));
                    //NotificationDto dto=getNotification(i, doc);
                    style.addLine(dto.getSenderUserDisplayName() + ": " + dto.getMessageBody());
                }
                Notification notification = style.build();

                // Put the auto cancel notification flag
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notification.sound = uri;
                NotificationManager notificationManager = getNotificationManager();
                notificationManager.notify(0, notification);

                lastID = Utils.getMessage(array.getJSONObject(0)).getmessageID();
                session.setLastMessageID(lastID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PendingIntent getPendingIntent() {

        return PendingIntent.getActivity(Context, 0, new Intent(Context, MessageActivity.class), PendingIntent.FLAG_UPDATE_CURRENT
                | PendingIntent.FLAG_ONE_SHOT);
    }

    public NotificationManager getNotificationManager() {
        return (NotificationManager) Context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}