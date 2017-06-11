package x40241.jeffrey.lomibao.a2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import x40241.jeffrey.lomibao.a2.model.StockInfo;
import x40241.jeffrey.lomibao.a2.net.StockDataSAX;


/**
 * Skeleton of a basic/simplistic Service with lifcycle stubs enumerated.
 * 
 * @author Jeffrey Peacock (Jeffrey.Peacock@uci.edu)
 */
public class StockServiceImpl
    extends Service
{
    private static final String LOGTAG = "StockServiceImpl";
    private static final boolean DEBUG = true;
    private static final int SERVICE_ID = 123456789;

    public static final String STOCK_SERVICE_INTENT = "x40241.jeffrey.lomibao.STOCK_SERVICE";
    public static final String STOCK_SERVICE_URL = "http://x40241-stockservice.appspot.com/StockServlet";

    // Track if we've been started at least once.
    private boolean initialized   = false;
    
    // Track if a client Activity is bound to us.
    private boolean isBound = false;
    
    // This is the object that receives interactions from clients.
    private final IBinder localBinder = new LocalBinder();
    
    //  for managing notifications
    private NotificationManager notificationManager;

    private Handler mHandler;
    Runnable mStatusChecker;

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public StockServiceImpl getService() {
            return StockServiceImpl.this;
        }
    }
    
    // Constructs the explicit Intent to use to start this service.
    // Placing this construction here encapsulates the info and allows other classes easy use.
    //
    // @param context
    // @param client
    // @return
    public static Intent getServiceIntent() {
        Intent intent = new Intent(STOCK_SERVICE_INTENT);
        return intent;
    }
    
    //**********************************************************************************************
    //  LIFECYCLE METHODS
    //**********************************************************************************************
    @Override
    public void onCreate() {
        Log.d (LOGTAG, "*** onCreate(): STARTING");
        Log.d (LOGTAG, "*** onCreate(): ENDING");
    }
    
    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        if (DEBUG) {
            Log.d (LOGTAG, "*** onStartCommand(): STARTING; initialized="+initialized);
            Log.d (LOGTAG, "*** onStartCommand(): flags="+flags);
            Log.d (LOGTAG, "*** onStartCommand(): intent="+intent);
        }
        if (initialized)
            return START_STICKY;
        initialize();
        
        // Display a notification about us starting. We put an icon in the status bar.
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        
        Log.d (LOGTAG, "*** onStart(): ENDING");
        // We want this service to continue running until it is explicitly stopped.
        return START_STICKY;
    }

    static private long lastSequence;
    class GetStockData implements Runnable {
        @Override
        public void run() {
            if (DEBUG) Log.d (LOGTAG, "*** GetStockData run.");
            List<StockInfo> stockInfoList = getStockData();
            if (DEBUG) Log.d(LOGTAG, "stockInfoList.size = " + stockInfoList.size());
            // send stock data to activity
            if(stockInfoList.size() > 0) {
                long sequence = stockInfoList.get(0).getSequence();
                if (DEBUG) Log.d(LOGTAG, "sequence = " + sequence + "; last = " + lastSequence);
                if(sequence > lastSequence) {
                    lastSequence = sequence;
                    sendMessageToActivity(stockInfoList);
                }
            }
        }
    }

    private void initialize() {
        //  TODO:  Implement this with specific initialization code.
        if(DEBUG) Log.d (LOGTAG, "*** initialize");
        initialized = true;

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new GetStockData(), 5, 5, TimeUnit.SECONDS);
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

        notificationManager.notify(SERVICE_ID, mBuilder.build());
    }
    
    @Override
    public void onDestroy() {
        Log.d (LOGTAG, "*** onDestroy()");
        mHandler.removeCallbacks(mStatusChecker);
        notificationManager.cancel(SERVICE_ID);
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
    public boolean onUnbind (Intent intent) {
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
    
    //  utility method for retrieving stock data.
    private List<StockInfo> getStockData()
    {
        URL url = null;
        InputStream in = null;
        List<StockInfo> stockData = null;
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
            stockData = new StockDataSAX().parse(in);
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
        return stockData;
    }

    // Use local broadcast manager for sending stock data to activity
    private void sendMessageToActivity(List<StockInfo> stockInfoList) {
        Intent intent = new Intent("StockUpdates");
        Bundle bundle = new Bundle();
        bundle.putSerializable("StockInfoList", (Serializable)stockInfoList);
        intent.putExtra("StockInfoListBundle", bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
