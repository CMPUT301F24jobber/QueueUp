package com.example.queueup.models;

import java.io.Serializable;

/**
 * Represents an attendee in an event queueing system.
 * Each attendee has attributes such as a unique ID, user ID, event ID, their position in line,
 * and optional geographical location data.
 * This class implements Serializable for object serialization.
 */
public class Attendee implements Serializable {
    private String id;
    private String userId;
    private String eventId;
    private int numberInLine;
    private GeoLocation location;

    /**
     * Default constructor for creating an empty Attendee object.
     */
    public Attendee() {}

    /**
     * Constructs an Attendee with specified user ID and event ID.
     * Initializes the number in line to 0 and sets the ID to the user ID.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event the user is attending.
     */
    public Attendee(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
        this.numberInLine = 0;
        this.id = userId;
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
     * @param userId  The user ID.
     * @param eventId The event ID.
     * @return A unique string ID combining the user ID and event ID.
     */
    public static String generateId(String userId, String eventId) {
        return userId + eventId;
    }

    /**
     * Sets the geographical location of the attendee.
     *
     * @param newLocation The new geographical location of the attendee.
     */
    public void setLocation(GeoLocation newLocation) {
        this.location = newLocation;
    }

    /**
     * Returns the geographical location of the attendee.
     *
     * @return The geographical location of the attendee.
     */
    public GeoLocation getLocation() {
        return location;
    }
}
