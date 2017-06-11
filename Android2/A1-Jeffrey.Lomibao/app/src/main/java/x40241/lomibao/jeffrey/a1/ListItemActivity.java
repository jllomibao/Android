package x40241.lomibao.jeffrey.a1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jllom on 5/24/2017.
 */

public class ListItemActivity extends Activity {
    private ListItem listItem;
    private static final String LOGTAG = "ListItemActivity";
    private static final boolean DEBUG = true;
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);
        Log.d (LOGTAG, "onCreate");

        holder = new ViewHolder();
        holder.thumbnail = (ImageView) this.findViewById(R.id.thumbnail);
        holder.textView = (TextView) findViewById(R.id.text_view);
        holder.button  = (Button) findViewById(R.id.button);
        initialize();

    }

    //  Encapsulate non-ui related initialization here.
    private void initialize() {
        final Intent callingIntent = this.getIntent();  // Get the Intent that started us.

        listItem = (ListItem) callingIntent.getSerializableExtra("listItem");
        if (listItem == null) return;

        Log.d (LOGTAG, listItem.name);


        holder.thumbnail.setImageResource(listItem.imageId);
        holder.textView.setText(listItem.name);
        holder.button.setText(""+listItem.color);

    }

}
