package x40241.jeffrey.lomibao.a4.aidl_example;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import x40241.jeffrey.lomibao.a4.aidl_example.model.ContactInfo;

/**
 * Created by jllom on 5/23/2017.
 */

public class CustomListAdapter extends BaseAdapter {
    private static final String LOGTAG = "CustomListAdapter";
    private static final boolean DEBUG = false;

    private Context context;
    private List<ContactInfo> list;
    private LayoutInflater layoutInflater;

    CustomListAdapter(Context context) {
        this.context = context;
    }

    public void setList (List<ContactInfo> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return ((list == null) ? 0 : list.size());
    }

    @Override
    public ContactInfo getItem (int position) {
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
            convertView = getLayoutInflator().inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.text_view);
            convertView.setTag(holder);
        }
        else holder = (ViewHolder) convertView.getTag();

        ContactInfo item = list.get(position);
        holder.textView.setText(item.getIdx() + ": " + item.getName() + " " + item.getSurname());
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
        TextView textView;
    }

}
