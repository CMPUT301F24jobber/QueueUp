package com.example.queueup.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.queueup.controllers.EventController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * ViewModel for managing event-related data and operations.
 * Interacts with EventController to perform CRUD operations and exposes LiveData to the UI.
 */
public class EventViewModel extends ViewModel {

    // LiveData for all events
    private final MutableLiveData<List<Event>> allEventsLiveData = new MutableLiveData<>();

    // LiveData for events by organizer
    private final MutableLiveData<List<Event>> eventsByOrganizerLiveData = new MutableLiveData<>();

    // LiveData for events by attendee
    private final MutableLiveData<List<Event>> eventsByAttendeeLiveData = new MutableLiveData<>();

    // LiveData for selected event details
    private final MutableLiveData<Event> selectedEventLiveData = new MutableLiveData<>();

    // LiveData for announcements of a specific event
    private final MutableLiveData<List<HashMap<String, String>>> announcementListLiveData = new MutableLiveData<>();

    // LiveData for error messages
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    // LiveData for loading states (optional)
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);

    // Instance of EventController
    private final EventController eventController;

    /**
     * Constructor initializes the EventController instance.
     */
    public EventViewModel() {
        this.eventController = EventController.getInstance();
    }

    // -------------------- LiveData Getters --------------------

    /**
     * Returns LiveData containing all events.
     */
    public LiveData<List<Event>> getAllEventsLiveData() {
        return allEventsLiveData;
    }

    /**
     * Returns LiveData containing events filtered by organizer.
     */
    public LiveData<List<Event>> getEventsByOrganizerLiveData() {
        return eventsByOrganizerLiveData;
    }

    /**
     * Returns LiveData containing events filtered by attendee.
     */
    public LiveData<List<Event>> getEventsByAttendeeLiveData() {
        return eventsByAttendeeLiveData;
    }

    /**
     * Returns LiveData containing details of a selected event.
     */
    public LiveData<Event> getSelectedEventLiveData() {
        return selectedEventLiveData;
    }

    /**
     * Returns LiveData containing announcements of a specific event.
     */
    public LiveData<List<HashMap<String, String>>> getAnnouncementListLiveData() {
        return announcementListLiveData;
    }

    /**
     * Returns LiveData containing error messages.
     */
    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    /**
     * Returns LiveData indicating loading states.
     */
    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    // -------------------- Event Fetching Methods --------------------

    /**
     * Fetches all events from the database.
     * Updates allEventsLiveData upon success or errorMessageLiveData upon failure.
     */
    public void fetchAllEvents() {
        isLoadingLiveData.setValue(true);
        eventController.getAllEvents().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) {
                        event.setEventId(doc.getId()); // Ensure eventId is set
                        events.add(event);
                    }
                }
                allEventsLiveData.setValue(events);
                isLoadingLiveData.setValue(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to fetch all events: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Fetches events created by a specific organizer.
     * Updates eventsByOrganizerLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param organizerId The ID of the organizer.
     */
    public void fetchEventsByOrganizer(String organizerId) {
        isLoadingLiveData.setValue(true);
        eventController.getAllEventsByOrganizer(organizerId).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) {
                        event.setEventId(doc.getId());
                        events.add(event);
                    }
                }
                eventsByOrganizerLiveData.setValue(events);
                isLoadingLiveData.setValue(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to fetch events by organizer: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Fetches events that a specific attendee is registered for.
     * Updates eventsByAttendeeLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param attendeeId The ID of the attendee.
     */
    public void fetchEventsByAttendee(String attendeeId) {
        isLoadingLiveData.setValue(true);
        eventController.getEventsByAttendeeId(attendeeId).addOnSuccessListener(new OnSuccessListener<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documentSnapshots) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot doc : documentSnapshots) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) {
                        event.setEventId(doc.getId());
                        events.add(event);
                    }
                }
                eventsByAttendeeLiveData.setValue(events);
                isLoadingLiveData.setValue(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to fetch events by attendee: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Fetches details of a specific event by its ID.
     * Updates selectedEventLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event.
     */
    public void fetchEventById(String eventId) {
        isLoadingLiveData.setValue(true);
        eventController.getEventById(eventId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        event.setEventId(documentSnapshot.getId());
                        selectedEventLiveData.setValue(event);
                    } else {
                        errorMessageLiveData.setValue("Failed to parse event data.");
                    }
                } else {
                    errorMessageLiveData.setValue("Event not found.");
                }
                isLoadingLiveData.setValue(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to fetch event: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Creates a new event.
     * Updates allEventsLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param newEvent The Event object to be created.
     */
    public void createEvent(Event newEvent) {
        isLoadingLiveData.setValue(true);
        // Generate a unique event ID
        String eventId = eventController.generateUniqueEventId();
        newEvent.setEventId(eventId);

        eventController.addEvent(newEvent).addOnSuccessListener(aVoid -> {
            // Optionally, you can fetch all events again or add the new event to LiveData
            fetchAllEvents();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to create event: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Updates an existing event.
     * Updates selectedEventLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param updatedEvent The Event object with updated information.
     */
    public void updateEvent(Event updatedEvent) {
        isLoadingLiveData.setValue(true);
        eventController.updateEvent(updatedEvent).addOnSuccessListener(aVoid -> {
            // Optionally, fetch the updated event details
            fetchEventById(updatedEvent.getEventId());
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to update event: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Deletes an event by its ID.
     * Updates allEventsLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event to be deleted.
     */
    public void deleteEvent(String eventId) {
        isLoadingLiveData.setValue(true);
        eventController.deleteEvent(eventId).addOnSuccessListener(aVoid -> {
            // Optionally, fetch all events again
            fetchAllEvents();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to delete event: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Reactivates an event that was previously deactivated.
     *
     * @param eventId The ID of the event to reactivate.
     */
    public void reactivateEvent(String eventId) {
        isLoadingLiveData.setValue(true);
        eventController.reactivateEvent(eventId).addOnSuccessListener(aVoid -> {
            // Optionally, fetch the updated event details
            fetchEventById(eventId);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to reactivate event: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Ends an event by setting its active status to false.
     *
     * @param eventId The ID of the event to end.
     */
    public void endEvent(String eventId) {
        isLoadingLiveData.setValue(true);
        eventController.endEvent(eventId).addOnSuccessListener(aVoid -> {
            // Optionally, fetch the updated event details
            fetchEventById(eventId);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to end event: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Sets the banner image URL for a specific event.
     *
     * @param eventId  The ID of the event.
     * @param imageUrl The URL of the banner image.
     */
    public void setEventBannerImage(String eventId, String imageUrl) {
        isLoadingLiveData.setValue(true);
        eventController.setEventBannerImage(eventId, imageUrl).addOnSuccessListener(aVoid -> {
            // Optionally, fetch the updated event details
            fetchEventById(eventId);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to set event banner image: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Deletes all events created by a specific user.
     * Updates allEventsLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param userId The ID of the user whose events are to be deleted.
     */
    public void deleteEventsByUser(String userId) {
        isLoadingLiveData.setValue(true);
        eventController.deleteEventsByUser(userId).addOnSuccessListener(aVoid -> {
            // Optionally, fetch all events again
            fetchAllEvents();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to delete events by user: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Registers the current user to an event (joins the waiting list).
     * Updates relevant LiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event to register for.
     */
    public void registerToEvent(String eventId) {
        isLoadingLiveData.setValue(true);
        eventController.registerToEvent(eventId).addOnSuccessListener(aVoid -> {
            // Optionally, fetch the event details again
            fetchEventById(eventId);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to register to event: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Unregisters the current user from an event (leaves the waiting list).
     * Updates relevant LiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event to unregister from.
     */
    public void unregisterFromEvent(String eventId) {
        isLoadingLiveData.setValue(true);
        String userId = CurrentUserHandler.getSingleton().getCurrentUserId();
        eventController.unregisterFromEvent(eventId, userId).addOnSuccessListener(aVoid -> {
            // Optionally, fetch the event details again
            fetchEventById(eventId);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to unregister from event: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Adds an announcement to a specific event.
     * Updates announcementListLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId      The ID of the event.
     * @param announcement The announcement to add as a HashMap.
     */
    public void addAnnouncement(String eventId, HashMap<String, String> announcement) {
        isLoadingLiveData.setValue(true);
        eventController.addAnnouncement(eventId, announcement).addOnSuccessListener(aVoid -> {
            // Fetch the updated list of announcements
            fetchAnnouncements(eventId);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to add announcement: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Fetches announcements for a specific event.
     * Updates announcementListLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event.
     */
    public void fetchAnnouncements(String eventId) {
        isLoadingLiveData.setValue(true);
        eventController.getAnnouncements(eventId).addOnSuccessListener(new OnSuccessListener<List<HashMap<String, String>>>() {
            @Override
            public void onSuccess(List<HashMap<String, String>> announcements) {
                announcementListLiveData.setValue(announcements);
                isLoadingLiveData.setValue(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to fetch announcements: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    // -------------------- Lottery System Methods --------------------

    /**
     * Draws a lottery to select a specified number of attendees from the waiting list.
     * Updates relevant LiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId        The ID of the event.
     * @param numberToSelect The number of attendees to select.
     */
    public void drawLottery(String eventId, int numberToSelect) {
        isLoadingLiveData.setValue(true);
        eventController.drawLottery(eventId, numberToSelect).addOnSuccessListener(new OnSuccessListener<List<String>>() {
            @Override
            public void onSuccess(List<String> selectedAttendees) {
                // Notify selected attendees
                eventController.notifySelectedAttendees(eventId, selectedAttendees).addOnSuccessListener(aVoid -> {
                    // Optionally, fetch updated event details
                    fetchEventById(eventId);
                    isLoadingLiveData.setValue(false);
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to notify selected attendees: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to draw lottery: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Handles the replacement of an attendee if they decline the invitation.
     *
     * @param eventId The ID of the event.
     */
    public void handleReplacement(String eventId) {
        isLoadingLiveData.setValue(true);
        eventController.handleReplacement(eventId).addOnSuccessListener(aVoid -> {
            // Optionally, fetch updated event details
            fetchEventById(eventId);
            isLoadingLiveData.setValue(false);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to handle replacement attendee: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    public void clearErrorMessage() {
        errorMessageLiveData.setValue(null);
    }

    /**
     * Retrieves user announcements for a specific event.
     * Updates announcementListLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event.
     */
    public void fetchUserAnnouncements(String eventId) {
        isLoadingLiveData.setValue(true);
        eventController.getUserAnnouncements(eventId).addOnSuccessListener(new OnSuccessListener<List<HashMap<String, String>>>() {
            @Override
            public void onSuccess(List<HashMap<String, String>> announcements) {
                announcementListLiveData.setValue(announcements);
                isLoadingLiveData.setValue(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to fetch user announcements: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    /**
     * Checks in a user to an event by updating their attendance status.
     * Updates selectedEventLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event.
     */
    public void checkInUser(String eventId) {
        isLoadingLiveData.setValue(true);
        eventController.checkInUser(eventId).addOnSuccessListener(aVoid -> {
            // Optionally, fetch the event details again
            fetchEventById(eventId);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to check-in to event: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

}
