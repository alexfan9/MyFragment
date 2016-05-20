package com.skyuma.myfragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsDetailFragment extends Fragment {


    public StatisticsDetailFragment() {
        // Required empty public constructor
    }

    public static StatisticsDetailFragment newInstance(Bundle bundle) {
        StatisticsDetailFragment fragment = new StatisticsDetailFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics_detail, container, false);
    }


}
