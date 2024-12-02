package com.example.queueup.models;

import java.util.UUID;


public class QRCode {
    private String id;
    private String eventId;

    /***
     * Empty constructor for QRCode
     */
    public QRCode() {}

    /***
     * Constructor for QRCode using an event's id.
     * @param eventId
     */
    public QRCode(String eventId) {
        this.id = UUID.randomUUID().toString();
        this.eventId = eventId;
    }

    /**
     * Returns the unique identifier of the QR code.
     *
     * @return The ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the QR code.
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns ID
     *
     * @return The event ID.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID
     *
     * @param eventId
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
