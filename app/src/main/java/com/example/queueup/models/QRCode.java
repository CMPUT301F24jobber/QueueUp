package com.example.queueup.models;

import java.util.UUID;

/**
 * Represents a QR code associated with an event.
 * It contains a unique identifier (ID) and an event ID.
 */
public class QRCode {
    private String id;
    private String eventId;

    /**
     * Default constructor that creates an empty QRCode object.
     */
    public QRCode() {}

    /**
     * Constructs a QRCode object with a specified event ID and generates a unique ID.
     *
     * @param eventId The ID of the event associated with the QR code.
     */
    public QRCode(String eventId) {
        this.id = UUID.randomUUID().toString();
        this.eventId = eventId;
    }

    /**
     * Returns the unique identifier of the QR code.
     *
     * @return The unique identifier (ID) of the QR code.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the QR code.
     *
     * @param id The new unique identifier (ID) of the QR code.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the event ID associated with the QR code.
     *
     * @return The event ID.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID associated with the QR code.
     *
     * @param eventId The new event ID to associate with the QR code.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
