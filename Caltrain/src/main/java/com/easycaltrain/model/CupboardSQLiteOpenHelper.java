package com.easycaltrain.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
    * @todo: make it work for searches that span >2 days
    * @todo: take into account holidays
    */
    public Cursor nextTrips(Stop fromStop, Stop toStop){
        String stopTimeTableName = cupboard().getTable(StopTime.class);
        String tripTableName = cupboard().getTable(Trip.class);

        Calendar rightNow = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        Calendar nextHours = (Calendar) rightNow.clone();
        nextHours.add(Calendar.MINUTE, 190);

        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	    sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

        String joinDestination = " EXISTS (SELECT 1 FROM " + stopTimeTableName + " AS st2"
                + " WHERE st2.trip_id = st1.trip_id "
                + " AND st2.stop_sequence > st1.stop_sequence"
                + " AND st2.stop_id = '" + toStop.getStopId() + "' )";


        String rawQuery = "SELECT st1.* "
                          + " FROM " + stopTimeTableName + " AS st1, " + tripTableName + " AS t"
                          + " WHERE st1.trip_id = t.trip_id"
                          + " AND " + weekClause()
                          + " AND arrival_time > '" + sdf.format(rightNow.getTime()) + "'"
                          + " AND arrival_time < '" + sdf.format(nextHours.getTime()) + "'"
                          + " AND stop_id = '" + fromStop.getStopId() + "'"
                          + " AND " + joinDestination
                          + " ORDER BY arrival_time ASC";

        return getWritableDatabase().rawQuery(rawQuery, null);
    }

    private String weekClause(){
        final SimpleDateFormat sdf = new SimpleDateFormat("F");
        sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        int weekDay = new Integer( sdf.format(new Date()) );

        String clause = "";
        if( weekDay <= 5){
            clause = "t.service_id LIKE 'WD_%'";
        } else if( weekDay == 6 ) {
            clause = "( t.service_id LIKE 'WE_%' OR t.service_id LIKE 'ST_%' )";
        } else {
            clause = "t.service_id LIKE 'WE_%'";
        }

        return clause;
    }


}
