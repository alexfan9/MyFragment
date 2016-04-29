package com.skyuma.myfragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 24/4/16.
 */
public class DetailMapActivity extends Activity{
    private static final String LTAG = BaseMapDemo.class.getSimpleName();
    private MapView mMapView;
    Polyline mPolyline;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private Marker mMarkerB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String strName = b.getString("name");
        long _dateTime = b.getLong("datetime");
        String timezone = b.getString("timezone");
        setContentView(R.layout.activity_detailmap);

        mMapView = (MapView) findViewById(R.id.detailMapView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        TextView textView = (TextView) findViewById(R.id.txtDetailTitle);
        textView.setText(simpleDateFormat.format(b.getLong("datetime")) + " " + timezone);

        GPSDBManager gpsdbManager = new GPSDBManager(this);
        JSONArray jsonArray = gpsdbManager.getActivityContent(strName);

        if (mMapView != null) {
            mBaiduMap = mMapView.getMap();
        }
        addTrack(jsonArray);
    }

    public void addTrack(JSONArray jsonArray){
        double myLat = 0.0;
        double myLng = 0.0;
        List<LatLng> points = new ArrayList<LatLng>();
        LatLng start = null;
        LatLng end = null;
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        if (jsonArray.length() == 0){
            return;
        }
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                LatLng srcLatLng = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                converter.coord(srcLatLng);
                LatLng point = converter.convert();
                points.add(point);
                if (i == 0){
                    start = point;
                }else if (i == jsonArray.length() -1){
                    end = point;
                }
                myLat += point.latitude;
                myLng += point.longitude;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0xAAFF0000).points(points);
        if (mBaiduMap != null) {
            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        }
        if (start != null){
            BitmapDescriptor bdA = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_marka);
            MarkerOptions ooA = new MarkerOptions().position(start).icon(bdA)
                    .zIndex(9).draggable(true);
            mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
        }
        if (end != null){
            BitmapDescriptor bdB = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_markb);
            MarkerOptions ooB = new MarkerOptions().position(end).icon(bdB)
                    .zIndex(9).draggable(true);
            mMarkerA = (Marker) (mBaiduMap.addOverlay(ooB));
        }
        //OverlayOptions options;
        LatLng avePoint = new LatLng(myLat / jsonArray.length(), myLng / jsonArray.length());
        //options = new DotOptions().center(avePoint).color(0xAAff00ff).radius(15);
        //mBaiduMap.addOverlay(options);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(avePoint, 18);
        mBaiduMap.animateMapStatus(u);

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
