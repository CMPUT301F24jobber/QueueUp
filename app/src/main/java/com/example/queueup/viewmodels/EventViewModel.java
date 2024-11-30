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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EventViewModel extends ViewModel {

    private final MutableLiveData<List<Event>> allEventsLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<Event>> eventsByOrganizerLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<Event>> eventsByAttendeeLiveData = new MutableLiveData<>();

    private final MutableLiveData<Event> selectedEventLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<HashMap<String, String>>> announcementListLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);

    private final EventController eventController;

    public EventViewModel() {
        this.eventController = EventController.getInstance();
    }


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

    /**
     * Fetches all events from the database.
     */
    public void fetchAllEvents() {
        isLoadingLiveData.setValue(true);
        eventController.getAllEvents()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Event> events = new ArrayList<>();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Event event = doc.toObject(Event.class);
                                if (event != null) {
                                    event.setEventId(doc.getId());
                                    events.add(event);
                                }
                            }
                            allEventsLiveData.setValue(events);
                        } else {
                            allEventsLiveData.setValue(new ArrayList<>());
                            errorMessageLiveData.setValue("No events found.");
                        }
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch all events: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Fetches events created by a specific organizer.
     *
     * @param organizerId
     */
    public void fetchEventsByOrganizer(String organizerId) {
        if (organizerId == null || organizerId.isEmpty()) {
            errorMessageLiveData.setValue("Organizer ID is invalid.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.getAllEventsByOrganizer(organizerId)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Event> events = new ArrayList<>();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Event event = doc.toObject(Event.class);
                                if (event != null) {
                                    event.setEventId(doc.getId());
                                    events.add(event);
                                }
                            }
                            eventsByOrganizerLiveData.setValue(events);
                        } else {
                            eventsByOrganizerLiveData.setValue(new ArrayList<>());
                            errorMessageLiveData.setValue("No events found for the organizer.");
                        }
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch events by organizer: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Fetches events attended by a specific attendee.
     *
     * @param userId
     */
    public void fetchEventsByUserId(String userId) {


        isLoadingLiveData.setValue(true);
        eventController.getEnrolledEventsByUserId(userId)
                .addOnSuccessListener(new OnSuccessListener<List<DocumentSnapshot>>() {
                    @Override
                    public void onSuccess(List<DocumentSnapshot> documentSnapshots) {
                        List<Event> events = new ArrayList<>();
                        if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                            for (DocumentSnapshot doc : documentSnapshots) {
                                Event event = doc.toObject(Event.class);
                                if (event != null) {
                                    event.setEventId(doc.getId());
                                    events.add(event);
                                }
                            }
                            eventsByAttendeeLiveData.setValue(events);
                        } else {
                            eventsByAttendeeLiveData.setValue(new ArrayList<>());
                            errorMessageLiveData.setValue("No events found for the attendee.");
                        }
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch events by attendee: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Fetches details of a specific event by its ID.
     *
     * @param eventId
     */
    public void fetchEventById(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Event ID is invalid.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.getEventById(eventId)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch event: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Creates a new event.
     *
     * @param newEvent
     */
    public void createEvent(Event newEvent) {
        if (newEvent == null) {
            errorMessageLiveData.setValue("Invalid event data.");
            return;
        }

        isLoadingLiveData.setValue(true);
        // Generate a unique event ID
        String eventId = eventController.generateUniqueEventId();
        newEvent.setEventId(eventId);

        eventController.addEvent(newEvent)
                .addOnSuccessListener(aVoid -> {
                    // Fetch all events again to update the LiveData
                    fetchAllEvents();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to create event: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Updates an existing event.
     *
     * @param updatedEvent
     */
    public void updateEvent(Event updatedEvent) {
        if (updatedEvent == null || updatedEvent.getEventId() == null || updatedEvent.getEventId().isEmpty()) {
            errorMessageLiveData.setValue("Invalid event data for update.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.updateEvent(updatedEvent)
                .addOnSuccessListener(aVoid -> {
                    // Fetch the updated event details
                    fetchEventById(updatedEvent.getEventId());
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to update event: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId
     */
    public void deleteEvent(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID for deletion.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.deleteEvent(eventId)
                .addOnSuccessListener(aVoid -> {
                    fetchAllEvents();
                })
                .addOnFailureListener(new OnFailureListener() {
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
     * @param eventId
     */
    public void reactivateEvent(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID for reactivation.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.reactivateEvent(eventId)
                .addOnSuccessListener(aVoid -> {
                    // Fetch the updated event details
                    fetchEventById(eventId);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to reactivate event: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Ends an event, preventing further registrations.
     *
     * @param eventId
     */
    public void endEvent(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID for ending.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.endEvent(eventId)
                .addOnSuccessListener(aVoid -> {
                    // Fetch the updated event details
                    fetchEventById(eventId);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to end event: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Sets the banner image for an event.
     *
     * @param eventId
     * @param imageUrl
     */
    public void setEventBannerImage(String eventId, String imageUrl) {
        if (eventId == null || eventId.isEmpty() || imageUrl == null || imageUrl.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID or image URL.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.setEventBannerImage(eventId, imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Fetch the updated event details
                    fetchEventById(eventId);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to set event banner image: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Deletes the banner image for an event.
     *
     * @param userId
     */
    public void deleteEventsByUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid user ID for deleting events.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.deleteEventsByUser(userId)
                .addOnSuccessListener(aVoid -> {

                    fetchAllEvents();
                })
                .addOnFailureListener(new OnFailureListener() {
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
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID for registration.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.registerToEvent(eventId)
                .addOnSuccessListener(aVoid -> {
                    // Fetch the event details again to update the LiveData
                    fetchEventById(eventId);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to register to event: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Unregisters the current user from an event.
     * @param eventId
     */
    public void unregisterFromEvent(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID for unregistration.");
            return;
        }

        String userId = CurrentUserHandler.getSingleton().getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid user ID for unregistration.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.unregisterFromEvent(eventId, userId)
                .addOnSuccessListener(aVoid -> {

                    fetchEventById(eventId);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to unregister from event: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Fetches announcements for a specific event.
     *
     * @param eventId
     */
    public void fetchAnnouncements(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID for fetching announcements.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.getAnnouncements(eventId)
                .addOnSuccessListener(new OnSuccessListener<List<HashMap<String, String>>>() {
                    @Override
                    public void onSuccess(List<HashMap<String, String>> announcements) {
                        if (announcements != null && !announcements.isEmpty()) {
                            announcementListLiveData.setValue(announcements);
                        } else {
                            announcementListLiveData.setValue(new ArrayList<>());
                            errorMessageLiveData.setValue("No announcements found for the event.");
                        }
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch announcements: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Draws a lottery for an event to select a specified number of attendees.
     *
     * @param eventId
     * @param numberToSelect
     */
    public void drawLottery(String eventId, int numberToSelect) {
        if (eventId == null || eventId.isEmpty() || numberToSelect <= 0) {
            errorMessageLiveData.setValue("Invalid event ID or number to select.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.drawLottery(eventId, numberToSelect, true, true);
    }

    /**
     * Handles the replacement of an attendee in the event.
     *
     * @param eventId
     */
    public void handleReplacement(String userId, String eventId, String eventName) {
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID for handling replacement.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.handleReplacement(userId, eventId, eventName)
                .addOnSuccessListener(aVoid -> {
                    fetchEventById(eventId);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to handle replacement attendee: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Fetches announcements created by the current user for a specific event.
     *
     * @param eventId
     */
    public void fetchUserAnnouncements(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID for fetching user announcements.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.getUserAnnouncements(eventId)
                .addOnSuccessListener(new OnSuccessListener<List<HashMap<String, String>>>() {
                    @Override
                    public void onSuccess(List<HashMap<String, String>> announcements) {
                        if (announcements != null && !announcements.isEmpty()) {
                            announcementListLiveData.setValue(announcements);
                        } else {
                            announcementListLiveData.setValue(new ArrayList<>());
                            errorMessageLiveData.setValue("No user announcements found for the event.");
                        }
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch user announcements: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Checks in the current user to an event.
     *
     * @param eventId
     */
    public void checkInUser(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            errorMessageLiveData.setValue("Invalid event ID for check-in.");
            return;
        }

        isLoadingLiveData.setValue(true);
        eventController.checkInUser(eventId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fetchEventById(eventId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to check-in to event: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Clears the current error message.
     */
    public void clearErrorMessage() {
        errorMessageLiveData.setValue(null);
    }
}
