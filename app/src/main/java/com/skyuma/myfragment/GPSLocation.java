package com.skyuma.myfragment;

/**
 * Created by alex on 26/3/16.
 */
public class GPSLocation {
    private double lantitude;
    private double longitude;
    private double altitude;
    private long time;
    private float speed;

    public GPSLocation(double altitude, double lantitude, double longitude, float speed, long time) {
        this.altitude = altitude;
        this.lantitude = lantitude;
        this.longitude = longitude;
        this.speed = speed;
        this.time = time;
    }

    public GPSLocation() {
    }

    public double getLantitude() {
        return lantitude;
    }

    public void setLantitude(double lantitude) {
        this.lantitude = lantitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}