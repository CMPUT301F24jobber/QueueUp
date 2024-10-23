package com.example.queueup.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;
import com.google.android.gms.tasks.Task;

public class EventViewModel extends AndroidViewModel {

    private final EventController eventController;
    private final MutableLiveData<Event> currentEvent = new MutableLiveData<>();

    public EventViewModel(@NonNull Application application) {
        super(application);
        eventController = EventController.getEventController();
    }
    public Task<Void> createEvent (Event event) {
        return eventController.createEvent(event).addOnSuccessListener( result -> currentEvent.setValue(event) );
    }
    public Task<Void> updateEvent (Event event) {
        return eventController.createEvent(event).addOnSuccessListener( result -> currentEvent.setValue(event) );
    }

//    public Task<Void> deleteEvent () {
//        return eventController.deleteEvent(currentEvent.getValue().getId()).addOnSuccessListener( result -> currentEvent.setValue(event) );
//    }



}
