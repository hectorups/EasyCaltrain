package com.easycaltrain.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class CupboardSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "easyCaltrain.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TIME_ZONE = "GMT-08";

    CSVManager mCSVManager;

    static {
        // register our models
        cupboard().register(StopTime.class);
        cupboard().register(Trip.class);
    }

    public CupboardSQLiteOpenHelper(Context context, CSVManager csvManager) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCSVManager = csvManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
        resetTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
        resetTables(db);
    }


    private void resetTables(SQLiteDatabase db){
        cupboard().withDatabase(db).delete(StopTime.class, null, null);
        ArrayList<StopTime>  stopTimes = mCSVManager.getStopTimes();
        for(StopTime stopTime : stopTimes){
            cupboard().withDatabase(db).put(stopTime);
        }

        cupboard().withDatabase(db).delete(Trip.class, null, null);
        ArrayList<Trip> trips = mCSVManager.getTrips();
        for(Trip trip : trips){
            cupboard().withDatabase(db).put(trip);
        }
    }

    public void forceReset(){
        resetTables(getWritableDatabase());
    }

   /*
    * Given 2 stops finds the scheduled StopTime in the next hours
    *
    */
    public Cursor nextTrips(Stop fromStop, Stop toStop){
        final NextTripQueryBuilder nextTripQueryBuilder = new NextTripQueryBuilder(fromStop, toStop);
        Cursor cursor = getWritableDatabase().rawQuery( nextTripQueryBuilder.build(), null);
        return cursor;
    }



}
