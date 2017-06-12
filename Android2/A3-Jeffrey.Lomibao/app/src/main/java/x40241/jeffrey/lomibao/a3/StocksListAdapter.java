package x40241.jeffrey.lomibao.a3;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.List;

import x40241.jeffrey.lomibao.a3.model.StockInfo;

import static x40241.jeffrey.lomibao.a3.R.id.textSwitcherPrice;
import static x40241.jeffrey.lomibao.a3.R.id.textSwitcherPriceDiff;

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
            Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

            holder.symbolTextSwitcher = (TextSwitcher) convertView.findViewById(R.id.textSwitcherSymbol);
            holder.nameTextSwitcher = (TextSwitcher) convertView.findViewById(R.id.textSwitcherName);
            holder.priceTextSwitcher = (TextSwitcher) convertView.findViewById(textSwitcherPrice);
            holder.priceDiffTextSwitcher = (TextSwitcher) convertView.findViewById(textSwitcherPriceDiff);

            holder.symbolTextSwitcher.setFactory(new LeftAlignViewFactory());
            holder.nameTextSwitcher.setFactory(new LeftAlignViewFactory());
            holder.priceTextSwitcher.setFactory(new RightAlignViewFactory());
            holder.priceDiffTextSwitcher.setFactory(new RightAlignViewFactory());

            holder.nameTextSwitcher.setInAnimation(in);
            holder.nameTextSwitcher.setOutAnimation(out);
            holder.symbolTextSwitcher.setInAnimation(in);
            holder.symbolTextSwitcher.setOutAnimation(out);
            holder.priceTextSwitcher.setInAnimation(in);
            holder.priceTextSwitcher.setOutAnimation(out);
            holder.priceDiffTextSwitcher.setInAnimation(in);
            holder.priceDiffTextSwitcher.setOutAnimation(out);

            convertView.setTag(holder);
        }
        else holder = (ViewHolder) convertView.getTag();

        StockInfo item = list.get(position);
        holder.nameTextSwitcher.setText(item.getName());
        holder.symbolTextSwitcher.setText(item.getSymbol());
        holder.priceTextSwitcher.setText("$ " + String.format("%.2f", item.getPrice()));
        holder.priceDiffTextSwitcher.setText("$ " + String.format("%.2f", item.getPriceChange()));
        return convertView;
    }

    private LayoutInflater getLayoutInflator() {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater)
                    this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return layoutInflater;
    }

    class LeftAlignViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            TextView t = new TextView(context);
            t.setGravity(Gravity.LEFT);
            return t;
        }
    }

    class RightAlignViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            TextView t = new TextView(context);
            t.setGravity(Gravity.RIGHT);
            return t;
        }
    }

    public class ViewHolder {
        TextSwitcher nameTextSwitcher;
        TextSwitcher symbolTextSwitcher;
        TextSwitcher priceTextSwitcher;
        TextSwitcher priceDiffTextSwitcher;
    }
}
