package com.eggbot.app;

public class LocationSample {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "LocationSample{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
