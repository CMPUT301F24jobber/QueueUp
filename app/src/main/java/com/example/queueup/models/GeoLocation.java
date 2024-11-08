package com.example.queueup.models;

/**
 * Represents a geographical location with latitude and longitude coordinates.
 * It provides methods to set and get the latitude and longitude values.
 */
public class GeoLocation {
    private double latitude;
    private double longitude;

    /**
     * Default constructor that creates an empty GeoLocation object.
     */
    public GeoLocation() {}

    /**
     * Constructs a GeoLocation object with specified latitude and longitude.
     *
     * @param latitude  The latitude of the geographical location.
     * @param longitude The longitude of the geographical location.
     */
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
     * @param latitude The new latitude value to be set.
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
     * @param longitude The new longitude value to be set.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
