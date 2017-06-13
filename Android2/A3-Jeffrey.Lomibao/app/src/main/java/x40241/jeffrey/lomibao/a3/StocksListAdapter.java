package x40241.jeffrey.lomibao.a3;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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


/**
 * Created by jllom on 5/23/2017.
 */

public class StocksListAdapter extends BaseAdapter {
    private static final String LOGTAG = "StockListAdapter";
    private static final boolean DEBUG = true;

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

            holder.symbolTextSwitcher = (TextSwitcher) convertView.findViewById(R.id.textSwitcherSymbol);
            holder.nameTextSwitcher = (TextSwitcher) convertView.findViewById(R.id.textSwitcherName);
            holder.priceTextSwitcher = (TextSwitcher) convertView.findViewById(R.id.textSwitcherPrice);
            holder.priceDiffTextSwitcher = (TextSwitcher) convertView.findViewById(R.id.textSwitcherPriceDiff);

            holder.symbolTextSwitcher.setFactory(new ViewFactorySymbol());
            holder.nameTextSwitcher.setFactory(new ViewFactoryName());
            holder.priceTextSwitcher.setFactory(new ViewFactoryPrice());
            holder.priceDiffTextSwitcher.setFactory(new ViewFactoryPriceDiff());

            Animation slide_in = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            Animation slide_out = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
            Animation fade_in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            Animation fade_out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
            holder.nameTextSwitcher.setInAnimation(slide_in);
            holder.nameTextSwitcher.setOutAnimation(slide_out);
            holder.symbolTextSwitcher.setInAnimation(fade_in);
            holder.symbolTextSwitcher.setOutAnimation(fade_out);
            holder.priceTextSwitcher.setInAnimation(slide_in);
            holder.priceTextSwitcher.setOutAnimation(slide_out);
            holder.priceDiffTextSwitcher.setInAnimation(fade_in);
            holder.priceDiffTextSwitcher.setOutAnimation(fade_out);

            convertView.setTag(holder);
        }
        else holder = (ViewHolder) convertView.getTag();

        StockInfo item = list.get(position);
        holder.nameTextSwitcher.setText(item.getName());
        holder.symbolTextSwitcher.setText(item.getSymbol());
        holder.priceTextSwitcher.setText("$ " + String.format("%.2f", item.getPrice()));
        float priceChange = item.getPriceChange();
        holder.priceDiffTextSwitcher.setText("$ " + String.format("%.2f", priceChange));

        int color = Color.TRANSPARENT;
        if(priceChange > 0) {
            color = Color.BLUE;
        } else if(priceChange < 0){
            color = Color.RED;
        }
        TextView tv = (TextView)holder.symbolTextSwitcher.getCurrentView();
        tv.setTextColor(color);
        tv = (TextView)holder.priceTextSwitcher.getCurrentView();
        tv.setTextColor(color);

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
        TextSwitcher nameTextSwitcher;
        TextSwitcher symbolTextSwitcher;
        TextSwitcher priceTextSwitcher;
        TextSwitcher priceDiffTextSwitcher;
    }

    class ViewFactorySymbol implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            TextView t = new TextView(context);
            t.setGravity(Gravity.LEFT);
            return t;
        }
    }

    class ViewFactoryName implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            TextView t = new TextView(context);
            t.setGravity(Gravity.LEFT);
            t.setTypeface(null, Typeface.BOLD);
            return t;
        }
    }

    class ViewFactoryPrice implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            TextView t = new TextView(context);
            t.setGravity(Gravity.RIGHT);
            return t;
        }
    }

    class ViewFactoryPriceDiff implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            TextView t = new TextView(context);
            t.setGravity(Gravity.RIGHT);
            return t;
        }
    }

}
