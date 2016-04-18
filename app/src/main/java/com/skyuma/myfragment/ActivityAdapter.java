package com.skyuma.myfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by afan on 2016/4/11.
 */
public class ActivityAdapter extends BaseAdapter implements View.OnClickListener{

    /*********** Declare Used Variables *********/
    private ArrayList<GPSActivity>activityArrayList;
    private static LayoutInflater inflater=null;
    Context _context = null;

    int i=0;

    public ActivityAdapter( Context context, ArrayList<GPSActivity>arrayList){
        activityArrayList = arrayList;
        _context = context;
        inflater = ( LayoutInflater )_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        if (activityArrayList.size() <=0){
            return 1;
        }
        return activityArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = inflater.inflate(R.layout.tabitem, null, true);

        TextView txtTitle = (TextView) rootView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rootView.findViewById(R.id.textView1);

        if (activityArrayList.isEmpty()) {
            txtTitle.setText("Empty");
        } else {
            GPSActivity gpsActivity = activityArrayList.get(position);
            txtTitle.setText(gpsActivity.getName());
            imageView.setImageResource(R.drawable.rain);
            extratxt.setText("Description " + gpsActivity.getName());
        }
        return rootView;

    }

    @Override
    public void onClick(View v) {

    }

    private class OnItemClickListener implements View.OnClickListener{
        private int mPosition;
        OnItemClickListener(int position){
            mPosition = position;
        }
        @Override
        public void onClick(View v) {
            System.out.println("OnItemClickListener click");
        }
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView text;
        public TextView text1;
        //public TextView textWide;
        //public ImageView image;

    }

}
