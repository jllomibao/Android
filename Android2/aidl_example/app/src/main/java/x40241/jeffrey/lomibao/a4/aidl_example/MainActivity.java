package x40241.jeffrey.lomibao.a4.aidl_example;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import x40241.jeffrey.lomibao.a4.aidl_example.model.ContactInfo;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private static final String LOGTAG = "MainActivity";
    private static final boolean DEBUG = true;

    List<ContactInfo> cList = new ArrayList<>();

    IRemoteService mIRemoteService;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            mIRemoteService = IRemoteService.Stub.asInterface(service);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            mIRemoteService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentRemoteService = new Intent(this, RemoteService.class);
        startService(intentRemoteService);
        bindService(intentRemoteService, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void onPassDataButtonClick(final View view) {
        if(DEBUG) Log.d(LOGTAG, "onPassDataButtonClick");

        try {
            Log.d(LOGTAG, "mIRemoteService pid: " + mIRemoteService.getPid());
        } catch(RemoteException remoteExeption) {
            Log.d(LOGTAG, "remoteException!");
        }

        // Create Contact Info
        cList.add(new ContactInfo("Jeffrey", "Lomibao"));
        cList.add(new ContactInfo("Grace", "Lomibao"));
        cList.add(new ContactInfo("Andres", "Lomibao"));

        Intent intent = new Intent(MainActivity.this, ActivityB.class);
        intent.putParcelableArrayListExtra("contact", (ArrayList) cList);

//        ContactInfo ci = new ContactInfo("Jeffrey", "Lomibao");
//        Intent intent = new Intent(MainActivity.this, ActivityB.class);
//        intent.putExtra("contact", ci);

        startActivity(intent);
    }

}
