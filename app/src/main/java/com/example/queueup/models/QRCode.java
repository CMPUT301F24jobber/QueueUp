package com.example.queueup.models;

import java.util.UUID;

public class QRCode {
    private String id;
    private String eventId;

    public QRCode() {}

    public QRCode(String eventId) {
        this.id = UUID.randomUUID().toString();
        this.eventId = eventId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

}