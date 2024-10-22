package com.example.queueup.controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.queueup.models.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("id", event.getId());
        eventMap.put("name", event.getName());
        eventMap.put("description", event.getDescription());
        eventMap.put("image", event.getImageUrl());
        eventMap.put("latitude", event.getLatitude());
        eventMap.put("longitude", event.getLongitude());
        eventMap.put("startDate", event.getStartDate());
        eventMap.put("endDate", event.getEndDate());
        Map<String, Object> thing = new HashMap<>();
        thing.put("id", event.getId());

        eventCollectionReference.document(event.getId()).set(eventMap);
        eventCollectionReference.add(thing);
    }
    public void getEvent(String id) {
        eventCollectionReference.document(id).getData();
    }
    
}