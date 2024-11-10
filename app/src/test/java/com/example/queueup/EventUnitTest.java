package com.example.queueup;

import static org.junit.Assert.assertEquals;

import com.example.queueup.models.Event;

import org.junit.Test;

import java.util.Date;

public class EventUnitTest {
    private Event mockEvent() {
        Event mockEvent = new Event("123", "Event Name", "Event Description", "https://www.example.com/image.jpg",
                "Event Location", "Organizer ID", new Date(), new Date(), 100, true);
        return mockEvent;
    }

    @Test
    public void testGetEventName() {
        Event event = mockEvent();
        assertEquals("Event Name", event.getEventName());
        assertEquals("Event Description", event.getEventDescription());
        assertEquals("Event Location", event.getEventLocation());
    }

}
