package com.skyuma.myfragment;

/**
 * Created by alex on 5/15/16.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;

/**
 * 获取gps位置信息的service
 *
 * @author king
 *
 */
public class MyGPSService extends Service {

    GPSDBManager gpsdbManager;
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    Context context;

    private LocationManager locationManager;
    private final GpsStatus.Listener statusLisener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            GpsStatus status = locationManager.getGpsStatus(null);
            onGPSLocationListener.onGpsStatusChanged(event, status);
        }
    };
    private OnGPSLocationListener onGPSLocationListener;
    public void setOnGPSLocationListener(OnGPSLocationListener onGPSLocationListener) {
        this.onGPSLocationListener = onGPSLocationListener;
    }
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;

    public MyGPSService() {
    }

    //private GPSUploadThread myThread;

    protected boolean checkGpsPermission(){
        if (Build.VERSION.SDK_INT>=23 &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        //创建LocationManger对象(LocationMangager，位置管理器。要想操作定位相关设备，必须先定义个LocationManager)
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkGpsPermission() == true) {
            locationManager.addGpsStatusListener(statusLisener);
        }
    }
    protected void setupCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
    }
    public void makeVibration(long milliseconds){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    public void startGPS(){
        if (locationManager == null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        setupCriteria();
        if (checkGpsPermission() == true) {
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            //保持cpu一直运行，不管屏幕是否黑屏
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
            wakeLock.acquire();
            makeVibration(1000);//震动1s
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }
    }


    public void stopGPS(){
        if (checkGpsPermission() == true) {
            locationManager.removeUpdates(locationListener);
            wakeLock.release();
            makeVibration(1000);//震动1s
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 实现一个位置变化的监听器
     */
    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            GPSLocation gpsLocation = new GPSLocation();
            gpsLocation.setLantitude(location.getLatitude());
            gpsLocation.setLongitude(location.getLongitude());
            gpsLocation.setAltitude(location.getAltitude());
            gpsLocation.setTime(location.getTime());
            gpsLocation.setSpeed(location.getSpeed());
            if (gpsdbManager == null){
                gpsdbManager = new GPSDBManager(context);
            }
            gpsdbManager.add(gpsLocation);

            if(onGPSLocationListener != null){
                onGPSLocationListener.onLocationChanged(location);
            }
        }

        // 当位置信息不可获取时
        @Override
        public void onProviderDisabled(String provider) {
            if(onGPSLocationListener != null){
                onGPSLocationListener.onProviderDisabled(provider);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if(onGPSLocationListener != null){
                onGPSLocationListener.onProviderEnabled(provider);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if(onGPSLocationListener != null){
                onGPSLocationListener.onStatusChanged(provider, status, extras);
            }
        }

    };
    /**
     * 返回一个Binder对象
     */
    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }

    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         * @return
         */
        public MyGPSService getService(){
            return MyGPSService.this;
        }
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        // toggleGPS(false);
        if (checkGpsPermission() == true) {
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
            }
        }
        wakeLock.release();
        super.onDestroy();
    }
    public interface OnGPSLocationListener {
        void onGpsStatusChanged(int event, GpsStatus status);
        void onLocationChanged(Location location);
        void onProviderDisabled(String provider);
        void onProviderEnabled(String provider);
        void onStatusChanged(String provider, int status, Bundle extras);
    }
}

