package com.example.queueup.controllers;

import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.Event;
import com.example.queueup.models.GeoLocation;
import com.example.queueup.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AttendeeController {
    private static AttendeeController singleInstance = null;
    private static final String TAG = "AttendeeController";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference attendeeCollectionReference = FirebaseFirestore.getInstance().collection("attendees");
    private final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();
    private UserController userController = UserController.getInstance();



    private AttendeeController() {}

    public static synchronized AttendeeController getInstance() {
        if (singleInstance == null) {
            singleInstance = new AttendeeController();
        }
        return singleInstance;
    }

    /**
     * Retrieves all attendance records for the current user.
     *
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getAllAttendance() {
        User currentUser = currentUserHandler.getCurrentUser().getValue();
        if (currentUser == null) {
            return Tasks.forException(new Exception("Current user is not logged in."));
        }
        String currentUserId = currentUser.getUuid();
        return attendeeCollectionReference.whereEqualTo("userId", currentUserId).get();
    }

    /**
     * Retrieves attendance records by a specific event ID.
     *
     * @param eventId The ID of the event
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getAttendanceByEventId(String eventId) {
        return attendeeCollectionReference.whereEqualTo("eventId", eventId).get();
    }

    /**
     * Retrieves attendance records by a specific user ID.
     *
     * @param userId The ID of the user
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getAttendanceByUserId(String userId) {
        return attendeeCollectionReference.whereEqualTo("userId", userId).get();
    }

    /**
     * Retrieves a specific attendance record by its ID.
     *
     * @param id
     * @return Task<DocumentSnapshot>
     */
    public Task<DocumentSnapshot> getAttendanceById(String id) {
        return attendeeCollectionReference.document(id).get();
    }

    /**
     * Creates a new attendance record for the current user and a specified event.
     *
     * @param eventId
     * @return Task<Void>
     */
    public Task<Void> joinWaitingList(String eventId) {
        User currentUser = currentUserHandler.getCurrentUser().getValue();
        if (currentUser == null) {
            return Tasks.forException(new Exception("Current user is not logged in."));
        }
        String userId = currentUser.getUuid();
        Attendee newAttendee = new Attendee(userId, eventId);
        newAttendee.setStatus("waiting"); // Set initial status to "waiting"
        return attendeeCollectionReference.document(newAttendee.getId()).set(newAttendee);
    }

    /**
     * Creates a new attendance record with optional geolocation.
     *
     * @param userId
     * @param eventId
     * @param location
     * @return Task<Void>
     */
    public Task<Void> joinWaitingList(String userId, String eventId, @Nullable Location location) {
        Attendee newAttendee = new Attendee(userId, eventId);
        newAttendee.setStatus("waiting");
        if (location != null) {
            GeoLocation newLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
            userCollectionReference.document(userId).update("geoLocation", newLocation);
            newAttendee.setLocation(newLocation);
        }
        return attendeeCollectionReference.document(newAttendee.getId()).set(newAttendee);
    }

    /**
     * Leaves the waiting list for the current user and a specific event.
     *
     * @param eventId
     * @return Task<Void>
     */
    public Task<Void> leaveWaitingList(String eventId) {
        User currentUser = currentUserHandler.getCurrentUser().getValue();
        if (currentUser == null) {
            return Tasks.forException(new Exception("Current user is not logged in."));
        }
        String userId = currentUser.getUuid();
        String attendeeId = Attendee.generateId(userId, eventId);

        return attendeeCollectionReference.document(attendeeId).delete();
    }

    /**
     * Leaves the waiting list for a specific user and event.
     * Useful for admin operations or when handling replacements.
     *
     * @param eventId
     * @param userId
     * @return Task<Void>
     */
    public Task<Void> leaveWaitingList(String eventId, String userId) {
        String attendeeId = Attendee.generateId(userId, eventId);
        return attendeeCollectionReference.document(attendeeId).update("status", "cancelled");
    }

    /**
     * Updates an existing attendance record.
     * @param attendee
     * @return Task<Void>
     */
    public Task<Void> updateAttendance(Attendee attendee) {
        return attendeeCollectionReference.document(attendee.getId()).set(attendee);
    }

    /**
     * Deletes an attendance record by its ID.
     * @param id
     * @return Task<Void>
     */
    public Task<Void> deleteAttendance(String id) {
        return attendeeCollectionReference.document(id).delete();
    }

    /**
     * Handles attendee check-in
     *
     * @param attendeeId
     * @param location
     * @return Task<Void>
     */
    public Task<Void> checkInAttendee(String attendeeId, @Nullable Location location) {
        if (location != null) {
            GeoLocation checkInLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
            return attendeeCollectionReference.document(attendeeId)
                    .update("location", checkInLocation, "isCheckedIn", true)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Attendee checked in successfully."))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to check in attendee.", e));
        }
        // If location is not provided, still mark as checked in
        return attendeeCollectionReference.document(attendeeId)
                .update("isCheckedIn", true)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Attendee checked in successfully without location."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to check in attendee.", e));
    }

    /**
     * Notifies an attendee about their selection status.
     *
     * @param attendeeId
     */
    public void notifyAttendeebyId(String attendeeId) {
        attendeeCollectionReference.document(attendeeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                Attendee attendee = task.getResult().toObject(Attendee.class);
                if (attendee != null) {
                    String eventId = attendee.getEventId();
                    String userId = attendee.getUserId();
                    String status = attendee.getStatus();
                    // Update the attendee's status in Firestore
                    db.collection("events").document(eventId).get().addOnSuccessListener(document -> {
                        if (document != null) {
                            Event event = document.toObject(Event.class);
                            userController.notifyUserById(userId, status, makeNotificationMessage(status, event.getEventName()));
                        }
                    });
                }
            } else {
                Log.e(TAG, "Failed to retrieve attendee with ID: " + attendeeId);
            }
        });
    }

    public static String makeNotificationMessage(String status, String eventName) {
        switch (status) {
            case "cancelled":
                return "Your spot in " + eventName + "has been revolked.";
            case "selected":
                return "You were selected for " + eventName + "!";
            case "not selected":
                return "You were not selected for " + eventName + ".";
        }
        return "error";
    }

    /**
     * Replaces an attendee if they decline the invitation.
     *
     * @param eventId
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> replaceAttendee(String eventId) {
        // Fetch the next attendee in line based on numberInLine and status "waiting"
        return attendeeCollectionReference
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "waiting")
                .orderBy("numberInLine")
                .limit(1)
                .get();
    }



    /**
     * Fetches user information for a list of attendees.
     *
     * @param attendees
     * @return Task<Map<String, User>> mapping user IDs to User objects
     */
    public Task<ArrayList<User>> fetchUsersForAttendees(List<Attendee> attendees) {
        List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
        for (Attendee attendee : attendees) {
            Log.d("attendee", attendee.getUserId());
            Task<DocumentSnapshot> userTask = userCollectionReference.document(attendee.getUserId()).get();
            userTasks.add(userTask);
        }

        return Tasks.whenAllSuccess(userTasks).continueWith(task -> {
            ArrayList<User> userList = new ArrayList<>();
            List<Object> results = task.getResult();
            for (int i = 0; i < results.size(); i++) {
                Object obj = results.get(i);
                if (obj instanceof DocumentSnapshot) {
                    DocumentSnapshot doc = (DocumentSnapshot) obj;
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            // Map userId to User object
                            userList.add(user);
                        }
                    }
                }
            }
            return userList;
        });
    }

    public Task<ArrayList<ArrayList<User>>> fetchUserListsForAttendees(ArrayList<Attendee> attendees) {
        List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
        for (Attendee attendee : attendees) {
            Task<DocumentSnapshot> userTask = userCollectionReference.document(attendee.getUserId()).get();
            userTasks.add(userTask);
        }
        return Tasks.whenAllSuccess(userTasks).continueWith(task -> {
            ArrayList<ArrayList<User>> userMap = new ArrayList<ArrayList<User>>();

            userMap.add(new ArrayList<>());
            userMap.add(new ArrayList<>());
            userMap.add(new ArrayList<>());
            userMap.add(new ArrayList<>());


            List<Object> results = task.getResult();
            for (int i = 0; i < results.size(); i++) {
                Object obj = results.get(i);
                if (obj instanceof DocumentSnapshot) {
                    DocumentSnapshot doc = (DocumentSnapshot) obj;
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        userMap.get(3).add(user);

                        if (user != null) {
                            // Map userId to User object
                            switch (attendees.get(i).getStatus()){
                                case "selected":
                                    userMap.get(0).add(user);
                                    break;
                                case "cancelled":
                                    userMap.get(1).add(user);
                                    break;
                                case "enrolled":
                                    userMap.get(2).add(user);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
            return userMap;
        });
    }

    /**
     * Sets the status of an attendee.
     *
     * @param attendeeId
     * @param status
     * @return Task<Void> indicating the completion of the operation
     */
    public Task<Void> setAttendeeStatus(String attendeeId, String status) {
        return attendeeCollectionReference.document(attendeeId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Attendee " + attendeeId + " status set to " + status + "."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to set status for attendee " + attendeeId + ".", e));
    }
}
