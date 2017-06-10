package x40241.jeffrey.lomibao.a3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import x40241.jeffrey.lomibao.a3.model.StockInfo;

/**
 * Created by jllom on 5/23/2017.
 */

public class StocksListAdapter extends BaseAdapter {
    private static final String LOGTAG = "StockListAdapter";
    private static final boolean DEBUG = false;

    private Context          context;
    private List<StockInfo>  list;
    private LayoutInflater   layoutInflater;

    StocksListAdapter(Context context) {
        this.context = context;
    }

    public void setList (List<StockInfo> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return ((list == null) ? 0 : list.size());
    }

    @Override
    public StockInfo getItem (int position) {
        //  In theory we should not be called if getCount() returned 0;
        return list.get(position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        if (DEBUG) {
            Log.d(LOGTAG, "getView.position="+position);
            Log.d(LOGTAG, "getView.convertView="+convertView);
        }
        if (list == null) {
            //  In theory it should not happen but handle this in some graceful way.
            //  Returning null will not produce graceful results.
            return null;
        }
        // You can find this ViewHolder idiom described in detail in this talk:
        //      http://www.youtube.com/watch?v=N6YdwzAvwOA&feature=related
        ViewHolder holder = null;

        if (convertView != null)
            holder = (ViewHolder) convertView.getTag();
        if (holder == null) // not the right view
            convertView = null;
        if (convertView == null) {
            convertView = getLayoutInflator().inflate(R.layout.stock_item, null);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.textViewName);
            holder.symbolTextView = (TextView) convertView.findViewById(R.id.textViewSymbol);
            holder.priceTextView = (TextView) convertView.findViewById(R.id.textViewPrice);
            holder.priceDiffTextView = (TextView) convertView.findViewById(R.id.textViewPriceDiff);
            convertView.setTag(holder);
        }
        else holder = (ViewHolder) convertView.getTag();

        StockInfo item = list.get(position);
        holder.nameTextView.setText(item.getName());
        holder.symbolTextView.setText(item.getSymbol());
        holder.priceTextView.setText("$ " + String.format("%.2f", item.getPrice()));
        holder.priceDiffTextView.setText("$ " + String.format("%.2f", item.getPriceChange()));
        return convertView;
    }

    private LayoutInflater getLayoutInflator() {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater)
                    this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return layoutInflater;
    }

    public class ViewHolder {
        TextView nameTextView;
        TextView symbolTextView;
        TextView priceTextView;
        TextView priceDiffTextView;
    }
}
