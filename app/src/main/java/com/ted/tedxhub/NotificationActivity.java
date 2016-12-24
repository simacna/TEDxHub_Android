package com.ted.tedxhub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by raghav on 17-01-2015.
 */
public class NotificationActivity extends Activity {

    private WebView mWebView;
    private ProgressBar progress;
    private SessionManager session;
    private Context appContext;
    private GlobalApplication appInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SwipeRefreshLayout swipeView =(SwipeRefreshLayout)findViewById(R.id.swipe);

        appContext = getApplicationContext();
        appInstance = GlobalApplication.getInstance();
        session = new SessionManager(appContext);
        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

//        mProgress = ProgressDialog.show(this, "Loading", "Please wait for a moment...");
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        swipeView.setColorSchemeResources(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.reload();
                        swipeView.setRefreshing(false);

                    }
                }, 4000);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                progress.setVisibility(View.VISIBLE);
//                NotificationActivity.this.progress.setProgress(0);
//                super.onPageStarted(view, url, favicon);
//            }
//
//            // when finish loading page
//            public void onPageFinished(WebView view, String url) {
////                if(mProgress.isShowing()) {
////                    mProgress.dismiss();
////                }
//                progress.setVisibility(View.GONE);
//                NotificationActivity.this.progress.setProgress(100);
//                super.onPageFinished(view, url);
//            }

        });

        //mWebView.loadUrl(String.format("%s%s", MainActivity.domain, appInstance.MyAccountNotificationsUrl));

        MainActivity.takeToUrl = String.format("%s%s", MainActivity.domain, appInstance.MyAccountNotificationsUrl);

        Intent i = new Intent(appContext, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Starting main Activity
        appContext.startActivity(i);
        finish();

        // Only enable swipeToRefresh if is mainWebView is scrolled to the top.
        mWebView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mWebView.getScrollY() == 0) {
                    swipeView.setEnabled(true);
                } else {
                    swipeView.setEnabled(false);
                }
            }
        });
    }
}
