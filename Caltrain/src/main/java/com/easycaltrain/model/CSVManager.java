package com.easycaltrain.model;

import android.content.res.Resources;

import com.easycaltrain.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class CSVManager {

    private Resources mResources;
    // Memoize stops
    private ArrayList<Stop> mStops;

    public CSVManager(Resources resources){
        mResources = resources;
    }

    public ArrayList<Stop> getStops(){

        if(mStops != null) return mStops;

        CSVConverter<Stop> mCSVConverter = new CSVConverter<>(R.raw.stops);

        mStops = mCSVConverter.getModels( new LineConverter<Stop>(){
            @Override
            public Stop lineToModel(String[] line){
                Stop stop = new Stop();
                stop.setStopId(line[0]);
                stop.setStopName(line[1]);
                stop.setStopDesc(line[2]);
                stop.setStopLat(new Float(line[3]));
                stop.setStopLon(new Float(line[4]));
                stop.setZoneId(new Integer(line[5]));
                return stop;
            }
        });

        return mStops;
    }

    public Stop getStop(String stopId){
        ArrayList<Stop> stops = getStops();

        Stop stop = null;
        for(int i = 0; i < stops.size(); i++){
            if( stopId.compareTo(stops.get(i).getStopId()) == 0 ){
                stop = stops.get(i);
                break;
            }
        }

        return stop;
    }

    public ArrayList<Trip> getTrips(){

        CSVConverter<Trip> mCSVConverter = new CSVConverter<>(R.raw.trips);

        return mCSVConverter.getModels( new LineConverter<Trip>(){
            @Override
            public Trip lineToModel(String[] line){
                Trip trip = new Trip();
                trip.setTripId(line[0]);
                trip.setTripShortName(new Integer(line[1]));
                trip.setRouteId(line[2]);
                trip.setServiceId(line[3]);
                trip.setTripHeadsign(line[4]);
                trip.setDirectionId(line[5]);
                return trip;
            }
        });
    }

    public ArrayList<StopTime> getStopTimes(){

        CSVConverter<StopTime> mCSVConverter = new CSVConverter<>(R.raw.stop_times);

        return mCSVConverter.getModels( new LineConverter<StopTime>(){
            @Override
            public StopTime lineToModel(String[] line){
                StopTime stopTime = new StopTime();
                stopTime.setTripId(line[0]);
                stopTime.setArrivalTime(line[1]);
                stopTime.setStopId(line[3]);
                stopTime.setStopSequence(new Integer(line[4]));
                return stopTime;
            }
        });
    }


   /*
    *   Private Classes
    */

    private interface LineConverter<T> {
        public T lineToModel(String[] line);
    }

    private class CSVConverter<T> {
        private InputStream mReader;

        public CSVConverter(int resourceId){
            InputStream reader = mResources.openRawResource(resourceId);
            mReader = reader;
        }

        private List getCSVLines(){
            List raw_stops = null;
            try{
                InputStreamReader city_csv = new InputStreamReader( mReader );
                CSVReader csvReader = new CSVReader( city_csv );
                raw_stops = csvReader.readAll();
            } catch(IOException e){}

            return raw_stops;
        }

        public ArrayList<T> getModels(LineConverter<T> lineConverter){
            ArrayList<T> models = new ArrayList<>();
            List lines = getCSVLines();

            // Skip the first line (titles)
            for(int i = 1; i < lines.size(); i++ ){
                String[] line = (String[])lines.get(i);
                if( line.length < 2 ){ continue; }
                models.add( lineConverter.lineToModel(line) );
            }

            return models;
        }
    }


}
