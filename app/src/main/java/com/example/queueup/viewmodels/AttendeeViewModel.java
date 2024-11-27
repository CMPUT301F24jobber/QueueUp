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


public class AttendeeViewModel extends ViewModel {

    private final MutableLiveData<List<Attendee>> allAttendancesLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<Attendee>> attendancesByEventLiveData = new MutableLiveData<>();

    private final MutableLiveData<Attendee> selectedAttendeeLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);

    private final MutableLiveData<List<AttendeeWithUser>> attendeesWithUserLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AttendeeWithUser>> attendeesWithUserInvitedLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AttendeeWithUser>> attendeesWithUserCancelLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AttendeeWithUser>> attendeesWithUserEnrolledLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isOnWaitingListLiveData = new MutableLiveData<>(false);
    private final AttendeeController attendeeController;


    public static class AttendeeWithUser {
        private final Attendee attendee;
        private final User user;

        /**
         * Constructor for the AttendeeWithUser
         * @param attendee
         * @param user
         */
        public AttendeeWithUser(Attendee attendee, User user) {
            this.attendee = attendee;
            this.user = user;
        }

        /**
         * Returns the attendee
         * @return
         */
        public Attendee getAttendee() {
            return attendee;
        }

        /**
         * Returns the user
         * @return
         */
        public User getUser() {
            return user;
        }
    }

    /**
     * Constructor for the AttendeeViewModel
     */
    public AttendeeViewModel() {
        this.attendeeController = AttendeeController.getInstance();
    }

    /**
     * Returns LiveData containing all attendance records.
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
     * Fetches all attendance records.
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
     * @param eventId
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
     * Fetches details of a specific attendee by ID.
     * @param attendeeId
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
     * @param eventId
     */
    public void joinWaitingList(String eventId) {
        isLoadingLiveData.setValue(true);
        attendeeController.joinWaitingList(eventId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Fetch all attendances again to update UI
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
     * Joins the waiting list for a specific event with a location.
     * @param userId
     * @param eventId
     * @param location
     */
    public void joinWaitingList(String userId, String eventId, @Nullable Location location) {
        isLoadingLiveData.setValue(true);
        attendeeController.joinWaitingList(userId, eventId, location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Fetch all attendances again to update UI
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
     * @param eventId
     */
    public void leaveWaitingList(String eventId) {
        isLoadingLiveData.setValue(true);
        attendeeController.leaveWaitingList(eventId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Fetch all attendances again to update UI
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
     * Leaves the waiting list for a specific event.
     * @param eventId
     * @param userId
     */
    public void leaveWaitingList(String eventId, String userId) {
        isLoadingLiveData.setValue(true);
        attendeeController.leaveWaitingList(userId, eventId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Fetch attendances by event again to update UI
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
     * Updates an attendance record with new details.
     * @param attendee
     */
    public void updateAttendance(Attendee attendee) {
        isLoadingLiveData.setValue(true);
        attendeeController.updateAttendance(attendee)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Fetch the updated attendee details
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
     * Deletes an attendance record.
     * @param attendeeId
     */
    public void deleteAttendance(String attendeeId) {
        isLoadingLiveData.setValue(true);
        attendeeController.deleteAttendance(attendeeId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Fetch all attendances again to update UI
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
     * Checks in a user to an event.
     * @param eventId
     * @param location
     */
    public void checkInUser(String eventId, @Nullable Location location) {
        isLoadingLiveData.setValue(true);
        String attendeeId = generateAttendeeId(eventId);
        attendeeController.checkInAttendee(attendeeId, location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Fetch the updated attendee details
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
     * Checks out a user from an event.
     * @param eventId
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
                                newAttendee.setNumberInLine(calculateNumberInLine(eventId));
                                // Update the attendee record
                                attendeeController.updateAttendance(newAttendee)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Notify the new attendee about their selection
                                                attendeeController.notifyAttendeebyId(newAttendee.getId());
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
     * Calculates the number of attendees in line for an event.
     * @param eventId
     * @return
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
     * @param attendeeId
     * @param isSelected
     */
    private void notifyAttendee(String attendeeId, boolean isSelected) {
        // This method is now handled by AttendeeController's notifyAttendee method
        // Therefore, this can be removed or left empty if needed for future use
    }

    /**
     * Clears the error message LiveData.
     */
    public void clearErrorMessage() {
        errorMessageLiveData.setValue(null);
    }

    /**
     * Generates an attendee ID based on the current user and event ID.
     * @param eventId
     * @return
     */
    private String generateAttendeeId(String eventId) {
        String userId = CurrentUserHandler.getSingleton().getCurrentUserId();
        return Attendee.generateId(userId, eventId);
    }

    /**
     * Fetches attendees for an event with user information.
     * @param eventId
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


    public void fetchAttendeeListsWithUserInfo(String eventId) {
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
     * Checks if a user is on the waiting list for an event.
     * @param userId
     * @param eventId
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
