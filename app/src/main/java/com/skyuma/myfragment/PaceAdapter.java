package com.skyuma.myfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by afan on 2016/5/5.
 */
public class PaceAdapter extends BaseAdapter {
    private List<Map<String, Object>> paceList;
    private static LayoutInflater inflater=null;
    Context _context = null;
    public PaceAdapter( Context context, List<Map<String, Object>> arrayList){
        paceList = arrayList;
        _context = context;
        inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void updatePaceAdapter(List<Map<String, Object>> arrayList){
        paceList = arrayList;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pace_item, null, true);
            holder.index = (TextView) convertView.findViewById(R.id.textPaceIndex);
            holder.value = (TextView) convertView.findViewById(R.id.textPaceValue);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Map<String, Object> item = paceList.get(position);
        holder.index.setText(String.format("%d", position + 1));
        long section_time = (long)item.get("section_time");
        holder.value.setText(String.format("%02d%s%02d%s", section_time /60, "'", section_time % 60, '"'));
        return convertView;
    }
    public static class ViewHolder{
        public TextView index;
        public TextView value;
    }
}
