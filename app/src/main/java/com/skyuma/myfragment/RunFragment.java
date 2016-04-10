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
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

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
            System.out.println("onGpsStatusChanged enter");
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
                    System.out.println("Satellite number :" + mSatelliteNum);
                }else if (event == GpsStatus.GPS_EVENT_STARTED){
                    is_running = true;
                    imageButtonStart.setImageResource(R.drawable.stop_selector);
                    System.out.println("GPS start");
                }else if (event == GpsStatus.GPS_EVENT_STOPPED){
                    is_running = false;
                    imageButtonStart.setImageResource(R.drawable.start_selector);
                    System.out.println("GPS stop");
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
            System.out.println("Latitude:" + location.getLatitude() + "Longitude:" + location.getLongitude() + "count:" + locations.size());
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
                                gpsdbManager.createActivity("activity1");
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
