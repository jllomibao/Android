package x40241.jeffrey.lomibao.a4;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
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
import android.widget.ViewSwitcher;

import java.util.List;

import x40241.jeffrey.lomibao.a4.db.DBHelper;
import x40241.jeffrey.lomibao.a4.model.StockInfo;

public class MainActivity
    extends AppCompatActivity
//        implements StockServiceImpl.OnNewStockDataListener
{
    static final String PACKAGE_NAME = "x40241.jeffrey.lomibao.a4";
    static final String CLASS_NAME = PACKAGE_NAME + ".StockServiceImpl";
    private static final String LOGTAG = "MainActivity";
    private static final boolean DEBUG = true;
    private Context activityContext = this;

    private boolean isBound;            // are we bound to our service
    private Intent  stockServiceIntent; // the intent used to start our service
//    private StockServiceImpl stockService;  // a ref to the service once we are bound
    private StockService stockService;  // a ref to the service once we are bound

    // these are for showing a list of stock prices
    private ListView stocksListView;
    private StocksListAdapter stocksListAdapter;

    private final Handler handler = new Handler();

    // these are for switcher animation
    private ViewSwitcher viewSwitcher;
    private View detailView;
    private TextView nameTextView;
    private TextView symbolTextView;
    private TextView priceTextView;
    private TextView priceDiffTextView;
    private TextView minTextView;
    private TextView maxTextView;
    private TextView avgTextView;

    private ServiceConnection serviceConnection;

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Called when the activity is first created.
        Log.d (LOGTAG, "*** onCreate(): STARTING");
        initialize();

        // Get UI references
        viewSwitcher = (ViewSwitcher) findViewById(R.id.mainActivityViewSwitcher);
        detailView = findViewById(R.id.detailView);

        nameTextView = (TextView) findViewById(R.id.textViewDetailName);
        symbolTextView = (TextView) findViewById(R.id.textViewDetailSymbol);
        priceTextView = (TextView) findViewById(R.id.textViewDetailPrice);
        priceDiffTextView = (TextView) findViewById(R.id.textViewDetailPriceDiff);
        minTextView = (TextView) findViewById(R.id.textViewMinPrice);
        maxTextView = (TextView) findViewById(R.id.textViewMaxPrice);
        avgTextView = (TextView) findViewById(R.id.textViewAvgPrice);

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
                // Use viewSwitcher to display detailView
                nameTextView.setText(listItem.getName());
                symbolTextView.setText(listItem.getSymbol());
                priceTextView.setText("$ " + String.format("%.2f", listItem.getPrice()));
                priceDiffTextView.setText("$ " + String.format("%.2f", listItem.getPriceChange()));
                if (listItem.getPriceChange() > 0) {
                    priceDiffTextView.setTextColor(Color.BLUE);
                } else if (listItem.getPriceChange() < 0){
                    priceDiffTextView.setTextColor(Color.RED);
                } else {
                    priceDiffTextView.setTextColor(Color.GRAY);
                }
                minTextView.setText("$ " + String.format("%.2f", listItem.getMin()));
                maxTextView.setText("$ " + String.format("%.2f", listItem.getMax()));
                avgTextView.setText("$ " + String.format("%.2f", listItem.getAvg()));
                viewSwitcher.showNext();
                //  Start new Activity.
                //  The child Activity should be able to display the data that was selected.
                // Use an IMPLICIT intent
//                Intent myIntent = new Intent();
//                myIntent.putExtra("listItem", listItem);
//
//                myIntent.setAction("x40241.jeffrey.lomibao.a3.intent.action.listItem");
//                if(myIntent.resolveActivity(getPackageManager())!= null) {
//                    startActivity(myIntent);
//                }
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
//        stockServiceIntent = StockServiceImpl.getServiceIntent(this.getBaseContext(), LOGTAG);
//        startService(stockServiceIntent);
        stockServiceIntent = new Intent();
        stockServiceIntent.setClassName(PACKAGE_NAME, CLASS_NAME);
        startService(stockServiceIntent);

        // Create a new service connection, then bind service
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                Log.d (LOGTAG, "*** onServiceConnected");
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.

                //  Local binding:  StockServiceImpl is in same process space.
                //  stockServiceImpl = ((StockServiceImpl.LocalBinder)service).getService();

                //  Remote binding:  StockServiceImpl is in separate process space so we
                //     get a StockService interface to access.
                stockService = StockService.Stub.asInterface(service);

                //  Once we are bound to our Service we can access it like any other object to
                //  get "services" -- i.e., call methods on it.
//            stockService.registerOnNewStockDataListener(MainActivity.this);

                // Tell the user about this for our demo.
                Toast.makeText(activityContext, R.string.service_connected,
                        Toast.LENGTH_SHORT).show();
            }

            public void onServiceDisconnected(ComponentName className) {
                Log.d (LOGTAG, "*** onServiceDisconnected");
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
//            stockService.unregisterOnNewStockDataListener(MainActivity.this);
                stockService = null;
                Toast.makeText(MainActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        };

        stockServiceIntent = new Intent();
        stockServiceIntent.setClassName(PACKAGE_NAME, CLASS_NAME);
        stockServiceIntent.putExtra("client", this.getClass().getCanonicalName());

        // Establish a connection with the service.  We use an explicit class name because we want
        // a specific service implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other applications).
        isBound = bindService(stockServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Register callback for getting stock updates from service
        IntentFilter intentFilter = new IntentFilter("StockUpdates");
//        LocalBroadcastManager.getInstance(activityContext).registerReceiver(
//                mMessageReceiver, intentFilter);
        this.registerReceiver(mMessageReceiver, intentFilter);

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
        if (!isBound) {
            if(DEBUG) Log.d(LOGTAG, "*** onRestart bindService");
            isBound = bindService(stockServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
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
     * Callback for getting stock info
     *********************************************************************************************/
    private int receiveCount;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        receiveCount++;
        if(DEBUG) Log.d(LOGTAG, "*** receiveCount = " + receiveCount);
        // Get extra data included in the Intent
        Bundle bundle = intent.getBundleExtra("StockInfoListBundle");
        List<StockInfo> stockData = (List) bundle.getParcelableArrayList("StockInfoList");
        stocksListAdapter.setList(stockData);
        stocksListAdapter.notifyDataSetChanged();
        }
    };

//    public void notifyNewStockData(final List<StockInfo> stockData) {
//        receiveCount++;
//        if(DEBUG) Log.d(LOGTAG, "*** receiveCount = " + receiveCount);
//        handler.post(new Runnable() {
//            public void run() {
//                stocksListAdapter.setList(stockData);
//                stocksListAdapter.notifyDataSetChanged();
//            }
//        });
//    }

    /**********************************************************************************************
     * Action Bar methods
     *********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    public void onActionDeleteClick(MenuItem item) {
        if(DEBUG) Log.d(LOGTAG, "Action Delete clicked.");
        Toast.makeText(activityContext, R.string.action_delete, Toast.LENGTH_SHORT).show();
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.deleteAll();
        stocksListAdapter.setList(dbHelper.getStockInfoFromCache());
        stocksListAdapter.notifyDataSetChanged();
    }

    public void onActionSettingsClick(MenuItem item) {
        if(DEBUG) Log.d(LOGTAG, "Action Settings clicked.");
        Toast.makeText(activityContext, R.string.action_settings, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if(DEBUG) Log.d(LOGTAG, "onBackPressed");
        if (viewSwitcher.getCurrentView() == detailView) {
            if(DEBUG) Log.d(LOGTAG, "viewSwitcher.showPrevious");
            viewSwitcher.showPrevious();
            return;
        }
        super.onBackPressed();
    }
}