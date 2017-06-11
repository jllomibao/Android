package x40241.jeffrey.lomibao.a3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import x40241.jeffrey.lomibao.a3.db.DBHelper;
import x40241.jeffrey.lomibao.a3.model.PriceData;
import x40241.jeffrey.lomibao.a3.model.StockInfo;
import x40241.jeffrey.lomibao.a3.model.StockQuote;
import x40241.jeffrey.lomibao.a3.net.StockDataSAX;

public final class LocalStockService
    extends Service
{
    private static final String LOGTAG = "LocalStockService";
    private static final boolean DEBUG = true;
    private static final int NOTIFICATION_ID = 1;

    public static final String STOCK_SERVICE_URL = "http://x40241-stockservice.appspot.com/StockServlet";
    public static final int    ONE_SECOND     = 1000;  // milliseconds
    public static final int    STOCK_SERVICE_POLL_PERIOD = 5 * ONE_SECOND;


    // Track if we've been started at least once.
    private boolean initialized   = false;

    // Track if a client Activity is bound to us.
    private boolean isBound = false;

    // This is for managing notifications
    private NotificationManager notificationManager;
    private final Timer timer = new Timer("DataWorker");

    // This is for managing the database
    DBHelper dbHelper;
    private List<StockInfo> stockInfo;
    private List<PriceData> priceData;

    // This is the object that receives interactions from clients.
    private final IBinder localBinder = new LocalBinder();

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder
            extends Binder
    {
        public LocalStockService getService() {
            return LocalStockService.this;
        }
    }

    // Setup the Intent we will use to connect to our service.
    public static Intent getServiceIntent (Context context, String clientname) {
        Intent intent = new Intent(context, LocalStockService.class);
        intent.putExtra("client", clientname);
        return intent;
    }
    
    @Override
    public void onCreate() {
        // Called when the service is first created.
        if (DEBUG) Log.d (LOGTAG, "*** onCreate()");
        //  Setup platform things we'll need for our Service;  Do UI actions
        if (DEBUG) Log.d (LOGTAG, "*** onCreate(): COMPLETED");
    }

    //  encapsulate non-service logic -- e.g., business logic
    private void initialize() { 
        if (DEBUG) Log.d (LOGTAG, "*** initialize() STARTED");
        // initialize database
        dbHelper = new DBHelper(this);

        // initialize timer that will periodically poll for new data
        timer.scheduleAtFixedRate(new DataWorker(), ONE_SECOND, STOCK_SERVICE_POLL_PERIOD);

        initialized = true;
        if (DEBUG) Log.d (LOGTAG, "*** initialize() COMPLETED");
    }

    private void showNotification() {
        //  TODO:  Implement this.
        Log.d (LOGTAG, "*** show notification");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_menu_home)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.stock_service_notification))
                .setAutoCancel(true);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGTAG, "Received start id " + startId + ": " + intent);
        if (initialized)
            return START_STICKY;

        initialize();
        // Display a notification about us starting. We put an icon in the status bar.
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification();

        Log.d (LOGTAG, "*** onStart(): ENDING");
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d (LOGTAG, "*** onDestroy()");
        notificationManager.cancel(NOTIFICATION_ID);
//        dbHelper.close();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        Log.d (LOGTAG, "*** onConfigurationChanged()");
    }

    @Override
    public void onLowMemory() {
        Log.d (LOGTAG, "*** onLowMemory()");
    }

    @Override
    public IBinder onBind (Intent intent) {
        Log.d (LOGTAG, "*** onBind()");
        if (DEBUG) {
            Log.d (LOGTAG, "*** onBind(): action="+intent.getAction());
            Log.d (LOGTAG, "*** onBind(): toString="+intent.toString());
        }
        isBound = true;
        return localBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d (LOGTAG, "*** onUnbind()");
        if (DEBUG) {
            Log.d (LOGTAG, "*** onUnbind(): action="+intent.getAction());
            Log.d (LOGTAG, "*** onUnbind(): toString="+intent.toString());
        }
        isBound = false;
        return true;
    }
    
    @Override
    public void onRebind (Intent intent) {
        Log.d (LOGTAG, "*** onRebind()");
        if (DEBUG) {
            Log.d (LOGTAG, "*** onUnbind(): action="+intent.getAction());
            Log.d (LOGTAG, "*** onUnbind(): toString="+intent.toString());
        }
    }

    private int count;
    static private long lastSequence; // used for detecting new data

    class DataWorker
        extends TimerTask
    {
        private final String LOGTAG = "DataWorker";
        public void run() {
            Log.d(LOGTAG, "***** STARTING");

            List<StockQuote> newStockQuote = getStockData();
            if (DEBUG) Log.d(LOGTAG, "stockInfo.size = " + newStockQuote.size());
            // send new stock data to activity
            if(newStockQuote.size() > 0) {
                long sequence = newStockQuote.get(0).getSequence();
                if (DEBUG) Log.d(LOGTAG, "sequence = " + sequence + "; last = " + lastSequence);
                if(sequence > lastSequence) {
                    lastSequence = sequence;
                    dbHelper.update(newStockQuote);
                    stockInfo = dbHelper.getStockInfoFromCache();
                    // Sort by stock name
                    Collections.sort(stockInfo, new Comparator<StockInfo>() {
                        @Override
                        public int compare(StockInfo lhs, StockInfo rhs) {
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    });
                    notifyNewStockData(stockInfo);
                }
            }
        }
    }

    //  utility method for retrieving stock data.
    private List<StockQuote> getStockData()
    {
        URL url = null;
        InputStream in = null;
        List<StockQuote> stockQuote = null;
        try {
            url = new URL(STOCK_SERVICE_URL);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                //  TODO:  Handle the error.
                Log.e (LOGTAG, "responseCode="+responseCode);
                return null;
            }
            in = httpConnection.getInputStream();
            stockQuote = new StockDataSAX().parse(in);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Throwable t) {
            //  At least ensure the thread always ends orderly, even
            //  in the event of something completely unexpected
            t.printStackTrace();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                }
                catch (IOException e) { /* ignore */ }
            }
        }
        return stockQuote;
    }

    private final ArrayList<OnNewStockDataListener> listeners = new ArrayList<>();
    public interface OnNewStockDataListener {
        public void notifyNewStockData(final List<StockInfo> stockInfo);
    }
    public void registerOnNewStockDataListener (final OnNewStockDataListener listener) {
        listeners.add(listener);
    }
    public void unregisterOnNewStockDataListener (final OnNewStockDataListener listener) {
        listeners.remove(listener);
    }
    private void notifyNewStockData (final List<StockInfo> stockInfo) {
        this.stockInfo = stockInfo;
        count++;
        if(DEBUG) {
            Log.d(LOGTAG, "notifyNewStockData(): count="+count);
            Log.d(LOGTAG, "notifyNewStockData(): listeners.size="+listeners.size());
        }
        for (OnNewStockDataListener listener : listeners) {
            listener.notifyNewStockData(stockInfo);
        }
    }
}
