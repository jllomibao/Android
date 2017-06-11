package x40241.jeffrey.lomibao.a2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import x40241.jeffrey.lomibao.a2.model.StockInfo;

public class MainActivity extends AppCompatActivity {
    private static final String LOGTAG = "MainActivity";
    private static final boolean DEBUG = true;
    private Context activityContext = this;
    private StockServiceImpl mBoundService;
    private boolean mIsBound = false;
    private ServiceConnection mConnection;
    private Intent intentStockService;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get UI references
        tvMessage = (TextView) findViewById(R.id.tv_message);

        // Start Stock service
        intentStockService = new Intent(activityContext, StockServiceImpl.class);
        startService(intentStockService);

        // Create a new service connection, then bind service
        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                mBoundService = ((StockServiceImpl.LocalBinder)service).getService();

                // Tell the user about this for our demo.
                Toast.makeText(activityContext, R.string.local_service_connected,
                        Toast.LENGTH_SHORT).show();
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                mBoundService = null;
                Toast.makeText(activityContext, R.string.local_service_disconnected,
                        Toast.LENGTH_SHORT).show();
            }
        };
        doBindService();

        // Register callback for getting stock updates from service
        LocalBroadcastManager.getInstance(activityContext).registerReceiver(
                mMessageReceiver, new IntentFilter("StockUpdates"));
    }

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

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(intentStockService, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    // This is the callback for getting stock info
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Bundle bundle = intent.getBundleExtra("StockInfoListBundle");
            List<StockInfo> stockInfoList = (List<StockInfo>) bundle.getSerializable("StockInfoList");
            if (DEBUG) tvMessage.setText("size = " + stockInfoList.size() + "\nsequence = " + stockInfoList.get(0).getSequence());
            for (int i = 0; i < stockInfoList.size(); i++) {
                StockInfo stockInfo = stockInfoList.get(i);
                Log.d(LOGTAG, "StockInfo: " + stockInfo.getName());
            }
        }
    };
}
