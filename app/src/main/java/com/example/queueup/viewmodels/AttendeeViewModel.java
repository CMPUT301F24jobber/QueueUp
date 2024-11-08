package com.example.queueup.viewmodels;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for managing attendee-related data and operations.
 * Interacts with AttendeeController to perform CRUD operations, fetch user information,
 * and exposes LiveData to the UI.
 */
public class AttendeeViewModel extends ViewModel {

    // LiveData for all attendance records of the current user
    private final MutableLiveData<List<Attendee>> allAttendancesLiveData = new MutableLiveData<>();

    // LiveData for attendees by event ID
    private final MutableLiveData<List<Attendee>> attendancesByEventLiveData = new MutableLiveData<>();

    // LiveData for a selected attendee's details
    private final MutableLiveData<Attendee> selectedAttendeeLiveData = new MutableLiveData<>();

    // LiveData for error messages
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    // LiveData for loading states
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);

    // LiveData for combined attendee and user information
    private final MutableLiveData<List<AttendeeWithUser>> attendeesWithUserLiveData = new MutableLiveData<>();

    // LiveData to indicate if a specific user is on a waiting list
    private final MutableLiveData<Boolean> isOnWaitingListLiveData = new MutableLiveData<>(false);

    // Instance of AttendeeController
    private final AttendeeController attendeeController;

    /**
     * Inner class to hold combined attendee and user data
     */
    public static class AttendeeWithUser {
        private final Attendee attendee;
        private final User user;

        public AttendeeWithUser(Attendee attendee, User user) {
            this.attendee = attendee;
            this.user = user;
        }

        public Attendee getAttendee() {
            return attendee;
        }

        public User getUser() {
            return user;
        }
    }

    /**
     * Constructor initializes the AttendeeController instance.
     */
    public AttendeeViewModel() {
        this.attendeeController = AttendeeController.getInstance();
    }

    // Getters for LiveData

    /**
     * Returns LiveData containing all attendance records for the current user.
     */
    public LiveData<List<Attendee>> getAllAttendancesLiveData() {
        return allAttendancesLiveData;
    }

    /**
     * Returns LiveData containing attendees filtered by event ID.
     */
    public LiveData<List<Attendee>> getAttendancesByEventLiveData() {
        return attendancesByEventLiveData;
    }

    /**
     * Returns LiveData containing details of a selected attendee.
     */
    public LiveData<Attendee> getSelectedAttendeeLiveData() {
        return selectedAttendeeLiveData;
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
     * Returns LiveData containing combined attendee and user information.
     */
    public LiveData<List<AttendeeWithUser>> getAttendeesWithUserLiveData() {
        return attendeesWithUserLiveData;
    }

    /**
     * Returns LiveData indicating if a specific user is on a waiting list.
     */
    public LiveData<Boolean> getIsOnWaitingListLiveData() {
        return isOnWaitingListLiveData;
    }

    // LiveData setters are private to prevent external modification

    /**
     * Fetches all attendance records for the current user.
     * Updates allAttendancesLiveData upon success or errorMessageLiveData upon failure.
     */
    public void fetchAllAttendances() {
        isLoadingLiveData.setValue(true);
        attendeeController.getAllAttendance()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Attendee> attendances = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Attendee attendee = doc.toObject(Attendee.class);
                            if (attendee != null) {
                                attendee.setId(doc.getId()); // Ensure attendee ID is set
                                attendances.add(attendee);
                            }
                        }
                        allAttendancesLiveData.setValue(attendances);
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch attendances: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Fetches attendance records for a specific event.
     * Updates attendancesByEventLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event.
     */
    public void fetchAttendancesByEvent(String eventId) {
        isLoadingLiveData.setValue(true);
        attendeeController.getAttendanceByEventId(eventId)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Attendee> attendances = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Attendee attendee = doc.toObject(Attendee.class);
                            if (attendee != null) {
                                attendee.setId(doc.getId());
                                attendances.add(attendee);
                            }
                        }
                        attendancesByEventLiveData.setValue(attendances);
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch attendances for event: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Fetches details of a specific attendee by their ID.
     * Updates selectedAttendeeLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param attendeeId The ID of the attendee.
     */
    public void fetchAttendeeById(String attendeeId) {
        isLoadingLiveData.setValue(true);
        attendeeController.getAttendanceById(attendeeId)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Attendee attendee = documentSnapshot.toObject(Attendee.class);
                            if (attendee != null) {
                                attendee.setId(documentSnapshot.getId());
                                selectedAttendeeLiveData.setValue(attendee);
                            } else {
                                errorMessageLiveData.setValue("Failed to parse attendee data.");
                            }
                        } else {
                            errorMessageLiveData.setValue("Attendee not found.");
                        }
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch attendee: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Joins the waiting list for a specific event.
     * Updates allAttendancesLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event to join.
     */
    public void joinWaitingList(String eventId) {
        isLoadingLiveData.setValue(true);
        attendeeController.joinWaitingList(eventId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Optionally, fetch all attendances again
                        fetchAllAttendances();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to join waiting list: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Joins the waiting list for a specific event with optional geolocation.
     * Updates allAttendancesLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param userId   The ID of the user.
     * @param eventId  The ID of the event.
     * @param location The location of the user (optional).
     */
    public void joinWaitingList(String userId, String eventId, @Nullable Location location) {
        isLoadingLiveData.setValue(true);
        attendeeController.joinWaitingList(userId, eventId, location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fetchAllAttendances();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to join waiting list with location: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Leaves the waiting list for a specific event.
     * Updates allAttendancesLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event to leave.
     */
    public void leaveWaitingList(String eventId) {
        isLoadingLiveData.setValue(true);
        attendeeController.leaveWaitingList(eventId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Optionally, fetch all attendances again
                        fetchAllAttendances();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to leave waiting list: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Leaves the waiting list for a specific user and event.
     * Useful for admin operations or when handling replacements.
     * Updates attendancesByEventLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event.
     * @param userId  The ID of the user.
     */
    public void leaveWaitingList(String eventId, String userId) {
        isLoadingLiveData.setValue(true);
        attendeeController.leaveWaitingList(userId, eventId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Optionally, fetch attendances by event again
                        fetchAttendancesByEvent(eventId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to leave waiting list for user: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Updates an existing attendance record.
     * Updates selectedAttendeeLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param attendee The attendee object with updated information.
     */
    public void updateAttendance(Attendee attendee) {
        isLoadingLiveData.setValue(true);
        attendeeController.updateAttendance(attendee)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Optionally, fetch the updated attendee details
                        fetchAttendeeById(attendee.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to update attendance: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Deletes an attendance record by its ID.
     * Updates allAttendancesLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param attendeeId The ID of the attendance record to delete.
     */
    public void deleteAttendance(String attendeeId) {
        isLoadingLiveData.setValue(true);
        attendeeController.deleteAttendance(attendeeId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Optionally, fetch all attendances again
                        fetchAllAttendances();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to delete attendance: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Checks in the current user to a specific event.
     * Updates selectedAttendeeLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId  The ID of the event to check in.
     * @param location The location during check-in (optional).
     */
    public void checkInUser(String eventId, @Nullable Location location) {
        isLoadingLiveData.setValue(true);
        String attendeeId = generateAttendeeId(eventId);
        attendeeController.checkInAttendee(attendeeId, location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Optionally, fetch the updated attendee details
                        fetchAttendeeById(attendeeId);
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to check-in: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Handles the replacement of an attendee if they decline the invitation.
     * Draws a new attendee and notifies them.
     * Updates attendancesByEventLiveData upon success or errorMessageLiveData upon failure.
     *
     * @param eventId The ID of the event.
     */
    public void handleReplacement(String eventId) {
        isLoadingLiveData.setValue(true);
        attendeeController.replaceAttendee(eventId)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot newAttendeeDoc = queryDocumentSnapshots.getDocuments().get(0);
                            Attendee newAttendee = newAttendeeDoc.toObject(Attendee.class);
                            if (newAttendee != null) {
                                // Update the numberInLine or any other necessary fields
                                // For example, set numberInLine based on current list size
                                newAttendee.setNumberInLine(calculateNumberInLine(eventId));
                                // Update the attendee record
                                attendeeController.updateAttendance(newAttendee)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                notifyAttendee(newAttendee.getId(), true);
                                                // Fetch updated attendances by event
                                                fetchAttendancesByEvent(eventId);
                                                isLoadingLiveData.setValue(false);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                errorMessageLiveData.setValue("Failed to update replacement attendee: " + e.getMessage());
                                                isLoadingLiveData.setValue(false);
                                            }
                                        });
                            } else {
                                errorMessageLiveData.setValue("Failed to parse replacement attendee data.");
                                isLoadingLiveData.setValue(false);
                            }
                        } else {
                            errorMessageLiveData.setValue("No attendees available for replacement.");
                            isLoadingLiveData.setValue(false);
                        }
                    }
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
     * Calculates the number in line for a new attendee based on current attendees.
     *
     * @param eventId The ID of the event.
     * @return The calculated number in line.
     */
    private int calculateNumberInLine(String eventId) {
        List<Attendee> currentAttendances = attendancesByEventLiveData.getValue();
        if (currentAttendances != null) {
            return currentAttendances.size() + 1;
        }
        return 1;
    }

    /**
     * Notifies an attendee about their selection status.
     *
     * @param attendeeId The ID of the attendee.
     * @param isSelected Whether the attendee was selected or not.
     */
    private void notifyAttendee(String attendeeId, boolean isSelected) {
        // TODO: Implement notification logic (e.g., Firebase Cloud Messaging)
        // This could involve calling a NotificationHandler or similar component
    }

    /**
     * Clears the current error message.
     */
    public void clearErrorMessage() {
        errorMessageLiveData.setValue(null);
    }

    /**
     * Generates the attendee ID based on the current user and event IDs.
     *
     * @param eventId The ID of the event.
     * @return The generated attendee ID.
     */
    private String generateAttendeeId(String eventId) {
        String userId = CurrentUserHandler.getSingleton().getCurrentUserId();
        return Attendee.generateId(userId, eventId);
    }

    /**
     * Fetches attendance records for a specific event, including user information.
     * Updates attendeesWithUserLiveData with combined attendee and user information.
     *
     * @param eventId The ID of the event
     */
    public void fetchAttendeesWithUserInfo(String eventId) {
        isLoadingLiveData.setValue(true);

        attendeeController.getAttendanceByEventId(eventId)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Attendee> attendees = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Attendee attendee = doc.toObject(Attendee.class);
                            if (attendee != null) {
                                attendee.setId(doc.getId());
                                attendees.add(attendee);
                            }
                        }

                        if (attendees.isEmpty()) {
                            attendeesWithUserLiveData.setValue(new ArrayList<>());
                            isLoadingLiveData.setValue(false);
                            return;
                        }

                        attendeeController.fetchUsersForAttendees(attendees)
                                .addOnSuccessListener(new OnSuccessListener<Map<String, User>>() {
                                    @Override
                                    public void onSuccess(Map<String, User> userMap) {
                                        List<AttendeeWithUser> combinedList = new ArrayList<>();
                                        for (Attendee attendee : attendees) {
                                            User user = userMap.get(attendee.getUserId());
                                            if (user != null) {
                                                combinedList.add(new AttendeeWithUser(attendee, user));
                                            }
                                        }
                                        attendeesWithUserLiveData.setValue(combinedList);
                                        isLoadingLiveData.setValue(false);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        errorMessageLiveData.setValue("Failed to fetch user information: " + e.getMessage());
                                        isLoadingLiveData.setValue(false);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to fetch attendees: " + e.getMessage());
                        isLoadingLiveData.setValue(false);
                    }
                });
    }

    /**
     * Checks if a specific user is on the waiting list for an event.
     * Updates isOnWaitingListLiveData with the result.
     *
     * @param userId  The ID of the user to check
     * @param eventId The ID of the event to check
     */
    public void checkWaitingListStatus(String userId, String eventId) {
        isLoadingLiveData.setValue(true);
        String attendeeId = Attendee.generateId(userId, eventId);

        attendeeController.getAttendanceById(attendeeId)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        isOnWaitingListLiveData.setValue(documentSnapshot.exists());
                        isLoadingLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessageLiveData.setValue("Failed to check waiting list status: " + e.getMessage());
                        isOnWaitingListLiveData.setValue(false);
                        isLoadingLiveData.setValue(false);
                    }
                });
    }
}
