package com.skyuma.myfragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsPaceFragment extends Fragment {
    PaceItemAdapter adapter = null;
    Context context;
    Bundle bundle;
    public StatisticsPaceFragment() {
        // Required empty public constructor
    }

    public static StatisticsPaceFragment newInstance(Context context, Bundle bundle) {
        StatisticsPaceFragment fragment = new StatisticsPaceFragment();
        fragment.context = context;
        fragment.bundle = bundle;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics_pace, container, false);
        MapUtils mapUtils = MapUtils.getInstance(context, bundle.getString("name"));
        ArrayList<MapUtils.PaceItem> paceItems = mapUtils.getPaceItems();
        /*ArrayList<PaceItem2> paceItems = new ArrayList<PaceItem2>();
        PaceItem2 item = new PaceItem2(1, 300, 300);
        PaceItem2 item1 = new PaceItem2(2, 300, 600);
        PaceItem2 item2 = new PaceItem2(3, 300, 900);
        paceItems.add(item);
        paceItems.add(item1);
        paceItems.add(item2);*/

        System.out.println("paceItems size :" + paceItems.size());
        PaceItemAdapter adapter = new PaceItemAdapter(getActivity(), paceItems);
        ListView listView = (ListView) rootView.findViewById(R.id.pace_item_list);
        listView.setAdapter(adapter);
        return rootView;
    }
    public class PaceItem2{
        public int index;
        public LatLng point;
        public long period;
        public long cost;

        public PaceItem2(int index, long period, long cost) {
            this.index = index;
            this.period = period;
            this.cost = cost;
        }

        public int getIndex() {
            return index;
        }

        public long getPeriod() {
            return period;
        }
        public long getCost() {
            return cost;
        }

    }

}
