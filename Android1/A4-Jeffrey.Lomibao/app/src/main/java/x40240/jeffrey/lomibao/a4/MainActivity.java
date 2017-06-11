package x40240.jeffrey.lomibao.a4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity
    extends Activity
{
    private final boolean DEBUG = true;
    public static final String LOGTAG = "A4-Jeffrey.Lomibao";
    public static final String LOCATION_BASE_URL = "http://jeffreypeacock.com/uci/x402.40/data/";
    public static final String MAPS_BASE_URL = "http://www.google.com/maps/search/";

    private int      index;
    private int      locationCount;

    // UI Components
    private WebView webView;
    private TextView descriptionText;
    private TextView addressText;
    private ProgressBar  progressBar;

    private FetchDataTask fetchDataTask;

    @SuppressLint ("SetJavaScriptEnabled")
    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(LOGTAG, "**** onCreate");
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.web_view);
        webView.setKeepScreenOn(true);
        webView.setInitialScale(100);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new MyWebViewClient());
        
        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);


        descriptionText = (TextView) findViewById(R.id.description_text);
        addressText = (TextView) findViewById(R.id.address_text);
        progressBar = (ProgressBar)  findViewById(R.id.progress_bar);
        locationCount = 0;
    }

    @Override
    public boolean onCreateOptionsMenu (final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        if (DEBUG) Log.d(LOGTAG, "**** onResume");
        if (fetchDataTask != null)
            return;
        fetchDataTask = new FetchDataTask();
        fetchDataTask.execute(LOCATION_BASE_URL, String.valueOf(locationCount));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (DEBUG) Log.d(LOGTAG, "**** onPause()");
        if (fetchDataTask != null) {
            fetchDataTask.cancel(true);
            fetchDataTask = null;
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(final WebView view, final String url) {
            super.onPageFinished(view, url);
            Log.d(LOGTAG, "onPageFinished.url="+url);
        }

        @Override
        public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
            Log.e(LOGTAG, "onReceivedSslError="+error.toString());
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            Log.d(LOGTAG, "shouldOverrideUrlLoading.url="+url);
            view.loadUrl(url);
            return false;
        }
    }

    public void onNextButtonClick (final View view) {
        Log.d(LOGTAG, "**** onNextButtonClick");
        //  When this method gets called it is from the Main thread
        if (fetchDataTask != null)
            return;
        fetchDataTask = new FetchDataTask();
        fetchDataTask.execute(LOCATION_BASE_URL, String.valueOf(locationCount));

    }

    private void onTaskCompleted(final boolean success) {
        fetchDataTask = null;
        if (success)
            locationCount++;
        else
            locationCount = 0;
    }
    //**********************************************************************************************
    //  Extend AsynTask to implement a Task that will be run partly in the Main thread, which allows
    //  UI updates, and partly in a separate background Thread so as to not affect UI liveness.
    //**********************************************************************************************
    class FetchDataTask
            extends AsyncTask<String, Void, LocationInfo>
    {
        private final String LOGTAG = FetchDataTask.class.getSimpleName();
        private final boolean DEBUG = true;

        //  This runs in the main thread and can update the UI
        @Override
        protected void onPreExecute() {
            if (DEBUG) Log.d(LOGTAG, "**** onPreExecute() STARTING");
            progressBar.setVisibility(View.VISIBLE);
        }

        //  This runs in a separate background thread.  UI cannot be updated from here.
        @Override
        protected LocationInfo doInBackground (final String... paramArrayOfParams) {
            if (DEBUG) Log.d(LOGTAG, "**** doInBackground() STARTING");

            // Get feedUrl param passed to us.
            final String feedUrl = paramArrayOfParams[0];
            int locationCount = Integer.parseInt(paramArrayOfParams[1]);

            LocationInfo  locationInfo = null;
            InputStream in = null;

            try {
                int tryCount = 0;
                do {
                    final StringBuilder sb = new StringBuilder(feedUrl);
                    sb.append("location-");
                    sb.append(locationCount);
                    sb.append(".xml");
                    if (DEBUG) Log.d(LOGTAG, "URL: " + sb.toString());

                    // http://www.jeffreypeacock.com/uci/x402.40/data/location-0.xml
                    final URL url = new URL(sb.toString());
                    final HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    final int responseCode = httpConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        in = httpConnection.getInputStream();
                        locationInfo = new LocationInfoSAX().parse(in);
                        break;
                    } else {
                        // Handle error.
                        Log.e (LOGTAG, "responseCode="+responseCode);
                        if(locationCount < 5) {
                            MainActivity.this.locationCount = ++locationCount;
                        } else {
                            MainActivity.this.locationCount = locationCount = 0;
                        }
                    }
                } while (++tryCount < 5);
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
            catch (final Throwable t) {
                t.printStackTrace();
            }
            finally {
                if (in != null) {
                    try {in.close();}
                    catch (IOException e) { /* ignore */ }
                }
            }
            return locationInfo;
        }

        //  This runs in the main thread and can update the UI
        @Override
        protected void onPostExecute (final LocationInfo locationInfo) {
            progressBar.setVisibility(View.GONE);
            if (locationInfo == null) {
                onTaskCompleted(false);
                return;
            }

            // display location info
            descriptionText.setText(locationInfo.getDescription());
            addressText.setText(locationInfo.getAddress());

            // map location
            final StringBuilder url = new StringBuilder();
            url.append(MAPS_BASE_URL); // get base url
            url.append("@" + locationInfo.getLatitude());
            url.append("," + locationInfo.getLongitude());
            // set zoom level based on altitude
            double altitude = locationInfo.getAltitude();
            Log.d(LOGTAG, "altitude = " + altitude);
            if(altitude > 20e3) {
                url.append("," + "12z");
            } else if(altitude > 10e3) {
                url.append("," + "13z");
            } else if(altitude > 1e3) {
                url.append("," + "14z");
            } else if(altitude > 400) {
                url.append("," + "15z");
            } else if(altitude > 100) {
                url.append("," + "16z");
            } else {
                url.append("," + "17z");
            }
            Log.d(LOGTAG, "url="+url);
            webView.loadUrl(url.toString());

            onTaskCompleted(true);
        }
    }
}

