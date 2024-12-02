package com.example.queueup.models;

import android.os.Parcel;
import android.os.Parcelable;


public class GeoLocation implements Parcelable {
    private double latitude;
    private double longitude;

    /***
     * Empty constructor for geolocation
     */
    public GeoLocation() {}

    /***
     * Constructor for geolocation requiring longitude and latitude
     * @param latitude
     * @param longitude
     */
    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /***
     * Constructor for geolocation using parcel with latitude and longitude
     * @param in
     */
    protected GeoLocation(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    /***
     * Function for writing geolocation to parcel.
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    /***
     * Required for parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }


    /***
     * Required for parcelable
     */
    public static final Creator<GeoLocation> CREATOR = new Creator<GeoLocation>() {
        @Override
        public GeoLocation createFromParcel(Parcel in) {
            return new GeoLocation(in);
        }

        @Override
        public GeoLocation[] newArray(int size) {
            return new GeoLocation[size];
        }
    };

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
        return "GeoLocation{latitude=" + latitude + ", longitude=" + longitude + "}";
    }
}