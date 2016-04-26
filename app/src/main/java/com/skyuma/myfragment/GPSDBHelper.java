package com.skyuma.myfragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alex on 26/3/16.
 */
public class GPSDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "running.db";
    private static final int DATABASE_VERSION = 1;

    public GPSDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("createing database");
        db.execSQL("CREATE TABLE IF NOT EXISTS activity" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, _lantitude REAL, _longitude REAL, _altitude REAL,_speed REAL,_time INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS activity_list" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, _activity TEXT, _datetime INTEGER, _atimezone TEXT)");
        System.out.println("database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE location ADD COLUMN other STRING");
    }

    public void backup(SQLiteDatabase db, String tableName){
        db.execSQL("CREATE TABLE " + tableName +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, _lantitude REAL, _longitude REAL, _altitude REAL,_speed REAL,_time INTEGER)");

        db.execSQL("insert into " + tableName +
                " select * from activity");
    }
}
