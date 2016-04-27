package com.skyuma.myfragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

import org.json.JSONArray;

import java.text.SimpleDateFormat;

/**
 * Created by alex on 24/4/16.
 */
public class DetailMapActivity extends Activity{
    private static final String LTAG = BaseMapDemo.class.getSimpleName();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String strName = b.getString("name");
        long _dateTime = b.getLong("datetime");
        String timezone = b.getString("timezone");

        mMapView = (MapView) findViewById(R.id.detailMapView);
        setContentView(R.layout.activity_detailmap);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        TextView textView = (TextView) findViewById(R.id.txtDetailTitle);
        textView.setText(simpleDateFormat.format(b.getLong("datetime")) + timezone);

        GPSDBManager gpsdbManager = new GPSDBManager(this);
        JSONArray jsonArray = gpsdbManager.getActivityContent(strName);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        if (mMapView != null) {
            mMapView.onDestroy();
        }

    }
}
