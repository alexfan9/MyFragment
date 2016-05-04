package com.skyuma.myfragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class GPSDBManager {
    private static final String LTAG = GPSDBManager.class.getSimpleName();
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
            GPSLocation location = gpsLocation;
            System.out.println("Latitude:" + location.getLantitude() + "Longitude:" + location.getLongitude());
            db.execSQL("INSERT INTO activity VALUES(null, ?, ?, ?, ?, ?)",
                    new Object[]{gpsLocation.getLantitude(), gpsLocation.getLongitude(), gpsLocation.getAltitude(), gpsLocation.getSpeed(), gpsLocation.getTime()});
            db.setTransactionSuccessful();	//设置事务成功完成
        } finally {
            db.endTransaction();	//结束事务
        }
    }
    public void createActivity(String activity){
        db.beginTransaction();
        try {
            long dateToken = System.currentTimeMillis();
            String name = activity + dateToken;
            TimeZone tz = TimeZone.getDefault();
            String strTimeZone = tz.getDisplayName();

            db.execSQL("INSERT INTO activity_list VALUES(null, ?, ?, ?, ?)",
                    new Object[]{name, dateToken, strTimeZone, 1});
            gpsdbHelper.backup(db, name);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public void deleteActivity(String name){
        db.beginTransaction();	//开始事务
        try {
            db.execSQL("drop table if exists " + name);
            db.execSQL("DELETE FROM activity_list where _activity = '" + name + "'");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteActivityList(){
        db.beginTransaction();	//开始事务
        try {
            Cursor c = db.rawQuery("SELECT * FROM activity_list", null);
            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndex("_activity"));
                db.execSQL("drop table if exists " + name);
            }
            db.execSQL("DELETE FROM activity_list");
            db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ = 0 WHERE NAME = 'activity_list'");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void saveActivity(GPSActivity gpsActivity){
        db.beginTransaction();
        try {
            int count = 0;
            String sql = "SELECT * FROM activity_list where _activity = '"+ gpsActivity.getName() +"'";
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                count ++;
            }
            if (count > 0){
                return;
            }
            db.execSQL("INSERT INTO activity_list VALUES(null, ?, ?, ?, ?)",
                    new Object[]{gpsActivity.getName(), gpsActivity.get_datetime(), gpsActivity.get_timezone(), gpsActivity.get_new()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void setActivityUnRead(String name){
        db.beginTransaction();
        try {
            String sql = "update activity_list set _new = 0 where _activity = '"+ name +"'";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void saveActivityContent(String name, ArrayList<GPSLocation> gpsLocations){
        db.beginTransaction();
        try {
            int count = 0;
            String sql = String.format("select count(*) from sqlite_master where type='table' and name='%s'", name);
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                count = c.getInt(0);
                Log.d(LTAG, "saveActivityContent count : " + count);
            }
            c.close();
            if (count > 0){
                return;
            }
            db.execSQL("CREATE TABLE " + name +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "_lantitude REAL, _longitude REAL, _altitude REAL," +
                    "_speed REAL,_time INTEGER)");

            Iterator iterator = gpsLocations.iterator();
            while (iterator.hasNext()){
                GPSLocation gpsLocation = (GPSLocation) iterator.next();
                db.execSQL("INSERT INTO " + name +
                                " VALUES(null, ?, ?, ?, ?, ?)",
                        new Object[]{gpsLocation.getLantitude(),
                                gpsLocation.getLongitude(),
                                gpsLocation.getAltitude(),
                                gpsLocation.getSpeed(),
                                gpsLocation.getTime()});

            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void saveActivityContent2(String name, String content){
        db.beginTransaction();
        try {
            int count = 0;
            String sql = String.format("select count(*) from sqlite_master where type='table' and name='%s'", name);
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                count = c.getInt(0);
                Log.d(LTAG, "saveActivityContent count : " + count);
            }
            c.close();
            if (count > 0){
                return;
            }
            db.execSQL("CREATE TABLE " + name +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "_lantitude REAL, _longitude REAL, _altitude REAL," +
                    "_speed REAL,_time INTEGER)");

            JSONArray jsonArray = new JSONArray(content);
            System.out.println(jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++){
                JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                db.execSQL("INSERT INTO " + name +
                                " VALUES(null, ?, ?, ?, ?, ?)",
                        new Object[]{jsonArray1.getDouble(1),
                                jsonArray1.getDouble(2),
                                jsonArray1.getDouble(3),
                                jsonArray1.getDouble(4),
                                jsonArray1.getLong(5)});
            }
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
    public void clear(){
        db.beginTransaction();	//开始事务
        try {
            String table = "activity";
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
        Cursor c = db.rawQuery("SELECT * FROM activity", null);
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

    public ArrayList<GPSActivity> getActivities() {
        ArrayList<GPSActivity> gpsActivities = new ArrayList<GPSActivity>();
        try {
            Cursor c = db.rawQuery("SELECT * FROM activity_list order by _datetime desc", null);
            while (c.moveToNext()) {
                GPSActivity gpsActivity = new GPSActivity();
                gpsActivity.setName(c.getString(c.getColumnIndex("_activity")));
                gpsActivity.set_datetime(c.getLong(c.getColumnIndex("_datetime")));
                gpsActivity.set_timezone(c.getString(c.getColumnIndex("_atimezone")));
                gpsActivity.set_new(c.getInt(c.getColumnIndex("_new")));
                gpsActivities.add(gpsActivity);
            }
            c.close();
        }catch (SQLiteException exception){

        }
        return gpsActivities;
    }

    public JSONArray getActivityContent(String s) {
        JSONArray jsonArray = new JSONArray();
        String sql = "SELECT * FROM " + s ;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("latitude", c.getDouble(c.getColumnIndex("_lantitude")));
                jsonObject.put("longitude", c.getDouble(c.getColumnIndex("_longitude")));
                jsonObject.put("altitude", c.getDouble(c.getColumnIndex("_altitude")));
                jsonObject.put("speed", c.getFloat(c.getColumnIndex("_speed")));
                jsonObject.put("date", c.getLong(c.getColumnIndex("_time")));
                jsonArray.put(jsonObject);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        if (jsonArray.length() == 0){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("latitude", 65.9667);
                jsonObject.put("longitude", -18.5333);
                jsonObject.put("altitude", 15.0444);
                jsonObject.put("speed", 3.0);
                long date = 1461561073;
                jsonObject.put("date", date);
                jsonArray.put(jsonObject);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        c.close();
        return jsonArray;
    }
    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
