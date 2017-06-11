package x40240.jeffrey.lomibao.a5;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Jefflom on 5/10/2017.
 */

public class ConfigureActivity extends AppCompatActivity {
    private static final String LOGTAG = ConfigureActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        // Education Spinner items
        Spinner spinnerTempComp = (Spinner) findViewById(R.id.spinnerTempComp);
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

}
