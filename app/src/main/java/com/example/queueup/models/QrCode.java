package com.example.queueup.models;

import java.util.UUID;

public class QrCode {
    private String id;
    private String eventId;

    QrCode() {}

    public QrCode(String eventId) {
        this.id = UUID.randomUUID().toString();
        this.eventId = eventId;
    }

    public String getId() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getEventID() {
        return eventId;
    }

    public void setEventID(String eventId) {
        this.eventId = eventId;
    }
}
