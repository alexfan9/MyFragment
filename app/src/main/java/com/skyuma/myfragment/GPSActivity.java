package com.skyuma.myfragment;

/**
 * Created by afan on 2016/4/8.
 */
public class GPSActivity {
    private String name;
    private long _datetime;
    private String _timezone;
    private int _new;

    public int get_new() {
        return _new;
    }

    public void set_new(int _new) {
        this._new = _new;
    }

    public GPSActivity(String name, long _datetime, String _timezone) {
        this.name = name;
        this._datetime = _datetime;
        this._timezone = _timezone;
    }

    public GPSActivity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long get_datetime() {
        return _datetime;
    }

    public void set_datetime(long _datetime) {
        this._datetime = _datetime;
    }

    public String get_timezone() {
        return _timezone;
    }

    public void set_timezone(String _timezone) {
        this._timezone = _timezone;
    }
}
