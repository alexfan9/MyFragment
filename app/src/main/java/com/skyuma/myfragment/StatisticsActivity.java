package com.skyuma.myfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    public ArrayList<Fragment> fragmentList;
    private ViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager)findViewById(R.id.statisticsViewpager);
        fragmentList = new ArrayList<Fragment>();

        Fragment statisticsDetailFragment = StatisticsDetailFragment.newInstance();
        Fragment statisticsPaceFragment = StatisticsPaceFragment.newInstance();
        Fragment statisticsTrackFragment = StatisticsTrackFragment.newInstance();
        Fragment statisticsChartFragment = StatisticsChartFragment.newInstance();


        fragmentList.add(statisticsTrackFragment);
        fragmentList.add(statisticsPaceFragment);
        fragmentList.add(statisticsDetailFragment);
        fragmentList.add(statisticsChartFragment);

        pager.setOffscreenPageLimit(2);
        pager.setAdapter(new StatisticsFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        pager.setCurrentItem(0);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
}
