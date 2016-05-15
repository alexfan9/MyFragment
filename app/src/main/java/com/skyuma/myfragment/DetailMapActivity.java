package com.skyuma.myfragment;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
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
    private static final String LTAG = DetailMapActivity.class.getSimpleName();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String strName = b.getString("name");
        String timezone = b.getString("timezone");
        setContentView(R.layout.activity_detailmap);

        mMapView = (MapView) findViewById(R.id.detailMapView);
        if (mMapView != null){
            mBaiduMap = mMapView.getMap();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        TextView textView = (TextView) findViewById(R.id.txtDetailTitle);
        textView.setText(simpleDateFormat.format(b.getLong("datetime")) + " " + timezone);

        GPSDBManager gpsdbManager = new GPSDBManager(this);
        JSONArray jsonArray = gpsdbManager.getActivityContent(strName);
        addTrack(jsonArray);
        setTotalDistance(jsonArray);
        paceStatistics(jsonArray);
    }

    public void drawPaceItem(LatLng gps_point, int index, long period){
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(gps_point);
        LatLng point = converter.convert();

        OverlayOptions options = new DotOptions().center(point).color(0xAAFFff00)
                .radius(15);
        int min = (int)(period / 60);
        int sec = (int)(period % 60);

        String strIndex = String.format("%d", index);
        OverlayOptions ooText = new TextOptions().bgColor(0x00000000)
                .fontSize(24).fontColor(0xFFFF00FF).text(String.format("%d %d:%d", index, min, sec))
                .position(point);
        mBaiduMap.addOverlay(ooText);
        mBaiduMap.addOverlay(options);
    }

    public void paceStatistics(JSONArray jsonArray){
        double distance = 0;
        int index = 1;
        JSONObject lastObject = null;
        JSONObject startObject = null;
        JSONObject endObject = null;
        JSONObject tempObject = null;
        int len = jsonArray.length();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (i > 0) {
                    distance += getDistance(lastObject.getDouble("latitude"),
                            lastObject.getDouble("longitude"),
                            jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"));
                }
                if (i == 0){
                    startObject = jsonObject;
                }
                if (distance > index * 1000) {
                    endObject = jsonObject;
                    long period = (endObject.getLong("date") - startObject.getLong("date")) / 1000;

                    LatLng srcLatLng = new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"));
                    drawPaceItem(srcLatLng, index, period);
                    startObject = jsonObject;
                    index++;
                }
                lastObject = jsonObject;
                if (i == len -1){
                    endObject = jsonObject;
                    long period = (endObject.getLong("date") - startObject.getLong("date")) / 1000;
                    LatLng srcLatLng = new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"));
                    double l_distance = distance - (index -1 ) * 1000;

                    long tmp = (long)(period * 1000 / l_distance);
                    drawPaceItem(srcLatLng, index, tmp);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private double meter2km(int meters){
        return Math.round(meters/100d)/10d;
    }
    public void setTotalDistance(JSONArray jsonArray){
        double totalDistalce = getTotalDistance(jsonArray);
        TextView textViewDistance = (TextView) findViewById(R.id.txtDistance);
        textViewDistance.setText( meter2km((int)totalDistalce) + "公里");

        int len = jsonArray.length();
        try {
            JSONObject first = jsonArray.getJSONObject(0);
            JSONObject last = jsonArray.getJSONObject(len -1);
            long period = (last.getLong("date") - first.getLong("date"))/1000;
            int usec = 1;
            int umin = usec * 60 ;
            int uhour = umin * 60;
            int uday = uhour * 24;

            long days = period / uday;
            long hours = (period % uday) /uhour;
            long miniutes = (period % uhour) /umin;
            long seconds = period % umin;

            String str = "";
            if (days > 0){
                str = (""+days+"天"+hours+"小时"+miniutes+"分"+seconds+"秒");
            }else if (hours > 0){
                str = (""+ hours+"小时"+miniutes+"分"+seconds+"秒");
            }else if (miniutes > 0){
                str = (""+miniutes+"分"+seconds+"秒");
            }else{
                str = (""+seconds+"秒");
            }

            float spe = (float)(totalDistalce / period);
            float pace = 1000/spe;
            int min = (int)(pace / 60);
            int sec = (int)(pace % 60);

            TextView txtLastTime = (TextView) findViewById(R.id.txtLastTime);
            txtLastTime.setText(str);

            str = (""+min+"分"+sec+"秒");
            TextView txtAvgPace = (TextView) findViewById(R.id.txtAvgPace);
            txtAvgPace.setText(str);

            long cal = (long)(60 * totalDistalce * 1.036 ) /1000;
            str = (""+cal+"大卡");
            TextView txtCalory = (TextView) findViewById(R.id.txtCalory);
            txtCalory.setText(str);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2){
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return  results[0];
    }

    public double getTotalDistance(JSONArray jsonArray){
        double distance = 0;
        JSONObject lastObject = null;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (i > 0) {
                    distance += getDistance(lastObject.getDouble("latitude"),
                            lastObject.getDouble("longitude"),
                            jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"));
                }
                lastObject = jsonObject;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return distance;
    }

    public void drawStart(LatLng point, int resId){
        try{
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(resId);
            MarkerOptions markerOptions = new MarkerOptions().position(point).icon(bitmapDescriptor).zIndex(9).draggable(true);
            mMapView.getMap().addOverlay(markerOptions);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void drawLine(List<LatLng> points){
        try{
            if (points.size() > 0){
                OverlayOptions overlayOptions = new PolylineOptions().width(12).color(0xAAFF0000).points(points);
                mMapView.getMap().addOverlay(overlayOptions);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void adjustScreen(LatLng point){
        try{
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(point)
                    .zoom(16)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //mBaiduMap.setMapStatus(mMapStatusUpdate);
            mBaiduMap.animateMapStatus(mMapStatusUpdate);


            /*MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point, 16);
            mBaiduMap.animateMapStatus(u);*/
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void addTrack(JSONArray jsonArray){

        double myLat = 0.0, myLng = 0.0;
        List<LatLng> points = new ArrayList<LatLng>();
        LatLng start = null, end = null;
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        if (jsonArray.length() == 0){
            return;
        }
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                LatLng srcLatLng = new LatLng(jsonObject.getDouble("latitude"),
                        jsonObject.getDouble("longitude"));
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
        drawStart(start, R.drawable.icon_marka);
        drawStart(end, R.drawable.icon_markb);
        drawLine(points);
        LatLng avePoint = new LatLng(myLat / jsonArray.length(), myLng / jsonArray.length());
        adjustScreen(avePoint);
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
