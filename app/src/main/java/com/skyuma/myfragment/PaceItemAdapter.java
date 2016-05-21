package com.skyuma.myfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 5/21/16.
 */
public class PaceItemAdapter extends BaseAdapter{
    private ArrayList<MapUtils.PaceItem> paceItems;
    private static LayoutInflater inflater=null;
    Context _context = null;

    public PaceItemAdapter(Context _context, ArrayList<MapUtils.PaceItem> paceItems) {
        this._context = _context;
        this.paceItems = paceItems;
    }


    @Override
    public int getCount() {
        if (paceItems != null) {
            return paceItems.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder holder;

        //if (convertView == null){
            //holder = new ViewHolder();
            //convertView = inflater.inflate(R.layout.paceitem, null, true);
            /*holder.index = (TextView) convertView.findViewById(R.id.itemIndex);
            holder.pace = (TextView) convertView.findViewById(R.id.itemPace);
            holder.cost = (TextView) convertView.findViewById(R.id.itemCost);
            convertView.setTag(holder);*/
        //}else{
            //holder = (ViewHolder) convertView.getTag();
        //}
        /*MapUtils.PaceItem paceItem = paceItems.get(position);
        holder.index.setText(paceItem.getIndex());
        holder.pace.setText(String.format("02%d:02%d", paceItem.getPeriod()/60, paceItem.getPeriod() % 60));
        holder.cost.setText(String.format("02%d:02%d:%02d", paceItem.getCost()/3600, paceItem.getCost()/60, paceItem.getCost()% 60));*/
        return convertView;
    }
    public static class ViewHolder{
        public TextView index;
        public TextView pace;
        public TextView cost;

    }
}
