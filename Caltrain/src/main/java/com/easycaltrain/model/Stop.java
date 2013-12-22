package com.easycaltrain.model;

public class Stop implements Comparable<Stop> {
    public String stop_id;
    public String stop_name;
    public String stop_desc;
    public Float stop_lat;
    public Float stop_lon;
    public int zone_id;
    public String stop_url;

    public String getStopId() {
        return stop_id;
    }

    public void setStopId(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getStopName() {
        return stop_name;
    }

    public void setStopName(String stop_name) {
        this.stop_name = stop_name;
    }

    public String getStopDesc() {
        return stop_desc;
    }

    public void setStopDesc(String stop_desc) {
        this.stop_desc = stop_desc;
    }

    public Float getStopLat() {
        return stop_lat;
    }

    public void setStopLat(Float stop_lat) {
        this.stop_lat = stop_lat;
    }

    public Float getStopLon() {
        return stop_lon;
    }

    public void setStopLon(Float stop_lon) {
        this.stop_lon = stop_lon;
    }

    public int getZoneId() {
        return zone_id;
    }

    public void setZoneId(int zone_id) {
        this.zone_id = zone_id;
    }

    public String getStopUrl() {
        return stop_url;
    }

    public void setStopUrl(String stop_url) {
        this.stop_url = stop_url;
    }

    public String getName(){
        return getStopId().replace("Caltrain", "").trim();
    }

    @Override
    public int compareTo(Stop b){
        if( getZoneId() > b.getZoneId()){
            return -1;
        } else if(getZoneId() < b.getZoneId()){
            return 1;
        }

        if( getStopLat() > b.getStopLat() ){
            return 1;
        }

        return -1;
    }
}
