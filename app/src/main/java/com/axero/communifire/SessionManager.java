package com.axero.communifire;

/**
 * Created with IntelliJ IDEA.
 * User: Raghav
 * Date: 12/29/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "CommunifirePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_USERNAME = "username";

    // Password (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_API = "apikey";

    public static final String KEY_APPURL = "appurl";
    public static final String KEY_APPNAME = "appName";

    public static final String KEY_LASTNOTIFICATIIONID = "lastnotificationid";
    public static final String KEY_LASTMESSAGEID = "lastmessageid";
    public static final String KEY_RECENTTHREADID = "recentthreadid";


    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String apikey, String username, String password, String domain) {

        if (apikey != null && !apikey.isEmpty()) {
            // Storing login value as TRUE
            editor.putBoolean(IS_LOGIN, true);

            // Storing api key in pref
            editor.putString(KEY_API, apikey);

            // Storing user name in pref
            editor.putString(KEY_USERNAME, username);

            // Storing password in pref
            editor.putString(KEY_PASSWORD, password);

            //domain = Utils.makeUrlFromDomain(domain);
            if ( !Utils.isUrlValid(domain)){
                domain = Utils.makeUrlFromDomain(domain);
            }
            editor.putString(KEY_APPURL, domain);

            // commit changes
            editor.commit();
        }
    }

    public void logout() {

        editor.remove(IS_LOGIN);
        editor.remove(KEY_PASSWORD);
        //editor.clear();
        editor.commit();
        AlarmHelper.CancelAlarm(_context);

    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // user email id
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // return user
        return user;
    }

    public String getAppName() {

        return pref.getString(KEY_APPNAME, null);
    }

    public String getUsername() {

        return pref.getString(KEY_USERNAME, null);
    }


    public void setAppName(String appName) {
        editor.putString(KEY_APPNAME, appName);

        // commit changes
        editor.commit();
    }

    public String getLastNotificationID() {

        return pref.getString(KEY_LASTNOTIFICATIIONID, null);
    }

    public void setLastNotificationID(String id) {
        editor.putString(KEY_LASTNOTIFICATIIONID, id);

        // commit changes
        editor.commit();
    }

    public String getLastMessageID() {

        return pref.getString(KEY_LASTMESSAGEID, null);
    }

    public void setLastMessageID(String id) {
        editor.putString(KEY_LASTMESSAGEID, id);

        // commit changes
        editor.commit();
    }


    public String getRecentThreadID() {

        return pref.getString(KEY_RECENTTHREADID, null);
    }

    public void setRecentThreadID(String id) {
        editor.putString(KEY_RECENTTHREADID, id);

        // commit changes
        editor.commit();
    }


    public String getApiKey() {
        return pref.getString(KEY_API, null);
    }

    public String getAppUrl() {
        return pref.getString(KEY_APPURL, null);
    }

    public Boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);

    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            //    user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }
}
