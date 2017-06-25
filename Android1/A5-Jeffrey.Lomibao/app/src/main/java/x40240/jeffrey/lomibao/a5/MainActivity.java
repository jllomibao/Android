package x40240.jeffrey.lomibao.a5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final boolean DEBUG = true;
    private static final String LOGTAG = ConfigureActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (DEBUG) Log.d (LOGTAG, "OnCreate");
    }

    public void onConfigureButtonClick(final View view) {
        // Perform action on click
        if (DEBUG) Log.d (LOGTAG, "onConfigureButtonClick");
        // Use an IMPLICIT intent
        Intent myIntent = new Intent();
        myIntent.setAction("x40240.jeffrey.lomibao.a5.intent.action.configure");
        if(myIntent.resolveActivity(getPackageManager())!= null) {
            startActivity(myIntent);
        }
    }

    public void onUserManualButtonClick(final View view) {
        // Perform action on click
        if (DEBUG) Log.d (LOGTAG, "onUserManualButtonClick");
        // Use an IMPLICIT intent
        Intent myIntent = new Intent();
        myIntent.setAction("x40240.jeffrey.lomibao.a5.intent.action.user_manual");
        if(myIntent.resolveActivity(getPackageManager())!= null) {
            startActivity(myIntent);
        }
    }
}
