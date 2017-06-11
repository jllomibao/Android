package x40240.jeffrey.lomibao.a5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onConfigureButtonClick(final View view) {
        // Perform action on click
        // Use an IMPLICIT intent
        Intent myIntent = new Intent();
        myIntent.setAction("x40240.jeffrey.lomibao.a5.intent.action.configure");
        if(myIntent.resolveActivity(getPackageManager())!= null) {
            startActivity(myIntent);
        }
    }

    public void onUserManualButtonClick(final View view) {
        // Perform action on click
        // Use an IMPLICIT intent
        Intent myIntent = new Intent();
        myIntent.setAction("x40240.jeffrey.lomibao.a5.intent.action.user_manual");
        if(myIntent.resolveActivity(getPackageManager())!= null) {
            startActivity(myIntent);
        }
    }
}
