package com.skyuma.myfragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunFragment extends Fragment {
    GPSDBManager gpsdbManager;
    TextView textView, textViewStatus;
    private Chronometer timer;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
    JSONArray jsonArray2 = new JSONArray();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
    private LocationManager locationManager;
    private final GpsStatus.Listener statusLisener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            if (locationManager != null){
                int count = 0;
                GpsStatus status = locationManager.getGpsStatus(null);
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
                    is_running = true;
                    imageButtonStart.setImageResource(R.drawable.stop_selector);
                    textView.setText("GPS Start");
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();

                }else if (event == GpsStatus.GPS_EVENT_STOPPED){
                    is_running = false;
                    imageButtonStart.setImageResource(R.drawable.start_selector);
                    textView.setText("GPS Stop");
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.stop();
                }
            }
        }
    };
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (is_running == false){
                is_running = true;
            }
            GPSLocation gpsLocation = new GPSLocation();
            gpsLocation.setLantitude(location.getLatitude());
            gpsLocation.setLongitude(location.getLongitude());
            gpsLocation.setAltitude(location.getAltitude());
            gpsLocation.setTime(location.getTime());
            gpsLocation.setSpeed(location.getSpeed());
            //locationList.add(gpsLocation);
            if (gpsdbManager != null){
                gpsdbManager.add(gpsLocation);
            }
            List<GPSLocation> locations = gpsdbManager.query();

           /* System.out.println("jsonArray2 output start");
            try {
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("latitude", location.getLatitude());
                jsonObject2.put("longitude", location.getLongitude());
                System.out.println(jsonObject2.toString());
                jsonArray2.put(jsonObject2);
            }catch (JSONException e){
                e.printStackTrace();
            }
            System.out.println(jsonArray2.toString());
            System.out.println("jsonArray2 output end");*/

            JSONArray jsonArray = new JSONArray();
            Iterator it1 = locations.iterator();
            while (it1.hasNext()){
                JSONObject jsonObject = new JSONObject();
                GPSLocation location2 = (GPSLocation) it1.next();
                try {
                    jsonObject.put("latitude", location2.getLantitude());
                    jsonObject.put("longitude", location2.getLongitude());
                    jsonObject.put("altitude", location2.getAltitude());
                    jsonObject.put("speed", location2.getSpeed());
                    jsonObject.put("date", location2.getTime());
                    jsonArray.put(jsonObject);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            System.out.println("jsonArray output start");
            System.out.println(jsonArray.toString());
            System.out.println("jsonArray output end");


            String latLongString;
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                float spe = location.getSpeed();// 速度
                float acc = location.getAccuracy();// 精度
                double alt = location.getAltitude();// 海拔
                float bea = location.getBearing();// 轴承
                long tim = location.getTime();// 返回UTC时间1970年1月1毫秒
                latLongString = "纬度:" + lat + "\n经度:" + lng + "\n精度：" + acc
                        + "\n速度：" + spe + "\n海拔：" + alt + "\n轴承：" + bea + "\n时间："
                        + sdf.format(tim);
            } else {
                latLongString = "无法获取位置信息";
            }
            textViewStatus.setText("您当前的位置是:\n" + latLongString);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


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
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Toast.makeText(getActivity(), "The Location servivce has not start yet.", Toast.LENGTH_SHORT).show();
        }else{
            if (checkGpsPermission() == true) {
                locationManager.addGpsStatusListener(statusLisener);
            }
        }

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

    protected void setupCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
    }

    protected void stopRunning() {
        if (locationManager != null && locationListener != null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Confirm")
                    .setMessage("Are you sure to stop this activity?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkGpsPermission() == true) {
                                locationManager.removeUpdates(locationListener);
                                gpsdbManager.createActivity("activity");
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    protected boolean checkGpsPermission(){
        if (Build.VERSION.SDK_INT>=23 &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
    protected void startRunning() {
        if (locationManager == null) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            Toast.makeText(getActivity(), "The GPS is off, please switch on.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
        }
        setupCriteria();
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm")
                .setMessage("Are you sure to start this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkGpsPermission() == true) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            if (gpsdbManager != null){
                                gpsdbManager.clear();
                            }
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


}
