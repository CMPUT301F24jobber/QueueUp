package com.example.queueup.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.UUID;

public class Facility implements Parcelable {

    private String uuid;
    private String name;
    private String details; // Add details field
    private String location;
    private String imageUrl;

    public Facility() {

    }

    public Facility(String name, String details, String location, String imageUrl) {
        this();
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.details = details; // Initialize details
        this.location = location;
        this.imageUrl = imageUrl;
    }

    protected Facility(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        details = in.readString(); // Read details
        location = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Facility> CREATOR = new Creator<Facility>() {
        @Override
        public Facility createFromParcel(Parcel in) {
            return new Facility(in);
        }

        @Override
        public Facility[] newArray(int size) {
            return new Facility[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details; // Add getter for details
    }

    public void setDetails(String details) { // Add setter for details
        this.details = details;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(name);
        dest.writeString(details); // Write details
        dest.writeString(location);
        dest.writeString(imageUrl);
    }

    @Override
    public String toString() {
        return name;
    }
}
