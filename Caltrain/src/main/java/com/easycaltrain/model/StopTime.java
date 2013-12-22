package com.easycaltrain.model;

public class StopTime {
    public Long _id;
    public String trip_id;
    public String arrival_time;
    public String stop_id;
    public int stop_sequence;

    public String getTripId() {
        return trip_id;
    }

    public void setTripId(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getArrivalTime() {
        return arrival_time;
    }

    public void setArrivalTime(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getStopId() {
        return stop_id;
    }

    public void setStopId(String stop_id) {
        this.stop_id = stop_id;
    }

    public int getStopSequence() {
        return stop_sequence;
    }

    public void setStopSequence(int stop_sequence) {
        this.stop_sequence = stop_sequence;
    }
}
