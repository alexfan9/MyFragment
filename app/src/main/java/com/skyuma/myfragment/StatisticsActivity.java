package com.skyuma.myfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_statistics);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.menuContainer, StatisticsMenu.newInstance(bundle)).commit();
        }
    }
}
