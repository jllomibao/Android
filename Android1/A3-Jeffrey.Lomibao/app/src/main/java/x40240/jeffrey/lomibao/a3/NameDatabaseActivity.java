package x40240.jeffrey.lomibao.a3;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import x40240.jeffrey.lomibao.a3.db.DBHelper;
import x40240.jeffrey.lomibao.a3.model.PersonInfo;

import static x40240.jeffrey.lomibao.a3.db.DBHelper.KEY_EDUCATION;
import static x40240.jeffrey.lomibao.a3.db.DBHelper.KEY_FIRSTNAME;
import static x40240.jeffrey.lomibao.a3.db.DBHelper.KEY_GENDER;
import static x40240.jeffrey.lomibao.a3.db.DBHelper.KEY_LASTNAME;
import static x40240.jeffrey.lomibao.a3.db.DBHelper.KEY_PETS;

public final class NameDatabaseActivity
    extends ListActivity
{
    private static final String LOGTAG = NameDatabaseActivity.class.getSimpleName();
    private static final boolean DEBUG = true;
    
	private PersonInfo personInfo;  // data passed to us, maybe
    SimpleCursorAdapter simpleCursorAdapter;

    private ArrayList<PersonInfo> list = new ArrayList<PersonInfo>();
	
	private Context activityContext = this;
	
    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intitialize();
        DBHelper dbHelper = new DBHelper(activityContext);
        String[] columns = new String[] { KEY_LASTNAME, KEY_FIRSTNAME, KEY_GENDER, KEY_EDUCATION, KEY_PETS };
        int[] resources = new int[] { R.id.lastname_text, R.id.firstname_text, R.id.gender_text, R.id.education_text, R.id.pets_text };
        setListAdapter(simpleCursorAdapter = new PersonInfoSimpleCursorAdapter(activityContext, R.layout.list_item, dbHelper.selectAllCursor(), columns, resources));
        dbHelper.close();
    }

    //  Encapsulate non-ui related initialization here.
    private void intitialize() {
        final Intent callingIntent = this.getIntent();  // Get the Intent that started us.
        
        //****************************************************************************************
        //  If we are being passed a Serialized POJO:
        //
        personInfo = (PersonInfo)callingIntent.getSerializableExtra("personInfo");
        if (personInfo == null) return;
        
        //****************************************************************************************
        //  If we are being called with each PersonInfo field.
        //
//        final String firstname = callingIntent.getStringExtra("firstname");
//        if (firstname == null) return;
//
//        final String lastname = callingIntent.getStringExtra("lastname");
//        int gender = callingIntent.getIntExtra("gender", -1);
//
//        personInfo = new PersonInfo();
//        personInfo.setFirstname(firstname);
//        personInfo.setLastname(lastname);
//        personInfo.setGender(gender);
    }

    //**********************************************************************************************
    //  Lifecycle Methods
    //  http://developer.android.com/reference/android/app/Activity.html
    //**********************************************************************************************
    @Override
    public void onStart() {
        //  Called after onCreate() OR onRestart()
        //  Called after onStop() but process has not been killed.
        if (DEBUG) Log.d (LOGTAG, "onRestart");
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
        new UpdateDBTask().execute (personInfo);
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

    // http://developer.android.com/reference/android/os/AsyncTask.html
    class UpdateDBTask
            extends AsyncTask<PersonInfo, Void, List<PersonInfo>>
    {
        private final String LOGTAG = UpdateDBTask.class.getSimpleName();
        private final boolean DEBUG = true;

        //  Runs on Main thread so we can manipulate the UI.
        @Override
        protected void onPreExecute() {
            showDialog(INDETERMINATE_DIALOG_KEY);
        }

        //  Do all expensive operations here off the main thread.
        @Override
        protected List<PersonInfo> doInBackground (final PersonInfo...paramArrayOfParams) {
            if (DEBUG) Log.d(LOGTAG, "**** doInBackground() STARTING");

            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            final DBHelper dbHelper = new DBHelper(activityContext);
            final PersonInfo personInfo = paramArrayOfParams[0];

            if (personInfo != null) dbHelper.insert(personInfo);  //  Do the insert.
            List<PersonInfo> list = dbHelper.selectAll();
            dbHelper.close();
            return list;
        }

        //  Runs on Main thread so we can manipulate the UI.
        @Override
        protected void onPostExecute(final List<PersonInfo> list) {
            if (DEBUG) Log.d(LOGTAG, "onPostExecute");
            DBHelper dbHelper = new DBHelper(activityContext);
            simpleCursorAdapter.changeCursor(dbHelper.selectAllCursor());
            dbHelper.close();
            dismissDialog(INDETERMINATE_DIALOG_KEY);
        }
    }

    private class PersonInfoSimpleCursorAdapter extends SimpleCursorAdapter {

        private Context context;
        private int layout;
        private Cursor cursor;
        private final LayoutInflater inflater;

        public PersonInfoSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context,layout,c,from,to);
            this.layout = layout;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.cursor = c;
        }

        @Override
        public View newView (Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            TextView textViewGender = (TextView)view.findViewById(R.id.gender_text);
            int genderIndex = cursor.getColumnIndexOrThrow(KEY_GENDER);
            switch(cursor.getInt(genderIndex)) {
                case 1: textViewGender.setText(getString(R.string.male_label)); break;
                case 2: textViewGender.setText(getString(R.string.female_label)); break;
                default: textViewGender.setText(getString(R.string.unknown_label)); break;
            }

            TextView textViewEducation = (TextView)view.findViewById(R.id.education_text);
            int educationIndex = cursor.getColumnIndexOrThrow(KEY_GENDER);
            switch(cursor.getInt(educationIndex)) {
                case 1: textViewEducation.setText(getString(R.string.associate_label)); break;
                case 2: textViewEducation.setText(getString(R.string.bachelor_label)); break;
                case 3: textViewEducation.setText(getString(R.string.master_label)); break;
                case 4: textViewEducation.setText(getString(R.string.doctorate_label)); break;
                default: textViewEducation.setText(getString(R.string.highschool_label)); break;
            }

        }

    }

}
