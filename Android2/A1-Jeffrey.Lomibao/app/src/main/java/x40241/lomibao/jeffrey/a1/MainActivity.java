package x40241.lomibao.jeffrey.a1;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity
    extends Activity
    implements Runnable
{
    private static final String LOGTAG = "MainActivity";
    private static final boolean DEBUG = true;
    
    private CustomListAdapter listAdapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //  Called when the activity is first created.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d (LOGTAG, "onCreate");

        listView = (ListView) this.findViewById(R.id.list_view);
        listAdapter = new CustomListAdapter(this);

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> arg0, View view, int position, long id) {
                if (DEBUG) {
                    Log.d (LOGTAG, "Item " + (position+1) + " clicked");
                    Log.d (LOGTAG, "onItemClick(): arg0="+arg0.getClass().getSimpleName());
                    Log.d (LOGTAG, "onItemClick(): arg1="+view.getClass().getSimpleName());
                }
                ListItem listItem = (ListItem) listView.getItemAtPosition(position);
                Log.d (LOGTAG, listItem.name);
                //  Start new Activity.
                //  The child Activity should be able to display the data that was selected.
                // Use an IMPLICIT intent
                Intent myIntent = new Intent();
                myIntent.putExtra("listItem", listItem);

                myIntent.setAction("x40241.jeffrey.lomibao.a1.intent.action.listItem");
                if(myIntent.resolveActivity(getPackageManager())!= null) {
                    startActivity(myIntent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        //  Called after onStart() as Activity comes to foreground.
        if (DEBUG) Log.d (LOGTAG, "onResume");
        super.onResume();
        new InitializeList().execute();
    }


    class InitializeList extends AsyncTask<ListItem, Void, List<ListItem>>
    {
        private final String LOGTAG = InitializeList.class.getSimpleName();
        private final boolean DEBUG = true;

        //  Runs on Main thread so we can manipulate the UI.
        @Override
        protected void onPreExecute() {
            //  1.  Display indeterminate progress indicator
            showDialog(INDETERMINATE_DIALOG_KEY);
        }

        //  Do all expensive operations here off the main thread.
        @Override
        protected List<ListItem> doInBackground (final ListItem...paramArrayOfParams) {
            if (DEBUG) Log.d(LOGTAG, "**** doInBackground() STARTING");

            //  2.  Load data from a file placed in the assets directory
            ImageMapper.CreateMap();
            List<ListItem> list = new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("listItems.csv")));
                String thisLine;
                while ((thisLine = br.readLine()) != null) {
                    String[] lineItems = thisLine.split(", ");
                    if (DEBUG) Log.d(LOGTAG, lineItems[0] + ", " + lineItems[1] + ", " + lineItems[2]);
                    list.add(new ListItem(Integer.parseInt(lineItems[0]), ImageMapper.getResId(lineItems[1]), lineItems[1], lineItems[2]));
                }
            } catch(Exception ex) {
                Log.d(LOGTAG, ex.getMessage());
            }

            //  3.  Delay for 3 seconds.  We wan't to see the progress indicator.
            try { Thread.sleep(3000); }
            catch (InterruptedException e) { /* ignore */ }

            return list;
        }

        //  Runs on Main thread so we can manipulate the UI.
        @Override
        protected void onPostExecute(final List<ListItem> list) {
            if (DEBUG) Log.d(LOGTAG, "onPostExecute");

            //  4.  Load the data into the listAdapter.  Hint:  Use a handler.
            listAdapter.setList(list);
            listAdapter.notifyDataSetChanged();

            //  5.  Cancel indeterminate progress indicator
            dismissDialog(INDETERMINATE_DIALOG_KEY);
        }
    }

    //**********************************************************************************************
    //  Dialog management
    //**********************************************************************************************
    private ProgressDialog progressDialog;

    protected static final int INDETERMINATE_DIALOG_KEY = 0;

    @Override
    protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case INDETERMINATE_DIALOG_KEY:
                progressDialog = new ProgressDialog(this);
                return progressDialog;
        }
        return null;
    }

    @Override
    protected void onPrepareDialog (final int id, final Dialog dialog) {
        if (DEBUG) {
            Log.d(LOGTAG, "onPrepareDialog.threadId="+ Thread.currentThread().getId());
            Log.d(LOGTAG, "onPrepareDialog.id="+id);
            Log.d(LOGTAG, "onPrepareDialog.dialog="+dialog);
        }
        if (dialog instanceof ProgressDialog)
        {
            ((ProgressDialog)dialog).setMessage(getResources().getText(R.string.please_wait_label));
            ((ProgressDialog)dialog).setIndeterminate(true);
            ((ProgressDialog)dialog).setCancelable(true);
        }
    }

    public void run() {
        List<ListItem> list = null;
        //  1.  Display indeterminate progress indicator
        //  2.  Load data from a file placed in the assets directory
        //  3.  Delay for 3 seconds.  We wan't to see the progress indicator.
            try { Thread.sleep(3000); }
            catch (InterruptedException e) { /* ignore */ }
        //  4.  Load the data into the listAdapter.  Hint:  Use a handler.
            listAdapter.setList(list);
        //  5.  Cancel indeterminate progress indicator
    }
    
}