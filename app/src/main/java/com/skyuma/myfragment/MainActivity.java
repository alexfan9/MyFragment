package com.skyuma.myfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.baidu.mapapi.SDKInitializer;

public class MainActivity extends AppCompatActivity implements SettingsFragment.OnSettingChangedListener{
    private SessionManager session;
    Fragment currentFragment = null;
    Fragment homeFragment = null;
    Fragment runViewFragment = null;
    Fragment settingFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        homeFragment = HomeFragment.newInstance("param1", "param2");
        runViewFragment = RunViewPagerFragment.newInstance("param1", "param2");
        settingFragment = SettingsFragment.newInstance("param1", "param2");
        currentFragment = homeFragment;
        addFragment(homeFragment);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_home:
                        swtichFragment(currentFragment, homeFragment);
                        break;
                    case R.id.rb_start:
                        swtichFragment(currentFragment, runViewFragment);
                        break;
                    case R.id.rb_me:
                        swtichFragment(currentFragment, settingFragment);
                        break;
                }
            }
        });


        findViewById(R.id.rb_home).performClick();

        session = new SessionManager(getApplicationContext());
        session.checkLogin(this);
        /*
        if(!session.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }*/


        findViewById(R.id.btnOpenBaiduMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BaiduMapActivity.class);
                startActivity(intent);
            }
        });
    }
    public void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!fragment.isAdded()){
            fragmentTransaction.add(R.id.content, fragment).commit();
        } else {
            fragmentTransaction.show(fragment).commit();
        }
    }
    public void swtichFragment(Fragment from, Fragment to){
        if (currentFragment != to ){
            currentFragment = to;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (!to.isAdded()){
                fragmentTransaction.hide(from).add(R.id.content, to).commit();
            } else {
                fragmentTransaction.hide(from).show(to).commit();
            }
        }
    }

    @Override
    public void OnSettingChanged() {
        if (homeFragment != null){
            ((HomeFragment) homeFragment).updateAdapter();
        }
    }
}
