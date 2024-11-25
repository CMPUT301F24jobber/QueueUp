package com.example.queueup.models;

public class GeoLocation {
    private double latitude;
    private double longitude;


    public GeoLocation() {}


    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the latitude of the geographical location.
     *
     * @return The latitude value.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the geographical location.
     *
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the longitude of the geographical location.
     *
     * @return The longitude value.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the geographical location.
     *
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
