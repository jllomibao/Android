package x40241.jeffrey.lomibao.a4.aidl_example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import x40241.jeffrey.lomibao.a4.aidl_example.model.ContactInfo;

/**
 * Created by jllom on 6/24/2017.
 */

public class ActivityB extends Activity {

//    private TextView tv;
    private ListView listView;
    private CustomListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);

//        tv = (TextView)findViewById(R.id.tvMessage);
        listView = (ListView) this.findViewById(R.id.list_view);
        listAdapter = new CustomListAdapter(this);
        listView.setAdapter(listAdapter);

        Bundle data = getIntent().getExtras();

        List<ContactInfo> ciList = (List) data.getSerializable("contact");

        listAdapter.setList(ciList);
        listAdapter.notifyDataSetChanged();


//        ContactInfo ci = (ContactInfo) data.getParcelable("contact");
//        tv.setText(ci.getIdx() + ": " + ci.getName() + " " + ci.getSurname());
    }
}
