package com.example.queueup;

import static org.junit.Assert.assertEquals;

import com.example.queueup.models.Event;

import org.junit.Test;

import java.util.Date;

public class EventUnitTest {
    private Event mockEvent() {
        return new Event(
                "123",
                "Event Name",
                "Event Description",
                "https://www.example.com/image.jpg",
                "Location",
                "Organizer",
                new Date(),
                new Date(),
                100,
                true,  // isActive
                false,  // isPublic
                null
        );
    }

    @Test
    public void testGetEventName() {
        Event event = mockEvent();
        assertEquals("Event Name", event.getEventName());
        assertEquals("Event Description", event.getEventDescription());
    }

}
