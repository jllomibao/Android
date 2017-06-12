package x40241.jeffrey.lomibao.a3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import x40241.jeffrey.lomibao.a3.model.StockInfo;

/**
 * Created by jllom on 5/24/2017.
 */

public class StockDetailActivity extends Activity {
    private StockInfo listItem;
    private static final String LOGTAG = "StockDetailActivity";
    private static final boolean DEBUG = true;

    private TextView nameTextView;
    private TextView symbolTextView;
    private TextView priceTextView;
    private TextView priceDiffTextView;
    private TextView minTextView;
    private TextView maxTextView;
    private TextView avgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail);
        Log.d (LOGTAG, "onCreate");

        nameTextView = (TextView) findViewById(R.id.textViewDetailName);
        symbolTextView = (TextView) findViewById(R.id.textViewDetailSymbol);
        priceTextView = (TextView) findViewById(R.id.textViewDetailPrice);
        priceDiffTextView = (TextView) findViewById(R.id.textViewDetailPriceDiff);
        minTextView = (TextView) findViewById(R.id.textViewMinPrice);
        maxTextView = (TextView) findViewById(R.id.textViewMaxPrice);
        avgTextView = (TextView) findViewById(R.id.textViewAvgPrice);
        initialize();

    }

    //  Encapsulate non-ui related initialization here.
    private void initialize() {
        final Intent callingIntent = this.getIntent();  // Get the Intent that started us.

        listItem = (StockInfo) callingIntent.getSerializableExtra("listItem");
        if (listItem == null) return;

        if(DEBUG) Log.d (LOGTAG, listItem.getName());

        nameTextView.setText(listItem.getName());
        symbolTextView.setText(listItem.getSymbol());
        priceTextView.setText("$ " + String.format("%.2f", listItem.getPrice()));
        priceDiffTextView.setText("$ " + String.format("%.2f", listItem.getPriceChange()));
        if (listItem.getPriceChange() > 0) {
            priceDiffTextView.setTextColor(Color.BLUE);
        } else if (listItem.getPriceChange() < 0){
            priceDiffTextView.setTextColor(Color.RED);
        } else {
            priceDiffTextView.setTextColor(Color.GRAY);
        }
        minTextView.setText("$ " + String.format("%.2f", listItem.getMin()));
        maxTextView.setText("$ " + String.format("%.2f", listItem.getMax()));
        avgTextView.setText("$ " + String.format("%.2f", listItem.getAvg()));

    }

}
