package com.ted.tedxhub;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
 * Created by raghav on 19-01-2015.
 */
public class MessageActivity extends Activity {

    private WebView mWebView;
    ProgressDialog mProgress;
    private ProgressBar progress;
    private SessionManager session;
    private Context appContext;
    private GlobalApplication appInstance;
    public String threadID;

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
//                MessageActivity.this.progress.setProgress(0);
//                super.onPageStarted(view, url, favicon);
//            }
//
//            // when finish loading page
//            public void onPageFinished(WebView view, String url) {
////                if(mProgress.isShowing()) {
////                    mProgress.dismiss();
////                }
//                progress.setVisibility(View.GONE);
//                MessageActivity.this.progress.setProgress(100);
//                super.onPageFinished(view, url);
//            }

        });
        //Log.v("Message final url", String.format("%s%s%s", session.getAppUrl(), appInstance.MyAccountMessagesUrl, session.getRecentThreadID()));
        mWebView.loadUrl(String.format("%s%s%s", session.getAppUrl(), appInstance.MyAccountMessagesUrl, session.getRecentThreadID()));

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

