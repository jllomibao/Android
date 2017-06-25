package x40241.jeffrey.lomibao.a4.aidl_example;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import x40241.jeffrey.lomibao.a4.aidl_example.model.ContactInfo;

public class RemoteService extends Service {
    static private final String LOGTAG = "Remote Service";
    List<ContactInfo> cList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOGTAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOGTAG, "onBind");
        // Return the interface
        return mBinder;
    }

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        public int getPid(){
            int processID = android.os.Process.myPid();
            Log.d(LOGTAG, "mBinder pid: " + processID);
            return processID;
        }

        public List<ContactInfo> getContactInfoList() {
            cList.add(new ContactInfo("Jeffrey", "Lomibao"));
            cList.add(new ContactInfo("Grace", "Lomibao"));
            cList.add(new ContactInfo("Andres", "Lomibao"));

            return cList;
        }

        public void basicTypes(int anInt, long aLong, boolean aBoolean,
                               float aFloat, double aDouble, String aString) {
            // Does nothing
        }
    };
}
