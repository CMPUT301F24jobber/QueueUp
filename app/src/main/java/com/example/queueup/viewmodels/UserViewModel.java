package com.example.queueup.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;


public class UserViewModel extends AndroidViewModel {
    private final UserController userController;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();


    public UserViewModel(@NonNull Application application) {
        super(application);
        userController = UserController.getInstance();

    }

    /**
     * Returns the LiveData object for the current user.
     * @return LiveData<User>
     */
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    /**
     * Returns the LiveData object for the error message.
     * @return LiveData<String>
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }


    /**
     * load device id
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
     * create user
     */
    public Task<Void> createUser(User user) {
        return userController.createUser(user)
                .addOnSuccessListener(aVoid -> currentUser.setValue(user))
                .addOnFailureListener(e -> errorMessage.setValue("Error creating user: " + e.getMessage()));
    }

    /**
     * update user
     */
    public Task<Void> updateUser(User user) {
        return userController.updateUser(user)
                .addOnSuccessListener(aVoid -> currentUser.setValue(user))
                .addOnFailureListener(e -> errorMessage.setValue("Error updating user: " + e.getMessage()));
    }

    /**
     * update picture
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
     * remove picture
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
     * update notification preferences
     */
    public Task<Void> updateNotificationPreferences(String userId, boolean receiveNotifications) {
        return userController.updateNotificationPreferences(userId, receiveNotifications)
                .addOnSuccessListener(aVoid -> {
                    User user = currentUser.getValue();
                    if (user != null) {
                        user.setReceiveAllNotifications(receiveNotifications);
                        currentUser.setValue(user);
                    }
                })
                .addOnFailureListener(e -> errorMessage.setValue("Error updating notification preferences: " + e.getMessage()));
    }

    /**
     * get device id
     */
    public String getDeviceId() {
        return userController.getDeviceId(getApplication());
    }
}