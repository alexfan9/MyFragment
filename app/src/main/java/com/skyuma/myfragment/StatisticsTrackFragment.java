package com.skyuma.myfragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsTrackFragment extends Fragment {
    Context context;
    Bundle bundle;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    View rootView;
    MapUtils mapUtils = null;
    public StatisticsTrackFragment() {
        // Required empty public constructor
    }
    public static StatisticsTrackFragment newInstance(Context context, Bundle bundle) {
        StatisticsTrackFragment fragment = new StatisticsTrackFragment();
        fragment.context = context;
        fragment.bundle = bundle;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_statistics_track, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.statisticsTrackMapView);
        if (mMapView != null){
            mBaiduMap = mMapView.getMap();
        }
        System.out.println("name:" + bundle.getString("name") + " timezone:" + bundle.getString("timezone") + " datetime" + bundle.getLong("datetime"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        TextView textView = (TextView) rootView.findViewById(R.id.statisticsDetailTitle);
        textView.setText(simpleDateFormat.format(bundle.getLong("datetime")) + " " + bundle.getString("timezone"));

        mapUtils = MapUtils.getInstance(context, bundle.getString("name"));
        addTrack();
        setTotalDistance();
        drawPaceItems();

        return rootView;
    }
    public void drawPaceItem(LatLng point, int index, long period){
        OverlayOptions options = new DotOptions().center(point).color(0xFF00CD00)
                .radius(15);
        int min = (int)(period / 60);
        int sec = (int)(period % 60);

        OverlayOptions ooText = new TextOptions().bgColor(0x00000000)
                .fontSize(24).fontColor(0xFFFF00FF).text(String.format("%d %d:%d", index, min, sec))
                .position(point);
        OverlayOptions ooText2 = new TextOptions().bgColor(0x00000000)
                .fontSize(24).fontColor(0xFF000000).text(String.format("%d", index))
                .position(point);
        mBaiduMap.addOverlay(ooText2);
        mBaiduMap.addOverlay(options);
    }

    public void drawPaceItems(){
        List<MapUtils.PaceItem> paceItems = mapUtils.getPaceItems();
        MapUtils.PaceItem  paceItem = null;
        Iterator<MapUtils.PaceItem> iterator=paceItems.iterator();
        while(iterator.hasNext()){
            paceItem = iterator.next();
            drawPaceItem(paceItem.getPoint(), paceItem.getIndex(), paceItem.getPeriod());
        }

    }


    private double meter2km(int meters){
        return Math.round(meters/100d)/10d;
    }
    private String getPeriodString(long period){
        String str = "";
        int usec = 1;
        int umin = usec * 60 ;
        int uhour = umin * 60;
        int uday = uhour * 24;

        long days = period / uday;
        long hours = (period % uday) /uhour;
        long miniutes = (period % uhour) /umin;
        long seconds = period % umin;

        if (days > 0){
            str = (""+days+"天"+hours+"小时"+miniutes+"分"+seconds+"秒");
        }else if (hours > 0){
            str = (""+ hours+"小时"+miniutes+"分"+seconds+"秒");
        }else if (miniutes > 0){
            str = (""+miniutes+"分"+seconds+"秒");
        }else{
            str = (""+seconds+"秒");
        }
        return str;
    }
    private String getAvgPaceString(double distance, long period){
        String str = "";
        float spe = (float)(distance / period);
        float pace = 1000/spe;
        int min = (int)(pace / 60);
        int sec = (int)(pace % 60);
        str = String.format("%d分%02d 秒", min, sec);
        return str;
    }

    private String getCaloryString(double distance){
        String str = "";
        long cal = (long)(60 * distance * 1.036 ) /1000;
        str = (""+cal+"大卡");
        return str;
    }
    public void setTotalDistance(){
        double distance = mapUtils.getTotalDistance();
        long period = mapUtils.getPeriod();

        TextView textViewDistance = (TextView) rootView.findViewById(R.id.txtDistance);
        textViewDistance.setText(meter2km((int) distance) + "公里");

        TextView txtLastTime = (TextView) rootView.findViewById(R.id.txtLastTime);
        txtLastTime.setText(getPeriodString(period));

        TextView txtAvgPace = (TextView) rootView.findViewById(R.id.txtAvgPace);
        txtAvgPace.setText(getAvgPaceString(distance, period));

        TextView txtCalory = (TextView) rootView.findViewById(R.id.txtCalory);
        txtCalory.setText(getCaloryString(distance));
    }

    public void drawPoint(LatLng point, int resId){
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(resId);
        MarkerOptions markerOptions = new MarkerOptions().position(point).icon(bitmapDescriptor).zIndex(9).draggable(true);
        if (mBaiduMap != null) {
            mBaiduMap.addOverlay(markerOptions);
        }
    }
    public void drawLine(List<LatLng> points){
        try{
            if (points.size() > 1){
                OverlayOptions overlayOptions = new PolylineOptions().width(12).color(0xAAFFC125).points(points);
                if (mBaiduMap != null && overlayOptions != null) {
                   mBaiduMap.addOverlay(overlayOptions);
                }
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
            if (mBaiduMap != null) {
                mBaiduMap.animateMapStatus(mMapStatusUpdate);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void addTrack(){
        drawPoint(mapUtils.getBaiduMapPoint(MapUtils.BAIDU_MAP_FIST_POINT), R.drawable.icon_marka);
        drawPoint(mapUtils.getBaiduMapPoint(MapUtils.BAIDU_MAP_LAST_POINT), R.drawable.icon_markb);
        drawLine(mapUtils.getBaiduMapPoints());
        adjustScreen(mapUtils.getBaiduMapPoint(MapUtils.BAIDU_MAP_AVG_POINT));
    }

    @Override
    public void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        if (mMapView != null) {
            mMapView.onDestroy();
        }

    }
}
