package x40241.jeffrey.lomibao.a5;

import android.graphics.Color;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Jefflom on 5/10/2017.
 */

public class UserManualActivity extends AppCompatActivity {

    private final boolean DEBUG = true;
    public static final String LOGTAG = "A5-Jeffrey.Lomibao";
    public static final String PDF_VIEWER_URL = "http://drive.google.com/viewerng/viewer?embedded=true&url=";
    public static final String USER_MANUAL_URL = "http://www2.emersonprocess.com/siteadmincenter/PM%20Rosemount%20Analytical%20Documents/Liq_Manual_51-1066.pdf";

    private WebView webView;
    private FetchDataTask fetchDataTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);

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
        fetchDataTask.execute(PDF_VIEWER_URL+USER_MANUAL_URL);
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

    //**********************************************************************************************
    //  Extend AsynTask to implement a Task that will be run partly in the Main thread, which allows
    //  UI updates, and partly in a separate background Thread so as to not affect UI liveness.
    //**********************************************************************************************
    class FetchDataTask
            extends AsyncTask<String, Void, String>
    {
        private final String LOGTAG = FetchDataTask.class.getSimpleName();
        private final boolean DEBUG = true;

        //  This runs in the main thread and can update the UI
        @Override
        protected void onPreExecute() {
            if (DEBUG) Log.d(LOGTAG, "**** onPreExecute() STARTING");
        }

        //  This runs in a separate background thread.  UI cannot be updated from here.
        @Override
        protected String doInBackground (final String... paramArrayOfParams) {
            if (DEBUG) Log.d(LOGTAG, "**** doInBackground() STARTING");

            // Get feedUrl param passed to us.
            final String url = paramArrayOfParams[0];

            return url;
        }

        //  This runs in the main thread and can update the UI
        @Override
        protected void onPostExecute (final String urlUserManual) {
            if (urlUserManual == null) {
                return;
            }
            webView.loadUrl(urlUserManual);
        }
    }
}