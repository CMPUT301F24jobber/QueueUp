package com.example.queueup.controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.queueup.models.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class EventController {
    private static EventController singleton = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventCollectionReference = db.collection("events");

     EventController() {}

    /**
     * idk what to put here
     */
    public static EventController getEventController() {
        if (singleton == null) {
            singleton = new EventController();
        }
        return singleton;
    }

    public void addEvent(Event event) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", event.getId());
        eventData.put("name", event.getName());
        eventData.put("description", event.getDescription());
        eventData.put("image", event.getImageUrl());
        eventData.put("latitude", event.getLatitude());
        eventData.put("longitude", event.getLongitude());
        eventData.put("startDate", event.getStartDate());
        eventData.put("endDate", event.getEndDate());
        Map<String, Object> thing = new HashMap<>();
        thing.put("id", event.getId());

        eventCollectionReference.document(event.getId()).set(eventData);
        eventCollectionReference.add(thing);
    }
    public Task<DocumentSnapshot> getEvent(String id) {
        return eventCollectionReference.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    final Map<String, Object> eventData = document.getData();
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
    
}