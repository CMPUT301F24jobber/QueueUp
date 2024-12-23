package com.example.queueup.controllers;

import static android.content.ContentValues.TAG;



import android.location.Location;
import android.util.Log;
import java.util.Random;

import androidx.annotation.Nullable;

import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventController {
    private static EventController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventCollectionReference = db.collection("events");
    private final CollectionReference attendanceCollectionReference = db.collection("attendees");
    private final UserController userController = UserController.getInstance();
    private final AttendeeController attendeeController = AttendeeController.getInstance();
    private EventController() {}

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
     * @return Task<DocumentSnapshot>
     */
    public Task<DocumentSnapshot> getEventById(String eventId) {
        return eventCollectionReference.document(eventId).get();
    }

    /**
     * Retrieves all events created by a specific organizer.
     *
     * @param organizerId The ID of the organizer
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getAllEventsByOrganizer(String organizerId) {
        return eventCollectionReference
                .whereEqualTo("organizerId", organizerId).get();
    }
    /**
     * Retrieves all events that a specific attendee is registered for.
     *
     * @param userId The ID of the attendee
     * @return Task<List<DocumentSnapshot>>
     */
    public Task<List<DocumentSnapshot>> getEnrolledEventsByUserId(String userId) {
        return attendanceCollectionReference
                .whereEqualTo("userId", userId)
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
     * @return Task<Void>
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
     * @param event
     * @return Task<Void>
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
     * @param eventId
     * @return Task<Void>
     */
    public Task<Void> deleteEvent(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        return eventRef.get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                List<String> attendeeIds = (List<String>)task.getResult().get("attendeeIds");
                if (attendeeIds != null && !attendeeIds.isEmpty()) {
                    List<Task<Void>> unregisterTasks = new ArrayList<>();
                    for (String userId : attendeeIds) {
                        unregisterTasks.add(AttendeeController.getInstance().leaveWaitingList(eventId));
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
     * Registers the current user to an event
     *
     * @param eventId
     * @return Task<Void>
     */
    public Task<Void> registerToEvent(String eventId) {
        String userId = CurrentUserHandler.getSingleton().getCurrentUserId();
        return registerToEvent(userId, eventId, null);
    }

    /**
     * Registers a user to an event
     *
     * @param userId
     * @param eventId
     * @param location
     * @return Task<Void>
     */
    public Task<Void> registerToEvent(String userId, String eventId, @Nullable Location location) {
        attendeeController.joinWaitingList(userId,eventId, location);
        return eventCollectionReference.document(eventId).update("attendeeIds", FieldValue.arrayUnion(userId+eventId));
    }

    /**
     * Unregisters a user from an event
     *
     * @param eventId
     * @param userId
     * @return Task<Void>
     */
    public Task<Void> unregisterFromEvent(String eventId, String userId) {
        attendeeController.leaveWaitingList(eventId);
        return eventCollectionReference.document(eventId).update("attendeeIds", FieldValue.arrayRemove(userId+eventId));
    }

    /**
     * Draws the lottery to select a specified number of attendees from the waiting list.
     *
     * @param eventId
     * @param numberToSelect
     * @return Task<List<String>>
     */
    public void drawLottery(String eventId, int numberToSelect, boolean notifyWinner, boolean nottifyLoser) {
        setIsDrawn(eventId);

        getEventById(eventId).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
            Event event = task.getResult().toObject(Event.class);
        String eventName = event.getEventName();
        ArrayList<String> attendeeIds = event.getAttendeeIds();

        if (attendeeIds == null || attendeeIds.isEmpty()) {
            throw new RuntimeException("No attendees to select from.");
        }

        // Shuffle the list to randomize selection
        ArrayList<String> shuffledAttendees = new ArrayList<>(attendeeIds);
        java.util.Collections.shuffle(shuffledAttendees);

        // Select the required number of attendees
        int correctedNum = Math.min(numberToSelect, shuffledAttendees.size());

        for (int i = 0, n = shuffledAttendees.size(); i < n; ++i) {
            String attendeeId = shuffledAttendees.get(i), selection = (i < correctedNum ? "selected" : "not selected");
            Log.d("UserId", attendeeId.substring(0, 36));
            userController.notifyUserById(attendeeId.substring(0, 36), selection, AttendeeController.makeNotificationMessage(selection, eventName));
            attendeeController.setAttendeeStatus(attendeeId, selection);
        }
            }
            return null;
        });

    }
    public void cancelWinners(String eventId, String eventName, boolean notify, boolean replace) {
        attendeeController.getAttendanceByEventId(eventId)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Attendee> attendees = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Attendee attendee = doc.toObject(Attendee.class);
                            if (attendee != null) {
                                attendees.add(attendee);
                            }
                        }
                        for (Attendee attendee : attendees) {
                            if (attendee.getStatus().equals("selected")) {
                                if (notify) {
                                userController.notifyUserById(attendee.getUserId(), "cancelled",
                                        AttendeeController.makeNotificationMessage("cancelled", eventName));
                                }
                                if (replace) {
                                    handleReplacement(attendee.getUserId(), attendee.getEventId(), eventName);
                                } else {
                                    attendeeController.setAttendeeStatus(attendee.getId(), "cancelled");
                                }
                            }
                        }

                    }
                });
    }


    /**
     * Retrieves all announcements for a specific event.
     *
     * @param eventId The ID of the event
     * @return Task<List<HashMap<String, String>>>
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
     * @param eventId
     * @return Task<Void>
     */
    public Task<Void> checkInUser(String eventId) {
        String userId = CurrentUserHandler.getSingleton().getCurrentUserId();
        return checkInUser(userId, eventId, null);
    }

    /**
     * Checks in a user to an event with optional geolocation.
     *
     * @param userId
     * @param eventId
     * @param location
     * @return Task<Void>
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
     * @param eventId
     * @return Task<Void>
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
     * @param eventId
     * @return Task<Void>
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
     * @param eventId
     * @return Task<Void>
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
     * @param userId
     * @return Task<Void>
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
     * @param eventId
     * @return Task<List<HashMap<String, String>>>
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
     * @param eventId
     * @return Task<Void>
     */
    public Task<Void> handleReplacement(String userId, String eventId, String eventName) {
        Log.d(TAG, "handleReplacement called for eventId: " + eventId);
        attendeeController.setAttendeeStatus(userId+eventId, "cancelled");
        return attendanceCollectionReference
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "waiting")
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Select the next attendee in line
                            ArrayList<String> attendees = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                Attendee nextAttendee = documentSnapshot.toObject(Attendee.class);
                                if (nextAttendee != null) {
                                    attendees.add(nextAttendee.getUserId());
                                }
                            }
                            if (!attendees.isEmpty()) {
                                String attendeeId = attendees.get((new Random()).nextInt(attendees.size()));
                                userController.notifyUserById(attendeeId.substring(0, 36), "selected", AttendeeController.makeNotificationMessage("selected", eventName));
                                attendeeController.setAttendeeStatus(attendeeId+eventId, "selected");
                            }
                        }
                        return Tasks.forResult(null);
                    } else {
                        Log.e(TAG, "Failed to fetch pending attendees for replacement.", task.getException());
                        throw task.getException();
                    }
                });
    }

    /**
     * Retrieves the name of the event by its ID.
     *
     * @param eventId
     * @return Task<String>
     */
    public Task<DocumentSnapshot> getDocumentSnapshot(String eventId) {
        return eventCollectionReference.document(eventId).get();
    }

    /**
     * Retrieves the user IDs of attendees on the waiting list for a specific event.
     *
     * @param eventId
     * @return Task<List<String>>
     */
    public Task<List<String>> getWaitingListUserIds(String eventId) {
        return attendanceCollectionReference
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "waiting")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<String> userIds = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            userIds.add(doc.getString("userId"));
                        }
                        return userIds;
                    } else {
                        throw new RuntimeException("Failed to retrieve waiting list users.");
                    }
                });
    }

    /**
     * Retrieves the user IDs of selected attendees for a specific event.
     *
     * @param eventId
     * @return Task<List<String>>
     */
    public Task<List<String>> getSelectedUserIds(String eventId) {
        return attendanceCollectionReference
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "selected")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<String> userIds = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            userIds.add(doc.getString("userId"));
                        }
                        return userIds;
                    } else {
                        throw new RuntimeException("Failed to retrieve selected attendees.");
                    }
                });
    }

    /**
     * Retrieves the user IDs of checked-in attendees for a specific event.
     *
     * @param eventId
     * @return Task<List<String>>
     */
    public Task<Boolean> isCapacityReached(String eventId) {
        return eventCollectionReference.document(eventId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                Event event = task.getResult().toObject(Event.class);
                if (event != null) {
                    int maxCap = event.getMaxCapacity();
                    List<String> attendeeIds = event.getAttendeeIds();
                    return attendeeIds != null && attendeeIds.size() >= maxCap;
                } else {
                    throw new RuntimeException("Failed to retrieve event.");
                }
            } else {
                throw new RuntimeException("Failed to retrieve event.");
            }
        });
    }


    /**
     * Adds or updates the event banner image URL.
     *
     * @param eventId
     * @param imageUrl
     * @return Task<Void>
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
    public Task<Void> setIsDrawn(String eventId) {
        return eventCollectionReference.document(eventId)
                .update("isDrawn",true);
    }
    public Task<Void> setRedrawEnabled(String eventId, boolean notify) {
        return eventCollectionReference.document(eventId)
                .update("redrawEnabled",notify);
    }
    public Task<Void> setSendingNotificationOnRedraw(String eventId, boolean notify) {
        return eventCollectionReference.document(eventId)
                .update("redrawNotificationEnabled",notify);
    }
    public Task<Void> updateEventField(String eventId, String field, Object obj) {
        return eventCollectionReference.document(eventId)
                .update("redrawNotificationEnabled", obj);
    }

    /**
     * Generates a unique event ID.
     * @return id
     */
    public String generateUniqueEventId() {
        return eventCollectionReference.document().getId();
    }
}