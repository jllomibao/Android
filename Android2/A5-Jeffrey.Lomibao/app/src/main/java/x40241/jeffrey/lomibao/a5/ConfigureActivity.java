package x40241.jeffrey.lomibao.a5;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.List;

import x40241.jeffrey.lomibao.a5.db.DBHelper;
import x40241.jeffrey.lomibao.a5.model.DeviceInfo;

/**
 * Created by Jefflom on 5/10/2017.
 */

public class ConfigureActivity extends AppCompatActivity {
    private static final boolean DEBUG = true;
    private static final String LOGTAG = ConfigureActivity.class.getSimpleName();
    private Context activityContext = this;
    private DeviceInfo deviceInfo;
    SimpleCursorAdapter simpleCursorAdapter;

    private EditText editTextTag;
    private EditText editTextLRV;
    private EditText editTextURV;
    private Spinner spinnerTempComp;
    private EditText editTextManualTemp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        // Get UI resources
        editTextTag = (EditText) findViewById(R.id.editTextTag);
        editTextLRV = (EditText) findViewById(R.id.editTextLRV);
        editTextURV = (EditText) findViewById(R.id.editTextURV);
        spinnerTempComp = (Spinner) findViewById(R.id.spinnerTempComp);
        editTextManualTemp = (EditText) findViewById(R.id.editTextManualTemp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // Temp Comp Spinner items
        ArrayAdapter<CharSequence> adapterTempComp = ArrayAdapter.createFromResource(
                this, R.array.items_temp_comp, android.R.layout.simple_spinner_item);
        adapterTempComp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTempComp.setAdapter(adapterTempComp);
        spinnerTempComp.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        Log.d(LOGTAG, "Spinner: position= "+ position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.d(LOGTAG, "Spinner: unselected");
                    }
                });
    }

    //**********************************************************************************************
    //  Lifecycle Methods
    //  http://developer.android.com/reference/android/app/Activity.html
    //**********************************************************************************************
    @Override
    public void onStart() {
        //  Called after onCreate() OR onRestart()
        //  Called after onStop() but process has not been killed.
        if (DEBUG) Log.d (LOGTAG, "onStart");
        super.onStart();
    }

    @Override
    public void onRestart() {
        //  Called after onStop() but process has not been killed.
        if (DEBUG) Log.d (LOGTAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        //  Called after onStart() as Activity comes to foreground.
        if (DEBUG) Log.d (LOGTAG, "onResume");
        super.onResume();
        new ReadDeviceInfoDBTask().execute();
    }

    @Override
    public void onPause() {
        //  Called when Activity is placed in background
        if (DEBUG) Log.d (LOGTAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        //  The Activity is no longer visible
        if (DEBUG) Log.d (LOGTAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        //  The Activity is finishing or being destroyed by the system
        if (DEBUG) Log.d (LOGTAG, "onDestroy");
        super.onDestroy();
    }


    public void onConfigureSaveButtonClick(final View view) {
        // Perform action on click
        deviceInfo.setTag(editTextTag.getText().toString());
        deviceInfo.setLowerRangeValue(Double.parseDouble(editTextLRV.getText().toString()));
        deviceInfo.setUpperRangeValue(Double.parseDouble(editTextURV.getText().toString()));
        deviceInfo.setTemperatureCompensation((spinnerTempComp.getSelectedItemPosition() == 0) ? DeviceInfo.TemperatureCompensationEnum.TC_AUTO: DeviceInfo.TemperatureCompensationEnum.TC_MANUAL);
        deviceInfo.setManualTemperature(Double.parseDouble(editTextManualTemp.getText().toString()));

        // update database contents
        new UpdateDeviceInfoDBTask().execute(deviceInfo);
    }

    // http://developer.android.com/reference/android/os/AsyncTask.html

    class ReadDeviceInfoDBTask
            extends AsyncTask<DeviceInfo, Void, List<DeviceInfo>>
    {
        private final String LOGTAG = ReadDeviceInfoDBTask.class.getSimpleName();

        //  Runs on Main thread so we can manipulate the UI.
        @Override
        protected void onPreExecute() {
            if (DEBUG) Log.d(LOGTAG, "onPreExecute");
            progressBar.setVisibility(View.VISIBLE);
        }

        //  Do all expensive operations here off the main thread.
        @Override
        protected List<DeviceInfo> doInBackground (final DeviceInfo...paramArrayOfParams) {
            if (DEBUG) Log.d(LOGTAG, "**** doInBackground() STARTING");

            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            // Get database contents
            DBHelper dbHelper = new DBHelper(activityContext);
            List<DeviceInfo> deviceInfoList = dbHelper.selectAll();
            Log.d(LOGTAG, "Device Info list size = " + deviceInfoList.size());
            if(deviceInfoList.size() >= 1) {
                deviceInfo = deviceInfoList.get(0);
            } else {
                deviceInfo = new DeviceInfo();
                dbHelper.insert(deviceInfo);
            }
            dbHelper.close();

            return deviceInfoList;
        }

        //  Runs on Main thread so we can manipulate the UI.
        @Override
        protected void onPostExecute(final List<DeviceInfo> list) {
            if (DEBUG) Log.d(LOGTAG, "onPostExecute");
            editTextTag.setText(deviceInfo.getTag());
            editTextLRV.setText("" + deviceInfo.getLowerRangeValue());
            editTextURV.setText("" + deviceInfo.getUpperRangeValue());
            editTextManualTemp.setText("" + deviceInfo.getManualTemperature());
            spinnerTempComp.setSelection(deviceInfo.getTemperatureCompensation().ordinal());
            progressBar.setVisibility(View.GONE);
        }
    }

    class UpdateDeviceInfoDBTask
            extends AsyncTask<DeviceInfo, Void, DeviceInfo>
    {
        private final String LOGTAG = ReadDeviceInfoDBTask.class.getSimpleName();

        //  Runs on Main thread so we can manipulate the UI.
        @Override
        protected void onPreExecute() {
            if (DEBUG) Log.d(LOGTAG, "onPreExecute");
            progressBar.setVisibility(View.VISIBLE);
        }

        //  Do all expensive operations here off the main thread.
        @Override
        protected DeviceInfo doInBackground (final DeviceInfo...paramArrayOfParams) {
            if (DEBUG) Log.d(LOGTAG, "**** doInBackground() STARTING");

            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            // Get database contents
            DBHelper dbHelper = new DBHelper(activityContext);
            final DeviceInfo deviceInfo = paramArrayOfParams[0];
            dbHelper.update(deviceInfo);
            dbHelper.close();

            return deviceInfo;
        }

        //  Runs on Main thread so we can manipulate the UI.
        @Override
        protected void onPostExecute(final DeviceInfo deviceInfo) {
            if (DEBUG) Log.d(LOGTAG, "onPostExecute");
            progressBar.setVisibility(View.GONE);
            // close activity
            ConfigureActivity.this.finish();
        }
    }

}
