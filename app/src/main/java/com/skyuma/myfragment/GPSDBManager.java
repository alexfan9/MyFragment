package com.skyuma.myfragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GPSDBManager {
    private GPSDBHelper gpsdbHelper;
    private SQLiteDatabase db;

    public GPSDBManager(Context context) {
        gpsdbHelper = new GPSDBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = gpsdbHelper.getWritableDatabase();
    }

    /**
     * add persons
     * @param gpsLocation
     */
    public void add(GPSLocation gpsLocation) {
        db.beginTransaction();	//开始事务
        try {
            db.execSQL("INSERT INTO location VALUES(null, ?, ?, ?, ?, ?)",
                    new Object[]{gpsLocation.getLantitude(),gpsLocation.getLongitude(),gpsLocation.getAltitude(),gpsLocation.getSpeed(),gpsLocation.getTime()});
            db.setTransactionSuccessful();	//设置事务成功完成
        } finally {
            db.endTransaction();	//结束事务
        }
    }
    public void createActivity(String activity){
        gpsdbHelper.backup(db, activity);
        db.beginTransaction();
        try {
            String strDate = Settings.System.DATE_FORMAT;
            String strTime = Settings.System.TIME_12_24;
            long dateToken = System.currentTimeMillis();
            TimeZone tz = TimeZone.getDefault();
            String strTimeZone = tz.getDisplayName();
            String StrTimeId = tz.getID();

            db.execSQL("INSERT INTO activities VALUES(null, ?, ?, ?)",
                    new Object[]{activity, dateToken, strTimeZone});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public void clear(){
        db.beginTransaction();	//开始事务
        try {
            String table = "location";
            db.execSQL("DELETE FROM " + table);
            db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ = 0 WHERE NAME = ' " + table + "'");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * query all GPSLocation, return list
     * @return List<GPSLocation>
     */
    public List<GPSLocation> query() {
        ArrayList<GPSLocation> locations = new ArrayList<GPSLocation>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            GPSLocation location = new GPSLocation();
            location.setLantitude(c.getDouble(c.getColumnIndex("_lantitude")));
            location.setLongitude(c.getDouble(c.getColumnIndex("_longitude")));
            location.setAltitude(c.getDouble(c.getColumnIndex("_altitude")));
            location.setSpeed(c.getFloat(c.getColumnIndex("_speed")));
            location.setTime(c.getLong(c.getColumnIndex("_time")));
            locations.add(location);
        }
        c.close();
        return locations;
    }

    public List<GPSActivity> getActivities() {
        List<GPSActivity> gpsActivities = new ArrayList<GPSActivity>();
        Cursor c = db.rawQuery("SELECT * FROM activities", null);
        while (c.moveToNext()) {
            GPSActivity gpsActivity = new GPSActivity();
            gpsActivity.setName(c.getString(c.getColumnIndex("_activity")));
            gpsActivity.set_datetime(c.getLong(c.getColumnIndex("_datetime")));
            gpsActivity.set_timezone(c.getString(c.getColumnIndex("_atimezone")));
            gpsActivities.add(gpsActivity);
        }
        c.close();
        return gpsActivities;
    }

    /**
     * query all persons, return cursor
     * @return	Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM location", null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
