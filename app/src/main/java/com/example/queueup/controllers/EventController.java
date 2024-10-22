package com.example.queueup.controllers;

import com.example.queueup.models.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class EventController {
    private static EventController singleton = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventCollectionReference = db.collection("events");

     EventController() {}

    /**
     * Gets the singleton instance of the EventController.
     * @return The UserController instance.
     */
    public static EventController getEventController() {
        if (singleton == null) {
            singleton = new EventController();
        }
        return singleton;
    }

    /**
     *
     * @param event
     */
    public Task<Void> createEvent(Event event) {
        return eventCollectionReference.document(event.getId()).set(event);
    }
    public Task<Void> updateEvent(Event event) {
        return eventCollectionReference.document(event.getId()).set(event);
    }
    public Task<Void> updateEventbyId(String id, String field, Object value) {
        return eventCollectionReference.document(id).update(field, value);
    }
    public Task<DocumentSnapshot> getEvent(String id) {
        return eventCollectionReference.document(id).get();
    }
    
}