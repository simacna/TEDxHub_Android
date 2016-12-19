package com.axero.communifire;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: Raghav
 * Date: 12/28/13
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginActivity extends Activity {
    private ProgressDialog pDialog;
    // Session Manager Class
    SessionManager session;
    GlobalApplication appInstance;
    Context appContext;
    EditText text_name;
    EditText text_password;
    EditText text_domain;
    String domain;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = getApplicationContext();

        appInstance = GlobalApplication.getInstance();

        // Session Manager
        session = new SessionManager(appContext);
        setContentView(R.layout.login);

        Intent i = new Intent(appContext, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Starting main Activity
        appContext.startActivity(i);
        finish();
        //session.logoutUser();
//        if (session.isLoggedIn()) {
//            // Staring MainActivity
//            Intent i = new Intent(appContext, MainActivity.class);
//            // Closing all the Activities
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//            // Add new Flag to start new Activity
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            // Staring Login Activity
//            appContext.startActivity(i);
//            finish();
//        } else {
//
//            setContentView(R.layout.login);
//
//            text_name = (EditText) findViewById(R.id.text_name);
//            text_password = (EditText) findViewById(R.id.text_password);
//            text_domain = (EditText) findViewById(R.id.text_domain);
//
//            String appUrl = session.getAppUrl();
//            String username = session.getUsername();
//
//            if (appUrl != null) {
//                text_domain.setText(appUrl);
//            }
//
//            if (username != null) {
//                text_name.setText(username);
//                text_password.requestFocus();
//            }
//
//
//        }


    }

    public void onLoginButtonClick(View view) {
        // Calling async task to get json
        new GetLoginSession().execute();
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetLoginSession extends AsyncTask<Void, Void, LoginStatus> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(getString(R.string.pleasewait));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected LoginStatus doInBackground(Void... arg0) {

            LoginStatus loginStatus;
            Boolean isValid = false;

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String name = text_name.getText().toString().trim();
            String password = text_password.getText().toString().trim();
            domain = text_domain.getText().toString().trim();
            String domainWithScheme = Utils.makeUrlFromDomain(domain);
            loginStatus = MakeURLCall(domainWithScheme, sh, name, password);
            if (loginStatus == LoginStatus.InvalidCrentials) {
                domainWithScheme =  removeCharAt(domainWithScheme, 4);
                loginStatus = MakeURLCall(domainWithScheme, sh, name, password);
            }
            return loginStatus;
        }

        @Override
        protected void onPostExecute(LoginStatus loginStatus) {
            super.onPostExecute(loginStatus);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            switch (loginStatus) {
                case InvalidUrl:
                    Toast.makeText(appContext, getString(R.string.enter_valid_url), Toast.LENGTH_SHORT).show();
                    break;
                case InvalidCrentials:
                    Toast.makeText(appContext, getString(R.string.enter_valid_credntials), Toast.LENGTH_SHORT).show();
                    break;
                case Error:
                    Toast.makeText(appContext, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                    break;
                case Success:
                    new GetApplicationDetails().execute();
                    break;
                case InternetConnectionMissing:
                    Toast.makeText(appContext, getString(R.string.internet_connection_check), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    public LoginStatus MakeURLCall(String domain, ServiceHandler sh, String name, String password) {

        Boolean match = Utils.isUrlValid(domain);
        LoginStatus loginStatus;
        Boolean isValid = false;
        String nameEncoded = name, passwordEncoded = password;
        if (!match) {
            loginStatus = LoginStatus.InvalidUrl;
            return loginStatus;
        }
        try {
            nameEncoded = URLEncoder.encode(name, "utf-8");
            passwordEncoded = URLEncoder.encode(password, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format("%s%s", domain, String.format(appInstance.ValidateCredentialsUrl, nameEncoded, passwordEncoded));

        if (!Utils.isNetworkAvailable(appContext)) {
            loginStatus = LoginStatus.InternetConnectionMissing;
            return loginStatus;
        }
        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
        Log.v("Login session url: ", url);
        Log.d("Response: ", "> " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                String ResponseData = jsonObj.getString(GlobalNames.ResponseData);

                session.createLoginSession(ResponseData, name, password, domain);

                if (session.isLoggedIn()) {
                    isValid = true;
                    loginStatus = LoginStatus.Success;
                } else {
                    isValid = false;
                    loginStatus = LoginStatus.InvalidCrentials;
                }

            } catch (JSONException e) {

                isValid = false;
                loginStatus = LoginStatus.InvalidCrentials;
                e.printStackTrace();
            }

        } else {
            loginStatus = LoginStatus.Error;
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
        return loginStatus;
    }

    private class GetApplicationDetails extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(getString(R.string.pleasewait));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            Boolean isValid = false;

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            domain = text_domain.getText().toString().trim();

            String nurl = String.format(appInstance.ApplicationDetailsUrl);
            String getNotificationUrl = String.format("%s%s", session.getAppUrl(), nurl);
            String token = session.getApiKey();
            if (token == null || token.equals("")) {
                return null;
            }

            String url = Utils.appendApiKey(getNotificationUrl, session.getApiKey());
            url = Utils.appendJsonFormatKey(url);

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.v("Login url: ", url);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String ResponseData = jsonObj.getString(GlobalNames.ResponseData);

                    session.setAppName(ResponseData);
                    isValid = true;

                } catch (JSONException e) {

                    isValid = false;
                    e.printStackTrace();
                }

            } else {
                isValid = false;
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return isValid;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (!result) {
                Toast.makeText(appContext, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            } else {
                // Staring MainActivity
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("caller", "LoginActivity");
                startActivity(i);
                finish();
            }
        }
    }
}