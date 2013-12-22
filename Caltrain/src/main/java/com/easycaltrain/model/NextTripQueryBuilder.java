package com.easycaltrain.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class NextTripQueryBuilder {

    private static final String TIME_ZONE = "GMT-08";
    private static final int HOURS_AHEAD = 23;

    private String mStopTimeTableName;
    private String mTripTableName;

    private Stop mFromStop;
    private Stop mToStop;

    public NextTripQueryBuilder(Stop fromStop, Stop toStop){
        mStopTimeTableName= cupboard().getTable(StopTime.class);
        mTripTableName = cupboard().getTable(Trip.class);
        mFromStop = fromStop;
        mToStop = toStop;
    }

    /*
    * Given 2 stops finds the scheduled StopTime in the next hours
    *
    * @todo: take into account holidays
    */
    public String build(){
        String rawQuery = nextTripsQueryPart(true)
               + " UNION "
               +  nextTripsQueryPart(false)
               + " ORDER BY dayorder ASC, arrival_time ASC";

        return rawQuery;
    }

    private String nextTripsQueryPart(boolean first){

        Calendar rightNow = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        Calendar nextHours = (Calendar) rightNow.clone();
        nextHours.add(Calendar.HOUR, HOURS_AHEAD);

        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

        String joinDestination = " EXISTS (SELECT 1 FROM " + mStopTimeTableName + " AS st2"
                + " WHERE st2.trip_id = st1.trip_id "
                + " AND st2.stop_sequence > st1.stop_sequence"
                + " AND st2.stop_id = '" + mToStop.getStopId() + "' )";

        String timeClause;
        if(first){
            timeClause = "arrival_time > '" + sdf.format(rightNow.getTime()) + "'";
        } else {
            timeClause = "arrival_time < '" + sdf.format(nextHours.getTime()) + "'";
        }

        String dayorder = (first ? "0" : "1");

        String rawQuery = "SELECT st1.*, " + dayorder + " AS dayorder"
                + " FROM " + mStopTimeTableName + " AS st1, " + mTripTableName + " AS t"
                + " WHERE st1.trip_id = t.trip_id"
                + " AND " + weekClause( first ? rightNow.getTime() : nextHours.getTime() )
                + " AND " + timeClause
                + " AND stop_id = '" + mFromStop.getStopId() + "'"
                + " AND " + joinDestination;
               // + " ORDER BY arrival_time ASC";

        return rawQuery;
    }

    private String weekClause(Date date){
        final SimpleDateFormat sdf = new SimpleDateFormat("F");
        sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        int weekDay = new Integer( sdf.format(date) );

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
