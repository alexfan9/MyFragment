package com.skyuma.myfragment;

import android.content.Context;
import android.location.Location;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 5/21/16.
 */
public class MapUtils {

    public static final int BAIDU_MAP_FIST_POINT = 1;
    public static final int BAIDU_MAP_LAST_POINT = 2;
    public static final int BAIDU_MAP_AVG_POINT = 3;


    private JSONArray jsonArray;
    String activity;
    GPSDBManager gpsdbManager;
    Context context;
    private static MapUtils instance=null;
    //静态工厂方法
    public static MapUtils getInstance(Context context, String activity) {
        if (instance == null) {
            instance = new MapUtils( context, activity);
        }
        return instance;
    }

    public MapUtils(Context context, String activity) {
        this.context = context;
        this.activity = activity;
        setActivity();
    }

    public void setActivity() {
        if (gpsdbManager == null){
            gpsdbManager = new GPSDBManager(context);
        }
        jsonArray = gpsdbManager.getActivityContent(activity);
    }

    public List<LatLng> getBaiduMapPoints() {
        List<LatLng> points = new ArrayList<LatLng>();
        if (jsonArray == null || jsonArray.length() < 1){
            return null;
        }
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                LatLng srcLatLng = new LatLng(jsonObject.getDouble("latitude"),
                        jsonObject.getDouble("longitude"));
                converter.coord(srcLatLng);
                LatLng point = converter.convert();
                points.add(point);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return points;
    }

    public LatLng getBaiduMapPoint(int type) {
        LatLng srcLatLng = null;
        LatLng point = null;
        JSONObject jsonObject = null;
        try {
            if (jsonArray == null || jsonArray.length() < 1){
                return null;
            }
            switch (type){
                case BAIDU_MAP_FIST_POINT:
                    jsonObject = jsonArray.getJSONObject(0);
                    srcLatLng = new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"));
                    break;
                case BAIDU_MAP_LAST_POINT:
                    jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
                    srcLatLng = new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"));
                    break;
                case BAIDU_MAP_AVG_POINT:
                    double myLat = 0.0, myLng = 0.0;
                    for (int i = 0; i < jsonArray.length(); i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        myLat += jsonObject.getDouble("latitude");
                        myLng += jsonObject.getDouble("longitude");
                    }
                    srcLatLng = new LatLng(myLat / jsonArray.length(), myLng / jsonArray.length());
                    break;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(srcLatLng);
        point = converter.convert();
        return point;
    }

    public double getTotalDistance() {
        double distance = 0;
        JSONObject lastObject = null;
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (i > 0) {
                    distance += getDistance(lastObject, jsonObject);
                }
                lastObject = jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return distance;
    }

    public long getPeriod() {
        long period = 0;
        try {
            if (jsonArray != null && jsonArray.length() > 1) {
                JSONObject first = jsonArray.getJSONObject(0);
                JSONObject last = jsonArray.getJSONObject(jsonArray.length() - 1);
                period = (last.getLong("date") - first.getLong("date")) / 1000;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return period;
    }

    public double getDistance(JSONObject obj1, JSONObject obj2) throws JSONException {
        float[] results = new float[1];
        Location.distanceBetween(obj1.getDouble("latitude"),
                obj1.getDouble("longitude"),
                obj2.getDouble("latitude"),
                obj2.getDouble("longitude"), results);
        return  results[0];
    }
    public ArrayList<PaceItem> getPaceItems(){
        if (jsonArray == null){
            return null;
        }
        ArrayList<PaceItem> paceItems = new ArrayList<PaceItem>();
        double distance = 0;
        int index = 1;
        JSONObject firstObject = null;
        JSONObject lastObject = null;
        JSONObject startObject = null;
        JSONObject endObject = null;
        int len = jsonArray.length();

        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (i > 0) {
                    distance += getDistance(lastObject, jsonObject);
                }
                if (i == 0){
                    startObject = jsonObject;
                    firstObject = jsonObject;
                }
                if (distance > index * 1000) {
                    endObject = jsonObject;
                    long period = (endObject.getLong("date") - startObject.getLong("date")) / 1000;
                    long cost = (endObject.getLong("date") - firstObject.getLong("date")) / 1000;
                    converter.coord(new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude")));
                    LatLng point = converter.convert();

                    PaceItem item = new PaceItem(index, period, point, cost);
                    paceItems.add(item);
                    startObject = jsonObject;
                    index++;
                }
                lastObject = jsonObject;
                if (i == len -1){
                    endObject = jsonObject;
                    long period = (endObject.getLong("date") - startObject.getLong("date")) / 1000;
                    long cost = (endObject.getLong("date") - firstObject.getLong("date")) / 1000;
                    double l_distance = distance - (index -1 ) * 1000;
                    long tmp = (long)(period * 1000 / l_distance);
                    converter.coord(new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude")));
                    LatLng point = converter.convert();
                    PaceItem item = new PaceItem(index, tmp, point, cost);
                    paceItems.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return paceItems;
    }

    public class PaceItem{
        public int index;
        public LatLng point;
        public long period;
        public long cost;

        public PaceItem(int index, long period, LatLng point, long cost) {
            this.index = index;
            this.period = period;
            this.point = point;
            this.cost = cost;
        }

        public int getIndex() {
            return index;
        }

        public long getPeriod() {
            return period;
        }

        public LatLng getPoint() {
            return point;
        }
        public long getCost() {
            return cost;
        }

    }
}
