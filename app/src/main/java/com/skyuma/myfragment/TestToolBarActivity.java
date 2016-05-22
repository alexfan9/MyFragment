package com.skyuma.myfragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TestToolBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tool_bar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.menuContainer, StatisticsMenu.newInstance(getApplicationContext(), bundle)).commit();
        }
    }
}
