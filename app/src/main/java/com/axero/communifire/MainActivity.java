package com.axero.communifire;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.ViewTreeObserver;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    private WebView mWebView;
    private ProgressBar progress;
    private SessionManager session;
    private Context appContext;
    private GlobalApplication appInstance;
    String currentUrl;
    private static final String domain = "http://tedxhub.ted.com";


    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";

    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if(resultCode == Activity.RESULT_OK) {
            if(data == null) {
                // If there is not data, then we may have taken a photo
                if(mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
        return;
    }


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
                // initially hide the indicator (onPageStarted can handle it)
                progress.setVisibility(View.GONE);

              if (url.endsWith("logout")) {
                  SessionManager session = new SessionManager(appContext);
                  session.logout();

                  mWebView.clearCache(true);
                  mWebView.clearHistory();

                  Utils.clearCookies(appContext);

//                  // Staring MainActivity
//                  Intent i = new Intent(appContext, LoginActivity.class);
//                  // Closing all the Activities
//                  i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                  // Add new Flag to start new Activity
//                  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                  // Staring Login Activity
//                  appContext.startActivity(i);
//                  finish();
                  view.loadUrl(domain);
                  return true;
              }
              currentUrl = url;
              view.loadUrl(url);
              return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Chat app already has its own loading indicator
                if (!url.startsWith(domain + "/chat")) {
                    swipeView.setEnabled(true);
                    progress.setVisibility(View.VISIBLE);
                    MainActivity.this.progress.setProgress(0);
                    super.onPageStarted(view, url, favicon);
                }
                else
                {
                    // disable swipe down refresh when user is on chat app
                    swipeView.setEnabled(false);
                }
            }

            // when finish loading page
            public void onPageFinished(WebView view, String url) {

                progress.setVisibility(View.GONE);
            }

            public boolean onShowFileChooser(
                  WebView webView, ValueCallback<Uri[]> filePathCallback,
                  WebChromeClient.FileChooserParams fileChooserParams) {
              if (mFilePathCallback != null) {
                  mFilePathCallback.onReceiveValue(null);
              }
              mFilePathCallback = filePathCallback;

              Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                  // Create the File where the photo should go
                  File photoFile = null;
                  try {
                      photoFile = createImageFile();
                      takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                  } catch (IOException ex) {
                      // Error occurred while creating the File
                      Log.e(TAG, "Unable to create Image File", ex);
                  }

                  // Continue only if the File was successfully created
                  if (photoFile != null) {
                      mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                              Uri.fromFile(photoFile));
                  } else {
                      takePictureIntent = null;
                  }
              }

              Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
              contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
              contentSelectionIntent.setType("image/*");

              Intent[] intentArray;
              if (takePictureIntent != null) {
                  intentArray = new Intent[]{takePictureIntent};
              } else {
                  intentArray = new Intent[0];
              }

              Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
              chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
              chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
              chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

              startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

              return true;
            }

            });

        mWebView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart (String url, String userAgent,
                        String contentDisposition, String mimetype,
                long contentLength){
                    // handle download, here we use browser to download, also you can try other approach.
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });




        // Alarm work

        AlarmHelper.setAlarm(this);

        Context appContext = getApplicationContext();
        SessionManager session=new SessionManager(appContext);
        GlobalApplication appInstance = GlobalApplication.getInstance();

        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put(GlobalNames.SecuredToken,session.getApiKey());
        extraHeaders.put(GlobalNames.RememberMe,"1");

        mWebView.loadUrl(domain, extraHeaders);


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


    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_logout:
                SessionManager session=new SessionManager(appContext);
                session.logout();
                // Staring MainActivity
                Intent i = new Intent(appContext, LoginActivity.class);
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Staring Login Activity
                appContext.startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


