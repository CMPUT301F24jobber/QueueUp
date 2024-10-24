package com.example.queueup;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;

import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testEvent() {
        Event event = new Event("id", "name", "desc", "www.google.com", 0.0, 0.0, new Date(), new Date());
        EventController controller = EventController.getEventController();
        controller.addEvent(event);
    }
}