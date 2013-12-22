package com.easycaltrain.model;

public class Trip {
    public Long _id;
    public String trip_id;
    public int trip_short_name;
    public String route_id;
    public String service_id;
    public String trip_headsign;
    public String direction_id;

    public String getTripId() {
        return trip_id;
    }

    public void setTripId(String trip_id) {
        this.trip_id = trip_id;
    }

    public int getTripShortName() {
        return trip_short_name;
    }

    public void setTripShortName(int trip_short_name) {
        this.trip_short_name = trip_short_name;
    }

    public String getRouteId() {
        return route_id;
    }

    public void setRouteId(String route_id) {
        this.route_id = route_id;
    }

    public String getServiceId() {
        return service_id;
    }

    public void setServiceId(String service_id) {
        this.service_id = service_id;
    }

    public String getTripHeadsign() {
        return trip_headsign;
    }

    public void setTripHeadsign(String trip_headsign) {
        this.trip_headsign = trip_headsign;
    }

    public String getDirectionId() {
        return direction_id;
    }

    public void setDirectionId(String direction_id) {
        this.direction_id = direction_id;
    }
}
