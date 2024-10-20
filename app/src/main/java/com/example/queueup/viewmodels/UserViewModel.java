package com.example.queueup.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.queueup.models.User;
import com.example.queueup.controllers.UserController;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserViewModel extends AndroidViewModel {
    private final UserController userController;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(Application application) {
        super(application);
        userController = UserController.getInstance();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadUserByDeviceId(String deviceId) {
        userController.getUserByDeviceId(deviceId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        currentUser.setValue(documentSnapshot.toObject(User.class));
                    } else {
                        errorMessage.setValue("User not found");
                    }
                })
                .addOnFailureListener(e -> errorMessage.setValue("Error loading user: " + e.getMessage()));
    }

    public Task<Void> createUser(User user) {
        return userController.createUser(user)
                .addOnSuccessListener(aVoid -> currentUser.setValue(user))
                .addOnFailureListener(e -> errorMessage.setValue("Error creating user: " + e.getMessage()));
    }

    public Task<Void> updateUser(User user) {
        return userController.updateUser(user)
                .addOnSuccessListener(aVoid -> currentUser.setValue(user))
                .addOnFailureListener(e -> errorMessage.setValue("Error updating user: " + e.getMessage()));
    }

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

    public void joinWaitingList(String eventId) {
        User user = currentUser.getValue();
        if (user != null) {
            user.joinWaitingList(eventId);
            updateUser(user);
        } else {
            errorMessage.setValue("No current user");
        }
    }

    public void leaveWaitingList(String eventId) {
        User user = currentUser.getValue();
        if (user != null) {
            user.leaveWaitingList(eventId);
            updateUser(user);
        } else {
            errorMessage.setValue("No current user");
        }
    }

    public String getDeviceId() {
        return userController.getDeviceId(getApplication());
    }
}