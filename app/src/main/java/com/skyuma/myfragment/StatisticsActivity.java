package com.skyuma.myfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class StatisticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("FPP");
        setContentView(R.layout.activity_statistics);
        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/

        /*Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.menuContainer, StatisticsMenu.newInstance(getApplicationContext(), bundle)).commit();
        }*/
    }
}
