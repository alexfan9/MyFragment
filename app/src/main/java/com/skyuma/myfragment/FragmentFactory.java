package com.skyuma.myfragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by afan on 2016/4/7.
 */
public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index, FragmentManager fragmentManager){
        Fragment fragment = null;
        switch (index){
            case R.id.rb_home:
                fragment = HomeFragment.newInstance("param1", "param2");
                break;
            case R.id.rb_start:
                fragment = RunViewPagerFragment.newInstance("param1", "param2");
                //fragment = HomeFragment.newInstance("param1", "param2");
                break;
            case R.id.rb_me:
                fragment = RunFragment.newInstance("param1", "param2");
                break;
        }
        return fragment;
    }
}
