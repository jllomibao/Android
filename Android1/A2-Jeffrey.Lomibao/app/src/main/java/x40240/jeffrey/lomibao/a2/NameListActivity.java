package x40240.jeffrey.lomibao.a2;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import x40240.jeffrey.lomibao.a2.model.PersonInfo;

public final class NameListActivity
    extends ListActivity
{
    private static final String LOGTAG = NameListActivity.class.getSimpleName();
    private static final boolean DEBUG = true;
    
	private PersonInfo personInfo;  // data passed to us
	private PersonInfoListAdapter listAdapter;

    private ArrayList<PersonInfo> list = new ArrayList<PersonInfo>();
	
	private Context activityContext = this;
	
    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intitialize();
        setListAdapter(listAdapter = new PersonInfoListAdapter(activityContext));

        listAdapter.setList(list);
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
//        String firstname = callingIntent.getStringExtra("firstname");
//        if (firstname == null) return;
//        String lastname = callingIntent.getStringExtra("lastname");
//        int gender = callingIntent.getIntExtra("gender", -1);
//
//        PersonInfo personInfo = new PersonInfo();
//        personInfo.setFirstname(firstname);
//        personInfo.setLastname(lastname);
//        personInfo.setGender(gender);

        list.add(personInfo);
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
    
    private class PersonInfoListAdapter
        extends BaseAdapter
    {
        //  Remember our context so we can use it when constructing views.
        private Context context;
        
        private List<PersonInfo> list;
        private LayoutInflater layoutInflater;

        public PersonInfoListAdapter(final Context context) {
            this.context = context;
            layoutInflater = (LayoutInflater)
                    this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * The number of items in the list.
         * 
         * @see android.widget.ListAdapter#getCount()
         */
        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        /**
         * @see android.widget.ListAdapter#getItem(int)
         */
        @Override
        public Object getItem (int position) {
            return list == null ? null : list.get(position);
        }

        /**
         * Use the array index as a unique id.
         * 
         * @see android.widget.ListAdapter#getItemId(int)
         */
        @Override
        public long getItemId (int position) {
            return position;
        }

        public void setList (List<PersonInfo> list) {
        	this.list = list;
        	this.notifyDataSetChanged();
        }
        
        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView (final int position, final View convertView, final ViewGroup parent)
        {
            ViewGroup listItem;
            if (convertView == null) {
            	listItem = (ViewGroup) layoutInflater.inflate(R.layout.list_item, null);
            }
            else {
            	listItem = (ViewGroup) convertView;
            }

            final TextView lastnameText = (TextView) listItem.findViewById(R.id.lastname_text);
            final TextView firstnameText = (TextView) listItem.findViewById(R.id.firstname_text);
            final TextView genderText = (TextView) listItem.findViewById(R.id.gender_text);
            final TextView educationText = (TextView) listItem.findViewById(R.id.education_text);
            final TextView petsText = (TextView) listItem.findViewById(R.id.pets_text);

            final PersonInfo personInfo = list.get(position);
            lastnameText.setText(personInfo.getLastname());
            firstnameText.setText(personInfo.getFirstname());

            final Resources resources = context.getResources();

            String genderName;
            switch (personInfo.getGender()) {
            case MALE:
            	genderName = resources.getString(R.string.male_label);
                break;
            case FEMALE:
            	genderName = resources.getString(R.string.female_label);
                break;
            default:
            case UNKNOWN:
                genderName = resources.getString(R.string.unknown_label);
                break;
            }
            genderText.setText(genderName);

            String educationString;
            switch(personInfo.getEducation()) {
                case HIGHSCHOOL: educationString = resources.getString(R.string.highschool_label); break;
                case ASSOCIATE:  educationString = resources.getString(R.string.associate_label); break;
                case BACHELOR:   educationString = resources.getString(R.string.bachelor_label); break;
                case MASTER:     educationString = resources.getString(R.string.master_label); break;
                case DOCTORATE:  educationString = resources.getString(R.string.doctorate_label); break;
                default:         educationString = resources.getString(R.string.unknown_label);
            }
            educationText.setText(educationString);

            if(personInfo.pets.isEmpty()) {
                petsText.setText(resources.getString(R.string.none_label));
            } else {
                StringBuilder petsString = new StringBuilder();
                PersonInfo.Pets[] petsArray = personInfo.pets.toArray(new PersonInfo.Pets[personInfo.pets.size()]);
                for(int i = 0; i < petsArray.length; i++) {
                    switch(petsArray[i]) {
                        case DOG: petsString.append(resources.getString(R.string.dog_label)); break;
                        case CAT: petsString.append(resources.getString(R.string.cat_label)); break;
                        case BIRD: petsString.append(resources.getString(R.string.bird_label)); break;
                        case FISH: petsString.append(resources.getString(R.string.fish_label)); break;
                    }
                    if(i != (petsArray.length - 1)) {
                        petsString.append(", ");
                    }
                }
                petsText.setText(petsString);
            }

            return listItem;
        }
    }
}
