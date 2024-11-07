package com.example.queueup.controllers;

import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.Event;
import com.example.queueup.models.GeoLocation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for handling events in the Event Lottery System.
 * Manages event creation, registration, announcements, and other event-related operations.
 */
public class EventController {
    private static EventController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventCollectionReference = db.collection("events");
    private final CollectionReference attendanceCollectionReference = db.collection("attendees");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();
    private final AttendeeController attendeeController = AttendeeController.getInstance();

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private EventController() {}

    /**
     * Retrieves the single instance of EventController.
     *
     * @return the singleton instance of EventController
     */
    public static synchronized EventController getInstance() {
        if (singleInstance == null) {
            singleInstance = new EventController();
        }
        return singleInstance;
    }

    /**
     * Get all events
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getAllEvents() {
        return eventCollectionReference.get();
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId The ID of the event
     * @return Task<DocumentSnapshot> containing the event details
     */
    public Task<DocumentSnapshot> getEventById(String eventId) {
        return eventCollectionReference.document(eventId).get();
    }

    /**
     * Retrieves all events created by a specific organizer.
     *
     * @param organizerId The ID of the organizer
     * @return Task<QuerySnapshot> containing the list of events
     */
    public Task<QuerySnapshot> getAllEventsByOrganizer(String organizerId) {
        return eventCollectionReference
                .whereEqualTo("organizerId", organizerId).get();
    }
    /**
     * Retrieves all events that a specific attendee is registered for.
     *
     * @param attendeeId The ID of the attendee
     * @return Task<List<DocumentSnapshot>> containing the list of events
     */
    public Task<List<DocumentSnapshot>> getEventsByAttendeeId(String attendeeId) {
        return attendanceCollectionReference
                .whereEqualTo("userId", attendeeId)
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> attendanceRecords = task.getResult().getDocuments();
                        List<Task<DocumentSnapshot>> eventTasks = new ArrayList<>();
                        for (DocumentSnapshot attendance : attendanceRecords) {
                            String eventId = attendance.getString("eventId");
                            if (eventId != null) {
                                eventTasks.add(getEventById(eventId));
                            }
                        }
                        return Tasks.whenAllSuccess(eventTasks);
                    } else {
                        throw task.getException();
                    }
                }).continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<?> results = task.getResult();
                        List<DocumentSnapshot> eventSnapshots = new ArrayList<>();
                        for (Object obj : results) {
                            if (obj instanceof DocumentSnapshot) {
                                eventSnapshots.add((DocumentSnapshot) obj);
                            }
                        }
                        return eventSnapshots;
                    } else {
                        throw new RuntimeException("Failed to retrieve events for attendee.");
                    }
                });
    }

    /**
     * Creates a new event.
     *
     * @param newEvent The event object to be added
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> addEvent(Event newEvent) {
        return eventCollectionReference.document(newEvent.getEventId()).set(newEvent)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Event added successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Failed to add event.", e);
                });
    }

    /**
     * Updates an existing event.
     *
     * @param event The event object with updated information
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> updateEvent(Event event) {
        return eventCollectionReference.document(event.getEventId()).set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Event updated successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Failed to update event.", e);
                });
    }

    /**
     * Deletes an event and unregisters all attendees.
     *
     * @param eventId The ID of the event to delete
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> deleteEvent(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        return eventRef.get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                List<String> attendeeIds = (List<String>) task.getResult().get("attendeeIds");
                if (attendeeIds != null && !attendeeIds.isEmpty()) {
                    List<Task<Void>> unregisterTasks = new ArrayList<>();
                    for (String userId : attendeeIds) {
                        unregisterTasks.add(attendeeController.leaveWaitingList(eventId));
                    }
                    return Tasks.whenAll(unregisterTasks);
                }
            }
            return Tasks.forResult(null);
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                return eventRef.delete();
            } else {
                throw new RuntimeException("Failed to unregister attendees before deleting the event.");
            }
        }).addOnSuccessListener(aVoid -> {
            Log.d("EventController", "Event deleted successfully.");
        }).addOnFailureListener(e -> {
            Log.e("EventController", "Failed to delete event.", e);
        });
    }

    /**
     * Registers the current user to an event (joins the waiting list).
     *
     * @param eventId The ID of the event
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> registerToEvent(String eventId) {
        String userId = currentUserHandler.getCurrentUserId();
        return registerToEvent(userId, eventId, null);
    }

    /**
     * Registers a user to an event (joins the waiting list) with optional geolocation.
     *
     * @param userId   The ID of the user
     * @param eventId  The ID of the event
     * @param location The location of the user (optional)
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> registerToEvent(String userId, String eventId, @Nullable Location location) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);

        return db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            String eventName = eventSnapshot.getString("eventName");
            Boolean isActive = eventSnapshot.getBoolean("isActive");
            Date endDate = eventSnapshot.getDate("eventEndDate");
            Date currentDate = new Date();
//            if (isActive == null || !isActive || (endDate != null && endDate.before(currentDate))) {
//                throw new RuntimeException("The event \"" + eventName + "\" is no longer active or has already ended.");
//            }

            Long maxCap = eventSnapshot.getLong("maxCapacity");
            Long currCap = eventSnapshot.getLong("currentCapacity");
            List<String> attendees = (List<String>) eventSnapshot.get("attendeeIds");

            if ((maxCap != null && maxCap > 0) && (currCap != null && currCap >= maxCap)) {
                throw new RuntimeException("The event \"" + eventName + "\" is at full capacity.");
            }

            if (attendees != null && attendees.contains(userId)) {
                throw new RuntimeException("You have already joined the waiting list for this event.");
            }

            // Update event's current capacity and attendee list
            transaction.update(eventRef, "currentCapacity", FieldValue.increment(1));
            transaction.update(eventRef, "attendeeIds", FieldValue.arrayUnion(userId));

            return null;
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                return attendeeController.joinWaitingList(userId, eventId, location);
            } else {
                Exception e = task.getException();
                String errorMessage = e != null ? e.getMessage() : "Unknown error";
                throw new RuntimeException("(Failed to register to event) " + errorMessage);
            }
        }).addOnSuccessListener(aVoid -> {
            Log.d("EventController", "Successfully registered to event.");
        }).addOnFailureListener(e -> {
            Log.e("EventController", "Failed to register to event.", e);
        });
    }

    /**
     * Unregisters a user from an event (leaves the waiting list).
     *
     * @param eventId The ID of the event
     * @param userId  The ID of the user
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> unregisterFromEvent(String eventId, String userId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);

        return db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            String eventName = eventSnapshot.getString("eventName");
            List<String> attendees = (List<String>) eventSnapshot.get("attendeeIds");

            if (attendees != null && attendees.contains(userId)) {
                transaction.update(eventRef, "attendeeIds", FieldValue.arrayRemove(userId));
                transaction.update(eventRef, "currentCapacity", FieldValue.increment(-1));
            } else {
                throw new RuntimeException("You are not registered for this event.");
            }

            return null;
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                return attendeeController.leaveWaitingList(eventId);
            } else {
                Exception e = task.getException();
                String errorMessage = e != null ? e.getMessage() : "Unknown error";
                throw new RuntimeException("(Failed to unregister from event) " + errorMessage);
            }
        }).addOnSuccessListener(aVoid -> {
            Log.d("EventController", "Successfully unregistered from event.");
        }).addOnFailureListener(e -> {
            Log.e("EventController", "Failed to unregister from event.", e);
        });
    }

    /**
     * Draws the lottery to select a specified number of attendees from the waiting list.
     *
     * @param eventId        The ID of the event
     * @param numberToSelect The number of attendees to select
     * @return Task<List<String>> containing the selected attendee IDs
     */
    public Task<List<String>> drawLottery(String eventId, int numberToSelect) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);

        return eventRef.get().continueWithTask(task -> {
            if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                throw new RuntimeException("Failed to retrieve event details.");
            }

            DocumentSnapshot eventSnapshot = task.getResult();
            List<String> attendeeIds = (List<String>) eventSnapshot.get("attendeeIds");

            if (attendeeIds == null || attendeeIds.isEmpty()) {
                throw new RuntimeException("No attendees to select from.");
            }

            // Shuffle the list to randomize selection
            List<String> shuffledAttendees = new ArrayList<>(attendeeIds);
            java.util.Collections.shuffle(shuffledAttendees);

            // Select the required number of attendees
            List<String> selectedAttendees = shuffledAttendees.subList(0, Math.min(numberToSelect, shuffledAttendees.size()));
            return Tasks.forResult(selectedAttendees);
        });
    }

    /**
     * Sends notifications to selected attendees to confirm their participation.
     * If an attendee declines, selects a replacement.
     *
     * @param eventId          The ID of the event
     * @param selectedAttendees The list of selected attendee IDs
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> notifySelectedAttendees(String eventId, List<String> selectedAttendees) {

        // TODO: Implement notification logic and handle responses.

        Log.d("EventController", "notifySelectedAttendees called with eventId: " + eventId +
                " and selectedAttendees: " + selectedAttendees.toString());

        return Tasks.forResult(null);
    }

    /**
     * Adds an announcement to an event.
     *
     * @param eventId      The ID of the event
     * @param announcement The announcement to add as a HashMap
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> addAnnouncement(String eventId, HashMap<String, String> announcement) {
        return eventCollectionReference.document(eventId)
                .update("announcementList", FieldValue.arrayUnion(announcement))
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Announcement added successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Failed to add announcement.", e);
                });
    }

    /**
     * Retrieves all announcements for a specific event.
     *
     * @param eventId The ID of the event
     * @return Task<List<HashMap<String, String>>> containing the list of announcements
     */
    public Task<List<HashMap<String, String>>> getAnnouncements(String eventId) {
        return eventCollectionReference.document(eventId).get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                List<HashMap<String, String>> announcements = (List<HashMap<String, String>>) task.getResult().get("announcementList");
                return announcements != null ? announcements : new ArrayList<>();
            } else {
                throw new RuntimeException("Failed to retrieve announcements.");
            }
        });
    }

    /**
     * Checks in a user to an event by updating their attendance status.
     *
     * @param eventId The ID of the event
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> checkInUser(String eventId) {
        String userId = currentUserHandler.getCurrentUserId();
        return checkInUser(userId, eventId, null);
    }

    /**
     * Checks in a user to an event with optional geolocation.
     *
     * @param userId   The ID of the user
     * @param eventId  The ID of the event
     * @param location The location of the user during check-in (optional)
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> checkInUser(String userId, String eventId, @Nullable Location location) {
        String attendanceId = Attendee.generateId(userId, eventId);
        return attendeeController.checkInAttendee(attendanceId, location)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Successfully checked-in user.");
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Failed to check-in user.", e);
                });
    }

    /**
     * Reactivates an event that was previously deactivated.
     *
     * @param eventId The ID of the event
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> reactivateEvent(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("isActive", true);
        return eventRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Event reactivated successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Failed to reactivate event.", e);
                });
    }

    /**
     * Ends an event by setting its active status to false.
     *
     * @param eventId The ID of the event
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> endEvent(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("isActive", false);
        return eventRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Event ended successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Failed to end event.", e);
                });
    }

    /**
     * Removes the banner image URL from an event.
     *
     * @param eventId The ID of the event
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> removeEventImage(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        return eventRef.update("eventBannerImageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Event image successfully removed.");
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Failed to remove event image.", e);
                });
    }

    /**
     * Deletes all events created by a specific user.
     *
     * @param userId The ID of the user
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> deleteEventsByUser(String userId) {
        return getAllEventsByOrganizer(userId).continueWithTask(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> events = task.getResult().getDocuments();
                List<Task<Void>> deleteTasks = new ArrayList<>();
                for (DocumentSnapshot eventSnapshot : events) {
                    String eventId = eventSnapshot.getId();
                    deleteTasks.add(deleteEvent(eventId));
                }
                return Tasks.whenAll(deleteTasks);
            } else {
                throw new RuntimeException("Failed to retrieve events for deletion.");
            }
        });
    }


    /**
     * Retrieves user announcements for a specific event.
     *
     * @param eventId The ID of the event
     * @return Task<List<HashMap<String, String>>> containing the list of announcements
     */
    public Task<List<HashMap<String, String>>> getUserAnnouncements(String eventId) {
        return getAnnouncements(eventId).continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult();
            } else {
                throw new RuntimeException("Failed to retrieve announcements.");
            }
        });
    }

    /**
     * Handles the replacement of an attendee if they decline the invitation.
     *
     * @param eventId The ID of the event
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> handleReplacement(String eventId) {
        // Draw a new attendee from the waiting list
        // Placeholder for actual replacement logic
        return drawLottery(eventId, 1).continueWithTask(task -> {
            if (task.isSuccessful()) {
                List<String> newAttendees = task.getResult();
                if (newAttendees != null && !newAttendees.isEmpty()) {
                    String newAttendeeId = newAttendees.get(0);
                    // Notify the new attendee
                    notifySelectedAttendees(eventId, newAttendees);
                }
                return Tasks.forResult(null);
            } else {
                throw new RuntimeException("Failed to draw replacement attendee.");
            }
        });
    }



    /**
     * Adds or updates the event banner image URL.
     *
     * @param eventId  The ID of the event
     * @param imageUrl The URL of the banner image
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> setEventBannerImage(String eventId, String imageUrl) {
        return eventCollectionReference.document(eventId)
                .update("eventBannerImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Event banner image set successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Failed to set event banner image.", e);
                });
    }

    /**
     * Generates a unique event ID.
     * This method generates a unique identifier for each event using Firestore's auto-generated IDs.
     *
     * @return A unique event ID as a String
     */
    public String generateUniqueEventId() {
        return eventCollectionReference.document().getId();
    }
}