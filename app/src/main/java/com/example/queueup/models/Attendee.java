package com.example.queueup.models;

import java.io.Serializable;


public class Attendee implements Serializable {
    private String id;
    private String userId;
    private String eventId;
    private int numberInLine;
    private GeoLocation location;
    private String status;

    /***
     * Empty constructor for Attendee
     */
    public Attendee() {}

    /***
     * Constructor for Attendee without geolocation
     * @param userId
     * @param eventId
     */
    public Attendee(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
        this.id = generateId(userId, eventId);
        this.numberInLine = 0;
        this.status = "pending";
        this.location = null;
    }

    /***
     * Constructor for Attendee with geolocation
     * @param userId
     * @param eventId
     * @param location
     */
    public Attendee(String userId, String eventId, GeoLocation location) {
        this.userId = userId;
        this.eventId = eventId;
        this.id = generateId(userId, eventId);
        this.numberInLine = 0;
        this.status = "pending";
        this.location = location;
    }


    /**
     * Returns the ID of the attendee.
     *
     * @return The ID of the attendee.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the attendee.
     *
     * @param id The new ID for the attendee.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the user ID associated with this attendee.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID for this attendee.
     *
     * @param userId The new user ID for the attendee.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the event ID associated with this attendee.
     *
     * @return The event ID.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID for this attendee.
     *
     * @param eventId The new event ID for the attendee.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the position of the attendee in line.
     *
     * @return The position of the attendee in line.
     */
    public int getNumberInLine() {
        return numberInLine;
    }

    /**
     * Returns the status of the attendee in line.
     *
     * @return The status of the attendee in line.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the attendee in line.
     *
     * @param status The new status of the attendee in line.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the position of the attendee in line.
     *
     * @param numberInLine The new position of the attendee in line.
     */
    public void setNumberInLine(int numberInLine) {
        this.numberInLine = numberInLine;
    }

    /**
     * Generates a unique ID for an attendee based on the user ID and event ID.
     *
     * @param userId
     * @param eventId
     * @return The unique ID for the attendee.
     */
    public static String generateId(String userId, String eventId) {
        return userId + eventId;
    }

    /**
     * Sets the geographical location of the attendee.
     *
     * @param newLocation
     */
    public void setLocation(GeoLocation newLocation) {
        this.location = newLocation;
    }

    /**
     * Returns the geographical location of the attendee.
     *
     * @return
     */
    public GeoLocation getLocation() {
        return location;
    }
}
