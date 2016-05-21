package com.skyuma.myfragment;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


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

        System.out.println("paceItems size :" + paceItems.size());
        PaceItemAdapter adapter = new PaceItemAdapter(getActivity(), paceItems);
        ListView listView = (ListView) rootView.findViewById(R.id.pace_item_list);
        listView.setAdapter(adapter);
        return rootView;
    }


}
