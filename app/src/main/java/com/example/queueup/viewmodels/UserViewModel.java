package com.example.queueup.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.queueup.models.User;
import com.example.queueup.controllers.UserController;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * ViewModel class that handles user-related operations such as loading, creating, updating,
 * and managing user data in Firestore. This ViewModel interacts with the UserController to
 * perform the necessary database operations and provides LiveData for the current user and error messages.
 */
public class UserViewModel extends AndroidViewModel {
    private final UserController userController;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /**
     * Constructor for the UserViewModel.
     *
     * @param application The application context.
     */
    public UserViewModel(@NonNull Application application) {
        super(application);
        userController = UserController.getInstance();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Loads a user by device ID.
     *
     * @param deviceId The device ID to search for.
     */
    public void loadUserByDeviceId(String deviceId) {
        userController.getUserByDeviceId(deviceId)
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        User user = document.toObject(User.class);
                        currentUser.setValue(user);
                    } else {
                        errorMessage.setValue("User not found");
                    }
                })
                .addOnFailureListener(e -> errorMessage.setValue("Error loading user: " + e.getMessage()));
    }

    /**
     * Creates a new user in Firestore.
     *
     * @param user The user to create.
     * @return A task that resolves when the user is created.
     */
    public Task<Void> createUser(User user) {
        return userController.createUser(user)
                .addOnSuccessListener(aVoid -> currentUser.setValue(user))
                .addOnFailureListener(e -> errorMessage.setValue("Error creating user: " + e.getMessage()));
    }

    /**
     * Updates an existing user in Firestore.
     *
     * @param user The user to update.
     * @return A task that resolves when the user is updated.
     */
    public Task<Void> updateUser(User user) {
        return userController.updateUser(user)
                .addOnSuccessListener(aVoid -> currentUser.setValue(user))
                .addOnFailureListener(e -> errorMessage.setValue("Error updating user: " + e.getMessage()));
    }

    /**
     * Updates a user's profile picture.
     *
     * @param userId The user ID to update.
     * @param profileImageUrl The new profile image URL.
     * @return A task that resolves when the profile picture is updated.
     */
    public Task<Void> updateProfilePicture(String userId, String profileImageUrl) {
        return userController.updateProfilePicture(userId, profileImageUrl)
                .addOnSuccessListener(aVoid -> {
                    User user = currentUser.getValue();
                    if (user != null) {
                        user.setProfileImageUrl(profileImageUrl);
                        currentUser.setValue(user);
                    }
                })
                .addOnFailureListener(e -> errorMessage.setValue("Error updating profile picture: " + e.getMessage()));
    }

    /**
     * Removes a user's profile picture.
     *
     * @param userId The user ID to update.
     * @return A task that resolves when the profile picture is removed.
     */
    public Task<Void> removeProfilePicture(String userId) {
        return userController.removeProfilePicture(userId)
                .addOnSuccessListener(aVoid -> {
                    User user = currentUser.getValue();
                    if (user != null) {
                        user.removeProfilePicture();
                        currentUser.setValue(user);
                    }
                })
                .addOnFailureListener(e -> errorMessage.setValue("Error removing profile picture: " + e.getMessage()));
    }

    /**
     * Updates a user's notification preferences.
     *
     * @param userId The user ID to update.
     * @param receiveNotifications Whether to receive notifications.
     * @return A task that resolves when the preferences are updated.
     */
    public Task<Void> updateNotificationPreferences(String userId, boolean receiveNotifications) {
        return userController.updateNotificationPreferences(userId, receiveNotifications)
                .addOnSuccessListener(aVoid -> {
                    User user = currentUser.getValue();
                    if (user != null) {
                        user.setReceiveNotifications(receiveNotifications);
                        currentUser.setValue(user);
                    }
                })
                .addOnFailureListener(e -> errorMessage.setValue("Error updating notification preferences: " + e.getMessage()));
    }

    // Additional methods related to waiting lists can remain unchanged

    /**
     * Retrieves the device ID from the UserController.
     *
     * @return The device ID.
     */
    public String getDeviceId() {
        return userController.getDeviceId(getApplication());
    }
}