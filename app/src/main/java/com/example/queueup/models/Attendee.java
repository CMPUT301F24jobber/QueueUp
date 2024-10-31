package com.example.queueup.models;

import java.io.Serializable;

public class Attendee implements Serializable {
    private String id;
    private String userId;
    private String eventId;
    private int numberInLine;
    private GeoLocation location;

    public Attendee() {}

    public Attendee(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
        this.numberInLine = 0;
        this.id = generateId(userId, eventId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getNumberInLine() {
        return numberInLine;
    }

    public void setNumberInLine(int numberInLine) {
        this.numberInLine = numberInLine;
    }

    public static String generateId(String userId, String eventId) {
        return userId + eventId;
    }

    public void setLocation(GeoLocation newLocation) {
        this.location = newLocation;
    }

    public GeoLocation getLocation() {
        return location;
    }
}