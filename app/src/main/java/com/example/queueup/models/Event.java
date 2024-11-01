package com.example.queueup.models;

import java.time.LocalDate;

public class Event {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private double longitude;
    private double latitude;
    private LocalDate startDate;
    private LocalDate endDate;
    private int attendeeLimit;
    private boolean unlimitedAttendee;

    public Event() {}

    public Event(String id, String name, String description, String imageUrl, double longitude, double latitude, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.longitude = longitude;
        this.latitude = latitude;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Event(String name, String description, String location, LocalDate startDate, LocalDate endDate, int attendeeLimit, boolean unlimitedAttendee) {
        this.name = name;
        this.description = description;
        this.longitude = 0.0;
        this.latitude = 0.0;
        this.startDate = startDate;
        this.endDate = endDate;
        this.attendeeLimit = attendeeLimit;
        this.unlimitedAttendee = unlimitedAttendee;
    }

    public int getAttendeeLimit() {
        return attendeeLimit;
    }

    public void setAttendeeLimit(int attendeeLimit) {
        this.attendeeLimit = attendeeLimit;
    }

    public boolean isUnlimitedAttendee() {
        return unlimitedAttendee;
    }

    public void setUnlimitedAttendee(boolean unlimitedAttendee) {
        this.unlimitedAttendee = unlimitedAttendee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLocation() {
        return this.longitude + " " + this.latitude;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageURL='" + imageUrl + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", attendeeLimit=" + attendeeLimit +
                ", unlimitedAttendee=" + unlimitedAttendee +
                '}';
    }
}
