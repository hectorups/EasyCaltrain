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

    private final SimpleDateFormat weekdayFormat = new SimpleDateFormat("F");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private Calendar mRightNow;
    private Calendar mNextHours;

    public NextTripQueryBuilder(Stop fromStop, Stop toStop){
        mStopTimeTableName= cupboard().getTable(StopTime.class);
        mTripTableName = cupboard().getTable(Trip.class);
        mFromStop = fromStop;
        mToStop = toStop;

        weekdayFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        timeFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
    }

    private void setupCalendars(){
        mRightNow = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        mNextHours = (Calendar) mRightNow.clone();
        mNextHours.add(Calendar.HOUR, HOURS_AHEAD);
    }

    /*
    * Given 2 stops finds the scheduled StopTime in the next hours
    *
    * @todo: take into account holidays
    */
    public String build(){
        setupCalendars();

        String rawQuery = nextTripsQueryPart(true);

        if( !isOneDayQuery() ){
            rawQuery += " UNION " +  nextTripsQueryPart(false);
        }

        rawQuery += " ORDER BY dayorder ASC, arrival_time ASC";

        return rawQuery;
    }

    private String nextTripsQueryPart(boolean firstQuery){
        String timeClause;
        if(firstQuery){
            timeClause = "arrival_time > '" + timeFormat.format(mRightNow.getTime()) + "'";
        } else {
            timeClause = "arrival_time < '" + timeFormat.format(mNextHours.getTime()) + "'";
        }

        String dayorder = (firstQuery ? "0" : "1");

        String rawQuery = "SELECT st1.*, " + dayorder + " AS dayorder"
                + " FROM " + mStopTimeTableName + " AS st1, " + mTripTableName + " AS t"
                + " WHERE st1.trip_id = t.trip_id"
                + " AND " + weekClause( firstQuery ? mRightNow.getTime() : mNextHours.getTime() )
                + " AND " + timeClause
                + " AND stop_id = '" + mFromStop.getStopId() + "'"
                + " AND " + joinDestination();
               // + " ORDER BY arrival_time ASC";

        return rawQuery;
    }

    private String joinDestination(){
        return " EXISTS (SELECT 1 FROM " + mStopTimeTableName + " AS st2"
                + " WHERE st2.trip_id = st1.trip_id "
                + " AND st2.stop_sequence > st1.stop_sequence"
                + " AND st2.stop_id = '" + mToStop.getStopId() + "' )";
    }

    private String weekClause(Date date){
        int weekDay = new Integer( weekdayFormat.format(date) );

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

    private boolean isOneDayQuery(){
        return ( weekdayFormat.format(mRightNow.getTime()) == weekdayFormat.format(mNextHours.getTime()) );
    }
}
