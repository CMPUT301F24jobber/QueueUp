package com.example.queueup.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Model class representing an Announcement for an event.
 */
public class Announcement implements Serializable {
    private String id;          // Unique ID for the announcement (Firestore document ID)
    private String eventId;     // ID of the event this announcement belongs to
    private String content;     // The content of the announcement
    private Date timestamp;     // When the announcement was created
    private String authorId;    // ID of the user who created the announcement


    public Announcement(){}

    /**
     * Parameterized constructor to create a new Announcement instance.
     *
     * @param eventId  The ID of the event.
     * @param content  The content of the announcement.
     * @param authorId The ID of the user creating the announcement.
     */
    public Announcement(String eventId, String content, String authorId) {
        this.eventId = eventId;
        this.content = content;
        this.authorId = authorId;
        this.timestamp = new Date(); // Sets the timestamp to the current date and time
    }


    /**
     * Gets the unique ID of the announcement.
     *
     * @return The announcement ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique ID of the announcement.
     * Typically managed by Firestore, but can be set manually if needed.
     *
     * @param id The announcement ID.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the event ID associated with this announcement.
     *
     * @return The event ID.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID associated with this announcement.
     *
     * @param eventId The event ID.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the content of the announcement.
     *
     * @return The announcement content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the announcement.
     *
     * @param content The announcement content.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the timestamp of when the announcement was created.
     *
     * @return The creation timestamp.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of when the announcement was created.
     *
     * @param timestamp The creation timestamp.
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the author ID of the announcement.
     *
     * @return The author ID.
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * Sets the author ID of the announcement.
     *
     * @param authorId The author ID.
     */
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }


    /**
     * Generates a unique announcement ID based on event ID and timestamp.
     * Can be customized based on specific requirements.
     *
     * @param eventId The ID of the event.
     * @param timestamp The timestamp of creation.
     * @return A unique announcement ID.
     */
    public static String generateAnnouncementId(String eventId, Date timestamp) {
        // Example implementation: concatenates eventId with timestamp's milliseconds
        return eventId + "_" + timestamp.getTime();
    }
}
