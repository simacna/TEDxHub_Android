package com.axero.communifire;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Patterns;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Raghav
 * Date: 12/31/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public final class Utils {
    public Utils() {

    }
    public static Integer parseInteger(final String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static String appendApiKey(String url, Context context) {
        SessionManager  session = new SessionManager(context);
        url= URLUtils.addParameter(url,"token", session.getApiKey());
        return  url;
    }

    public static String appendApiKey(String url, String apiKey) {
        url= URLUtils.addParameter(url,"token", apiKey);
        return  url;
    }

    public static String appendJsonFormatKey(String url) {
        url=String.format("%s&format=json", url);
        return  url;
    }

    public static String makeUrlFromDomain(String domain) {
        if (!domain.startsWith("http")) {

            domain = String.format("http://%s", domain);
        }

        if (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        return domain;
    }



    public static boolean isNetworkAvailable(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static boolean isUrlValid(String url) {
       return Patterns.WEB_URL.matcher(url).matches();
    }

    public static MessageDto getMessage(JSONObject obj) {
        MessageDto messageDto = new MessageDto();
        try {

            messageDto.setSenderUserDisplayName(obj.getString(GlobalNames.SenderUserDisplayName));
            messageDto.setMessageBody(obj.getString(GlobalNames.MessageBody));
            messageDto.setDateSent(obj.getString(GlobalNames.DateSent));
            messageDto.setMessageID(obj.getString(GlobalNames.MessageID));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return messageDto;
    }

    public static NotificationDto getNotification(JSONObject obj) {
        NotificationDto notification = new NotificationDto();
        try {
            JSONObject fromUser= obj.getJSONObject (GlobalNames.FromUser);

            notification.setFromUser(fromUser.getString(GlobalNames.UserInfoDisplayName));
            notification.setAvatarImageUrl(fromUser.getString(GlobalNames.AvatarImageURL));
            notification.setActionText(obj.getString(GlobalNames.ActionText));
            notification.setSubject(obj.getString(GlobalNames.Subject));
            notification.setDateCreated(obj.getString(GlobalNames.DateCreatedString));
            notification.setNotificationId(obj.getString(GlobalNames.ID));



        } catch (Exception e) {
            e.printStackTrace();
        }

        return notification;

    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
