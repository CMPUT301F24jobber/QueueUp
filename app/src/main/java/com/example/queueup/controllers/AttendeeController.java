package com.example.queueup.controllers;

import android.location.Location;

import androidx.annotation.Nullable;

import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.GeoLocation;
import com.example.queueup.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for managing Attendees in the Event Lottery System.
 * Handles CRUD operations, waiting list management, and fetching user information.
 */
public class AttendeeController {
    private static AttendeeController singleInstance = null;

    private final CollectionReference attendeeCollectionReference = FirebaseFirestore.getInstance().collection("attendees");
    private final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private AttendeeController() {}

    /**
     * Retrieves the single instance of AttendeeController.
     *
     * @return the singleton instance of AttendeeController
     */
    public static synchronized AttendeeController getInstance() {
        if (singleInstance == null) {
            singleInstance = new AttendeeController();
        }
        return singleInstance;
    }

    /**
     * Retrieves all attendance records for the current user.
     *
     * @return Task<QuerySnapshot> containing all attendance records for the current user
     */
    public Task<QuerySnapshot> getAllAttendance() {
        String currentUserId = currentUserHandler.getCurrentUser().getValue().getUuid();
        return attendeeCollectionReference.whereEqualTo("userId", currentUserId).get();
    }

    /**
     * Retrieves attendance records by a specific event ID.
     *
     * @param eventId The ID of the event
     * @return Task<QuerySnapshot> containing all attendees for the specified event
     */
    public Task<QuerySnapshot> getAttendanceByEventId(String eventId) {
        return attendeeCollectionReference.whereEqualTo("eventId", eventId)
                .orderBy("numberInLine")
                .get();
    }

    /**
     * Retrieves attendance records by a specific user ID.
     *
     * @param userId The ID of the user
     * @return Task<QuerySnapshot> containing all attendance records for the specified user
     */
    public Task<QuerySnapshot> getAttendanceByUserId(String userId) {
        return attendeeCollectionReference.whereEqualTo("userId", userId).get();
    }

    /**
     * Retrieves a specific attendance record by its ID.
     *
     * @param id The ID of the attendance record
     * @return Task<DocumentSnapshot> containing the attendance record
     */
    public Task<DocumentSnapshot> getAttendanceById(String id) {
        return attendeeCollectionReference.document(id).get();
    }

    /**
     * Creates a new attendance record for the current user and a specified event.
     * (Join Waiting List)
     *
     * @param eventId The ID of the event
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> joinWaitingList(String eventId) {
        String userId = currentUserHandler.getCurrentUser().getValue().getUuid();
        Attendee newAttendee = new Attendee(userId, eventId);
        return attendeeCollectionReference.document(newAttendee.getId()).set(newAttendee);
    }

    /**
     * Creates a new attendance record with optional geolocation.
     *
     * @param userId   The ID of the user
     * @param eventId  The ID of the event
     * @param location The location of the user (optional)
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> joinWaitingList(String userId, String eventId, @Nullable Location location) {
        Attendee newAttendee = new Attendee(userId, eventId);
        if (location != null) {
            GeoLocation newLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
            newAttendee.setLocation(newLocation);
        }
        return attendeeCollectionReference.document(newAttendee.getId()).set(newAttendee);
    }

    /**
     * Leaves the waiting list for a specific event.
     *
     * @param eventId The ID of the event
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> leaveWaitingList(String eventId) {
        String userId = currentUserHandler.getCurrentUser().getValue().getUuid();
        String attendeeId = Attendee.generateId(userId, eventId);
        return attendeeCollectionReference.document(attendeeId).delete();
    }

    /**
     * Leaves the waiting list for a specific user and event.
     * Useful for admin operations or when handling replacements.
     *
     * @param eventId The ID of the event.
     * @param userId  The ID of the user.
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> leaveWaitingList(String eventId, String userId) {
        String attendeeId = Attendee.generateId(userId, eventId);
        return attendeeCollectionReference.document(attendeeId).delete();
    }

    /**
     * Updates an existing attendance record.
     * (Used for updating status, e.g., accepting or declining an invitation)
     *
     * @param attendee The attendee object with updated information
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> updateAttendance(Attendee attendee) {
        return attendeeCollectionReference.document(attendee.getId()).set(attendee);
    }

    /**
     * Deletes an attendance record by its ID.
     *
     * @param id The ID of the attendance record to delete
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> deleteAttendance(String id) {
        return attendeeCollectionReference.document(id).delete();
    }

    /**
     * Handles attendee check-in by updating their geolocation and check-in status.
     *
     * @param attendeeId The ID of the attendee
     * @param location   The location during check-in (optional)
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> checkInAttendee(String attendeeId, @Nullable Location location) {
        if (location != null) {
            GeoLocation checkInLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
            return attendeeCollectionReference.document(attendeeId)
                    .update("geoLocation", checkInLocation, "isCheckedIn", true);
        }
        // If location is not provided, still mark as checked in
        return attendeeCollectionReference.document(attendeeId)
                .update("isCheckedIn", true);
    }

    /**
     * Notifies an attendee about their selection status.
     * (Implementation would require integration with a notification system)
     *
     * @param attendeeId The ID of the attendee
     * @param isSelected Whether the attendee was selected or not
     */
    public void notifyAttendee(String attendeeId, boolean isSelected) {
        // TODO: Implement notification logic (e.g., Firebase Cloud Messaging)
    }

    /**
     * Replaces an attendee if they decline the invitation.
     *
     * @param eventId The ID of the event
     * @return Task<QuerySnapshot> containing the new attendee selected
     */
    public Task<QuerySnapshot> replaceAttendee(String eventId) {
        // Fetch the next attendee in line based on numberInLine
        return attendeeCollectionReference.whereEqualTo("eventId", eventId)
                .orderBy("numberInLine")
                .limit(1)
                .get();
    }

    /**
     * Fetches user information for a list of attendees.
     *
     * @param attendees List of attendees to fetch user information for
     * @return Task<Map<String, User>> mapping user IDs to User objects
     */
    public Task<Map<String, User>> fetchUsersForAttendees(List<Attendee> attendees) {
        List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
        for (Attendee attendee : attendees) {
            Task<DocumentSnapshot> userTask = userCollectionReference.document(attendee.getUserId()).get();
            userTasks.add(userTask);
        }

        return Tasks.whenAllSuccess(userTasks).continueWith(task -> {
            Map<String, User> userMap = new HashMap<>();
            List<Object> results = task.getResult();
            for (int i = 0; i < results.size(); i++) {
                Object obj = results.get(i);
                if (obj instanceof DocumentSnapshot) {
                    DocumentSnapshot doc = (DocumentSnapshot) obj;
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            // Map userId to User object
                            userMap.put(attendees.get(i).getUserId(), user);
                        }
                    }
                }
            }
            return userMap;
        });
    }
}
