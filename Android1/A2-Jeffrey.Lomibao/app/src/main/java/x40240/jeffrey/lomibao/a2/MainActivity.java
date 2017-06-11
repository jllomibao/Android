package x40240.jeffrey.lomibao.a2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import x40240.jeffrey.lomibao.a2.model.PersonInfo;

import static x40240.jeffrey.lomibao.a2.model.PersonInfo.Pets.BIRD;
import static x40240.jeffrey.lomibao.a2.model.PersonInfo.Pets.CAT;
import static x40240.jeffrey.lomibao.a2.model.PersonInfo.Pets.DOG;
import static x40240.jeffrey.lomibao.a2.model.PersonInfo.Pets.FISH;

public class MainActivity
    extends Activity
{
    private static final String LOGTAG = MainActivity.class.getSimpleName();
    
    private EditText firstnameEdit;
    private EditText lastnameEdit;
    private RadioButton maleButton;
    private RadioButton femaleButton;
    private Spinner educationSpinner;
    private CheckBox dogCheckBox, catCheckBox, birdCheckBox, fishCheckBox;


    private Context activityContext = this;  // our execution context
    
    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Not used, but demonstrates how we can track clicks on the EditText field
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick (final View v) {
                Log.d(LOGTAG, "Text: Got Click!");
            }
        };
        
        //  Demonstrates how we can monitor/respond to each key typed into the EditText field
        View.OnKeyListener onKeyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey (View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            Log.d(LOGTAG, view.getClass().getSimpleName()+": Key event!");
                            return true;
                    }
                }
                return false;
            }
        };

        // Education Spinner items
        Spinner spinnerEducation = (Spinner) findViewById(R.id.spinner_education);
        ArrayAdapter<CharSequence> adapterEducation = ArrayAdapter.createFromResource(
                this, R.array.education, android.R.layout.simple_spinner_item);
        adapterEducation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEducation.setAdapter(adapterEducation);
        spinnerEducation.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        Log.d(LOGTAG, "Spinner: position= "+ position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.d(LOGTAG, "Spinner: unselected");
                    }
                });

        firstnameEdit = (EditText) findViewById(R.id.firstname_edit);
        firstnameEdit.setOnClickListener(onClickListener);
        firstnameEdit.setOnKeyListener(onKeyListener);
        
        lastnameEdit = (EditText) findViewById(R.id.lastname_edit);
        lastnameEdit.setOnClickListener(onClickListener);
        lastnameEdit.setOnKeyListener(onKeyListener);
        
        maleButton = (RadioButton) this.findViewById(R.id.male_radio);
        femaleButton = (RadioButton) this.findViewById(R.id.female_radio);

        educationSpinner = (Spinner) findViewById(R.id.spinner_education);
        dogCheckBox = (CheckBox) findViewById(R.id.checkBox_dog);
        catCheckBox = (CheckBox) findViewById(R.id.checkBox_cat);
        birdCheckBox = (CheckBox) findViewById(R.id.checkBox_bird);
        fishCheckBox = (CheckBox) findViewById(R.id.checkBox_fish);
    }
    
    @Override
    protected void onResume() {
        //  Called after onStart() as Activity comes to foreground.
        super.onResume();
        clearData();
    }
    
    private void clearData() {
        firstnameEdit.setText(null);
        lastnameEdit.setText(null);
        maleButton.setChecked(false);
        femaleButton.setChecked(false);
        educationSpinner.setSelection(0);
        dogCheckBox.setChecked(false);
        catCheckBox.setChecked(false);
        birdCheckBox.setChecked(false);
        fishCheckBox.setChecked(false);
    }
    
    //  This is the OnClickListener that is set in the XML layout as
    //     android:onClick="onOKButtonClick"
    public void onOKButtonClick(final View view) {
        // Perform action on click
        final PersonInfo personInfo = new PersonInfo();
        personInfo.setFirstname(firstnameEdit.getText().toString());
        personInfo.setLastname(lastnameEdit.getText().toString());
        
        if (maleButton.isChecked()) personInfo.setGender(PersonInfo.Gender.MALE);
        if (femaleButton.isChecked()) personInfo.setGender(PersonInfo.Gender.FEMALE);

        switch(educationSpinner.getSelectedItemPosition()) {
            case 0: personInfo.setEducation(PersonInfo.Education.HIGHSCHOOL); break;
            case 1: personInfo.setEducation(PersonInfo.Education.ASSOCIATE); break;
            case 2: personInfo.setEducation(PersonInfo.Education.BACHELOR); break;
            case 3: personInfo.setEducation(PersonInfo.Education.MASTER); break;
            case 4: personInfo.setEducation(PersonInfo.Education.DOCTORATE); break;
        }
        Log.d(LOGTAG, "educationSpinner="+educationSpinner.getSelectedItem());


        if(dogCheckBox.isChecked()) personInfo.addPet(DOG);
        if(catCheckBox.isChecked()) personInfo.addPet(CAT);
        if(birdCheckBox.isChecked()) personInfo.addPet(BIRD);
        if(fishCheckBox.isChecked()) personInfo.addPet(FISH);

        Log.d(LOGTAG, "firstname="+personInfo.getFirstname());
        Log.d(LOGTAG, "lastname="+personInfo.getLastname());
        Log.d(LOGTAG, "gender="+personInfo.getGender());
        Log.d(LOGTAG, "education="+personInfo.getEducation());

    //        final Intent myIntent = new Intent();
    //
    //        // This is an EXPLICIT intent -- i.e., we specify the target Activity by class name.
    //         myIntent.setClass (activityContext, NameListActivity.class);
    //
    //        // We can pass the PersonInfo in its entirety using the Intent.
    //        // This specific mechanism requires the POJO (i.e., PersonInfo) implement the
    //        // Serializable marker interface.
    //         myIntent.putExtra("personInfo", personInfo);
    //
    //         startActivity(myIntent);

        // This is an attempt to do IMPLICIT intent
        Intent myIntent = new Intent();
        //myIntent.setAction(Intent.ACTION_VIEW);
        //myIntent.setAction(x40240.jeffrey.lomibao.a2.intent.action.ACTION_VIEW);
        myIntent.setAction("x40240.jeffrey.lomibao.a2.intent.action.ACTION_VIEW");
        myIntent.putExtra("personInfo", personInfo);
        if(myIntent.resolveActivity(getPackageManager())!= null) {
            startActivity(myIntent);
        }

    }
    
    //  This is the OnClickListener that is set in the XML layout as 
    //     android:onClick="onClearButtonClick"
    public void onClearButtonClick(final View view) {
    	clearData();
    }
    
    //  This is the OnClickListener that is set in the XML layout as 
    //     android:onClick="onClearButtonClick"
    public void onListButtonClick(final View view) {
        Intent myIntent = new Intent();
        startActivity(new Intent().setClass (activityContext, NameListActivity.class));
    }
}