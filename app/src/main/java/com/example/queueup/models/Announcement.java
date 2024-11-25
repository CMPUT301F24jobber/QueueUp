package com.example.queueup.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Model class representing an Announcement for an event.
 */
public class Announcement implements Serializable {
    private String id;
    private String eventId;
    private String content;
    private Date timestamp;
    private String authorId;


    public Announcement(){}

    /**
     * Creates a new announcement with the given event ID, content, and author ID.
     *
     * @param eventId
     * @param content
     * @param authorId
     */
    public Announcement(String eventId, String content, String authorId) {
        this.eventId = eventId;
        this.content = content;
        this.authorId = authorId;
        this.timestamp = new Date();
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
     *
     * @param id
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
     * @param eventId
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the content of the announcement.
     *
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the announcement.
     *
     * @param content
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
     * Generates a unique id
     *
     * @param eventId
     * @param timestamp
     * @return A unique announcement ID.
     */
    public static String generateAnnouncementId(String eventId, Date timestamp) {
        // Example implementation: concatenates eventId with timestamp's milliseconds
        return eventId + "_" + timestamp.getTime();
    }
}
