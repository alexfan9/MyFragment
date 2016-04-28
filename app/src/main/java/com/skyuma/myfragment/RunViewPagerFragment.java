package com.skyuma.myfragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RunViewPagerFragment extends Fragment  {

    private ViewPager pager;

    public ArrayList<Fragment> getFragmentList() {
        return fragmentList;
    }

    public ArrayList<Fragment> fragmentList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextView textViewRun, textViewPace, textViewMap;

    private String mParam1;
    private String mParam2;

    public RunViewPagerFragment() {
        // Required empty public constructor
    }
    public void initTextView(View v){
        textViewRun = (TextView) v.findViewById(R.id.textViewRun);
        textViewPace = (TextView) v.findViewById(R.id.textViewPace);
        textViewMap = (TextView) v.findViewById(R.id.textViewMap);

        textViewRun.setOnClickListener(new TextViewListerner(0));
        textViewPace.setOnClickListener(new TextViewListerner(1));
        textViewMap.setOnClickListener(new TextViewListerner(2));
    }

    public class TextViewListerner implements View.OnClickListener{
        private int index = 0;
        public TextViewListerner(int index){
            this.index = index;
        }
        @Override
        public void onClick(View v) {
            pager.setCurrentItem(this.index);
            updatedPageState(index);
        }
    }

    public void updatedPageState(int index){
        if (index == 0){
            textViewRun.setTextSize(24);
            textViewPace.setTextSize(18);
            textViewMap.setTextSize(18);
        }else if (index == 1){
            textViewRun.setTextSize(18);
            textViewPace.setTextSize(24);
            textViewMap.setTextSize(18);
        }else if (index == 2){
            textViewRun.setTextSize(18);
            textViewPace.setTextSize(18);
            textViewMap.setTextSize(24);
        }
    }
    public static RunViewPagerFragment newInstance(String param1, String param2) {
        RunViewPagerFragment runViewPagerFragment = new RunViewPagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        runViewPagerFragment.setArguments(args);
        return runViewPagerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_run_view_pager, container, false);
        initTextView(rootView);
        pager = (ViewPager) rootView.findViewById(R.id.viewpager);
        fragmentList = new ArrayList<Fragment>();

        Fragment runFragment = RunFragment.newInstance("param1", "param2");
        Fragment paceFragment = PaceFragment.newInstance("param1", "param2");
        Fragment mapFragment = MapFragment.newInstance("param1", "param2");

        fragmentList.add(runFragment);
        fragmentList.add(paceFragment);
        fragmentList.add(mapFragment);
        pager.setAdapter(new RunFragmentPagerAdapter(getActivity().getSupportFragmentManager(), fragmentList));
        pager.setCurrentItem(0);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updatedPageState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return rootView;
    }


}
