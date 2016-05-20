package com.skyuma.myfragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by alex on 5/20/16.
 */
public class StatisticsMenu extends Fragment {
    Fragment frag;
    FragmentTransaction fragmentTransaction;
    Bundle bundle;

    public StatisticsMenu() {
    }

    public static StatisticsMenu newInstance(Bundle bundle) {
        StatisticsMenu fragment = new StatisticsMenu();
        fragment.bundle = bundle;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.statistics_menu, container, false);
        frag = StatisticsTrackFragment.newInstance(bundle);
        fragmentTransaction = getFragmentManager().beginTransaction().add(R.id.container, frag);
        fragmentTransaction.commit();

        Button buttonTrack = (Button) rootView.findViewById(R.id.buttonTrack);
        Button buttonPace = (Button) rootView.findViewById(R.id.buttonPace);
        Button buttonDetail = (Button) rootView.findViewById(R.id.buttonDetail);
        Button buttonChart = (Button) rootView.findViewById(R.id.buttonChart);

        buttonTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = StatisticsTrackFragment.newInstance(bundle);
                fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragmentTransaction.commit();
            }
        });
        buttonPace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new StatisticsPaceFragment();
                fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragmentTransaction.commit();
            }
        });
        buttonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new StatisticsDetailFragment();
                fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragmentTransaction.commit();
            }
        });
        buttonChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new StatisticsChartFragment();
                fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }
}
