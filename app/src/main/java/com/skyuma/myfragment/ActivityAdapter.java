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

/**
 * Created by afan on 2016/4/11.
 */
public class ActivityAdapter extends BaseAdapter{
    /*********** Declare Used Variables *********/
    private ArrayList<GPSActivity>activityArrayList;
    private static LayoutInflater inflater=null;
    Context _context = null;
    public ActivityAdapter( Context context, ArrayList<GPSActivity>arrayList){
        activityArrayList = arrayList;
        _context = context;
        inflater = ( LayoutInflater )_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void updateAdapterContent(ArrayList<GPSActivity>arrayList){
        activityArrayList = arrayList;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return activityArrayList.size();
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
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.tabitem, null, true);
            holder.img = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.item);
            holder.info = (TextView) convertView.findViewById(R.id.textView1);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        GPSActivity gpsActivity = activityArrayList.get(position);
        holder.img.setImageResource(R.mipmap.ic_launcher);

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy年MM月dd日 E");// 设置日期格式
        String strTitle =  simpleDateFormat1.format(gpsActivity.get_datetime());

        if (gpsActivity.get_new() == 1) {
            holder.title.setText(strTitle + "(New)");
        }else{
            holder.title.setText(strTitle);
        }
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");// 设置日期格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm Z");// 设置日期格式
        String strDate =  simpleDateFormat.format(gpsActivity.get_datetime());
        MapUtils mapUtils = MapUtils.getInstance(_context, gpsActivity.getName());
        double distance = mapUtils.getTotalDistance();
        long period = mapUtils.getPeriod();

        //meter2km((int) distance) + "公里"
        holder.info.setText(String.format("%.2f km    %s ", meter2km((int) distance), getAvgPaceString(distance, period)));
        return convertView;
    }
    private String getAvgPaceString(double distance, long period){
        String str = "";
        float spe = (float)(distance / period);
        float pace = 1000/spe;
        int min = (int)(pace / 60);
        int sec = (int)(pace % 60);
        str = String.format("%d分%02d 秒", min, sec);
        return str;
    }
    private double meter2km(int meters){
        return Math.round(meters/100d)/10d;
    }
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
        public ImageView img;
        public TextView title;
        public TextView info;
    }
}
