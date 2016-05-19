package com.skyuma.myfragment;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunFragment extends Fragment {
    GPSDBManager gpsdbManager;
    OnSettingChangedListener mCallback = null;
    TextView textView, textViewStatus;
    private Chronometer timer;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
    JSONArray jsonArray = new JSONArray();
    Location first_location;
    Location last_location;
    Location tmp_location;
    double distance;
    double tmp_distance;
    int index = 0;
    List<Map<String, Object>> sectionList = new ArrayList<Map<String, Object>>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private MyGPSService myGPSService;
    public ArrayList<GPSLocation> getLocationList() {
        return locationList;
    }

    public  ArrayList<GPSLocation> locationList = new ArrayList<GPSLocation>();
    private static boolean is_running = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<GpsSatellite> numSatelliteList;
    private int mSatelliteNum;
    private ImageButton imageButtonStart;

    public double getDistance(double lat1, double lon1, double lat2, double lon2){
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return  results[0];
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunFragment newInstance(String param1, String param2) {
        RunFragment fragment = new RunFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RunFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnSettingChangedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() +
                    " must implemtn OnSettingChangedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);
        textView = (TextView) rootView.findViewById(R.id.textViewGPS);
        textViewStatus = (TextView) rootView.findViewById(R.id.textViewStatus);
        timer = (Chronometer)rootView.findViewById(R.id.chronometer);
        timer.setBase(SystemClock.elapsedRealtime());
        numSatelliteList = new ArrayList<GpsSatellite>();
        Intent intent = new Intent(getActivity(), MyGPSService.class);
        getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        imageButtonStart = (ImageButton) rootView.findViewById(R.id.imageButtonStart);
        imageButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_running) {
                    stopRunning();
                } else {
                    startRunning();
                }
            }
        });

        gpsdbManager = new GPSDBManager(getActivity());
        return rootView;
    }


    @Override
    public void onPause() {
        super.onPause();

    }
    protected void stopRunning() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm")
                .setMessage("Are you sure to stop this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myGPSService.stopGPS();
                        index = 0;
                        is_running = false;
                        imageButtonStart.setImageResource(R.drawable.start_selector);
                        textView.setText("GPS Stop");
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.stop();
                        if (jsonArray != null) {

                            if (jsonArray.length() >= 5) {
                                gpsdbManager.createActivity("activity");
                                mCallback.OnSettingChanged();
                            } else {
                                Toast.makeText(getActivity(), "距离太短,此次活动将不被保存.", Toast.LENGTH_SHORT).show();
                            }
                            jsonArray = null;
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    protected void startRunning() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm")
                .setMessage("Are you sure to start this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myGPSService.startGPS();

                        index = 1;
                        first_location = null;
                        is_running = true;
                        imageButtonStart.setImageResource(R.drawable.stop_selector);
                        textView.setText("GPS Start");
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.start();
                        if (gpsdbManager != null){
                            gpsdbManager.clear();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
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
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            myGPSService = ((MyGPSService.MsgBinder)service).getService();
            myGPSService.setContext(getActivity());
            //注册回调接口来接收下载进度的变化
            myGPSService.setOnGPSLocationListener(new MyGPSService.OnGPSLocationListener() {
                @Override
                public void onGpsStatusChanged(int event, GpsStatus status) {
                    int count = 0;
                    if (status == null){
                        return;
                    }
                    if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
                        int maxSatellites = status.getMaxSatellites();
                        Iterator<GpsSatellite> it = status.getSatellites().iterator();
                        numSatelliteList.clear();
                        while (it.hasNext() && count < maxSatellites){
                            GpsSatellite s = it.next();
                            numSatelliteList.add(s);
                            count++;
                        }
                        mSatelliteNum = numSatelliteList.size();
                        if (textView != null){
                            textView.setText("Satellite number :" + mSatelliteNum + "  count:" + count);
                        }
                    }else if (event == GpsStatus.GPS_EVENT_STARTED){


                    }else if (event == GpsStatus.GPS_EVENT_STOPPED){

                    }
                }

                @Override
                public void onLocationChanged(Location location) {

                    JSONArray jsonArray = gpsdbManager.getActivityContent("activity");
                    distance = getTotalDistance(jsonArray);
                    String latLongString;
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        float spe = location.getSpeed();// 速度
                        float pace = 1000/spe;
                        int min = (int)(pace / 60);
                        int sec = (int)(pace % 60);
                        float acc = location.getAccuracy();// 精度
                        double alt = location.getAltitude();// 海拔
                        float bea = location.getBearing();// 轴承
                        long tim = location.getTime();// 返回UTC时间1970年1月1毫秒
                        float cal = (float)(60 * distance * 1.036 ) /1000;

                        latLongString = "纬度:" + lat + "\n经度:" + lng + "\n精度：" + acc
                                + "\n速度：" + spe + "\n海拔：" + alt + "\n轴承：" + bea
                                + "\n点数：" + jsonArray.length()
                                + "\n距离：" + (int)distance + " 米"
                                + "\n当前配速：" + min + "分" + sec + "秒"
                                + "\n卡路里：" + cal
                                + "\n时间："+ sdf.format(tim);

                        MainActivity activity = (MainActivity) getActivity();
                        PaceFragment paceFragment = (PaceFragment) ((RunViewPagerFragment) activity.runViewFragment).getFragmentList().get(1);
                        MapFragment mapFragment = (MapFragment) ((RunViewPagerFragment) activity.runViewFragment).getFragmentList().get(2);
                        //mapFragment.setCurrentLocation(location);
                        mapFragment.drawLine(jsonArray);
                        if (distance > index * 1000) {
                            myGPSService.makeVibration(1000);
                            index++;
                        }

                        paceFragment.textView.setText("距离：" + (float) (distance /1000) + " 千米");
                    } else {
                        latLongString = "无法获取位置信息";
                    }
                    textViewStatus.setText("您当前的位置是:\n" + latLongString);
                }

                @Override
                public void onProviderDisabled(String provider) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            });
        }
    };

}
