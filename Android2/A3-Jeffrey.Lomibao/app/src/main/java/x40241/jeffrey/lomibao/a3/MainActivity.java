package x40241.jeffrey.lomibao.a3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import x40241.jeffrey.lomibao.a3.model.StockInfo;

public class MainActivity
    extends AppCompatActivity
        implements LocalStockService.OnNewStockDataListener
{
    private static final String LOGTAG = "MainActivity";
    private static final boolean DEBUG = true;
    private Context activityContext = this;

    private boolean isBound;            // are we bound to our service
    private Intent  stockServiceIntent; // the intent used to start our service
    private LocalStockService stockService;  // a ref to the service once we are bound
    
    private TextView statusText;
    private TextView countText;
    private TextView stockDataText;

    // these are for showing a list of stock prices
    private ListView stocksListView;
    private StocksListAdapter stocksListAdapter;

    private final Handler handler = new Handler();
    
    //  The ServiceConnection object the platform will use to inform us the service is started, 
    //  we are bound to it, and we can reference it directly.
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d (LOGTAG, "*** onServiceConnected");
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            stockService = ((LocalStockService.LocalBinder)service).getService();
            
            //  Once we are bound to our Service we can access it like any other object to
            //  get "services" -- i.e., call methods on it.
            stockService.registerOnNewStockDataListener(MainActivity.this);
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d (LOGTAG, "*** onServiceDisconnected");
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            stockService.unregisterOnNewStockDataListener(MainActivity.this);
            stockService = null;
            Toast.makeText(MainActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Called when the activity is first created.
        Log.d (LOGTAG, "*** onCreate(): STARTING");
        initialize();

        // Get UI references
        stocksListAdapter = new StocksListAdapter(this);
        stocksListView = (ListView)this.findViewById(R.id.stocksListView);
        stocksListView.setAdapter(stocksListAdapter);
        stocksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> arg0, View view, int position, long id) {
                if (DEBUG) {
                    Log.d (LOGTAG, "Item " + (position+1) + " clicked");
                    Log.d (LOGTAG, "onItemClick(): arg0="+arg0.getClass().getSimpleName());
                    Log.d (LOGTAG, "onItemClick(): arg1="+view.getClass().getSimpleName());
                }
                StockInfo listItem = (StockInfo) stocksListView.getItemAtPosition(position);
                Log.d (LOGTAG, listItem.getName());
                //  Start new Activity.
                //  The child Activity should be able to display the data that was selected.
                // Use an IMPLICIT intent
                Intent myIntent = new Intent();
                myIntent.putExtra("listItem", listItem);

                myIntent.setAction("x40241.jeffrey.lomibao.a3.intent.action.listItem");
                if(myIntent.resolveActivity(getPackageManager())!= null) {
                    startActivity(myIntent);
                }
            }
        });

        // Set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Log.d (LOGTAG, "*** onCreate(): COMPLETED");
    }
    
    //  Encapsulate non-ui initialization
    private void initialize() {
        Log.d (LOGTAG, "*** initialize(): STARTING");
        
        // Make sure the service is started.  It will continue running until someone calls
        // stopService().  The Intent we use to find the service explicitly specifies our service
        // component, because we want it running in our own process and don't want other
        // applications to replace it.
        stockServiceIntent = LocalStockService.getServiceIntent(this.getBaseContext(), LOGTAG);
        startService(stockServiceIntent);

        // Establish a connection with the service.  We use an explicit class name because we want
        // a specific service implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other applications).
        isBound = bindService(stockServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.d (LOGTAG, "*** initialize(): COMPLETED");
    }
    
    @Override
    public void onStart() {
        // Called after onCreate() OR onRestart()
        Log.d (LOGTAG, "onStart");
        super.onStart();
    }
    
    @Override
    public void onRestart() {
        //  Called after onPause() as Activity is brought
        //  Called after onStop() but process has not been killed.
        Log.d (LOGTAG, "onRestart");
        
        //  Bind to our service...
        if (! isBound)
            isBound = bindService(stockServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        super.onRestart();
    }
    
    @Override
    public void onResume() {
        //  Called after onStart() as Activity comes to foreground.
        Log.d (LOGTAG, "onResume(): STARTED");
        super.onResume();
        Log.d (LOGTAG, "onResume(): COMPLETED");

    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //  Called before an Activity is killed.
        Log.d (LOGTAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onPause() {
        //  Called when Activity is placed in background
        Log.d (LOGTAG, "onPause");
        super.onPause();
        if (isBound)
        {
            // Detach our existing connection.
            unbindService(serviceConnection);
            isBound = false;
        }
    }
    
    @Override
    public void onStop() {
        Log.d (LOGTAG, "onStop");
        super.onStop();
    }
    
    @Override
    public void onDestroy() {
        Log.d (LOGTAG, "onDestroy");
        super.onDestroy();
    }

    /**********************************************************************************************
     * Action Bar methods
     *********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    public void onActionSaveClick(MenuItem item) {
        if(DEBUG) Log.d(LOGTAG, "Action Save clicked.");
        Toast.makeText(activityContext, R.string.action_save, Toast.LENGTH_SHORT).show();
    }

    public void onActionRevertClick(MenuItem item) {
        if(DEBUG) Log.d(LOGTAG, "Action Revert clicked.");
        Toast.makeText(activityContext, R.string.action_revert, Toast.LENGTH_SHORT).show();
    }

    public void onActionSettingsClick(MenuItem item) {
        if(DEBUG) Log.d(LOGTAG, "Action Settings clicked.");
        Toast.makeText(activityContext, R.string.action_settings, Toast.LENGTH_SHORT).show();
    }

    private int receiveCount;
    @Override
    public void notifyNewStockData(final List<StockInfo> stockData) {
        receiveCount++;
        if(DEBUG) Log.d(LOGTAG, "*** receiveCount = " + receiveCount);
        handler.post(new Runnable() {
            public void run() {
                stocksListAdapter.setList(stockData);
                stocksListAdapter.notifyDataSetChanged();
            }
        });
    }
}