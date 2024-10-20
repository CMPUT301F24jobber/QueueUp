package com.example.queueup.handlers;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import com.example.queueup.controllers.UserController;
import com.example.queueup.viewmodels.UserViewModel;
import com.example.queueup.models.User;
import com.google.firebase.messaging.FirebaseMessaging;

public class CurrentUserHandler {
    private static CurrentUserHandler singleInstance = null;
    private static UserViewModel userViewModel;
    private static AppCompatActivity ownerActivity;
    private static UserController userController = UserController.getInstance();

    public static void setSingleton() {
        if (ownerActivity == null) {
            throw new RuntimeException("Owner activity must be set in MainActivity.");
        }
        singleInstance = new CurrentUserHandler();
        userViewModel = new ViewModelProvider(ownerActivity).get(UserViewModel.class);
    }

    public static CurrentUserHandler getSingleton() {
        if (singleInstance == null) {
            setSingleton();
        }
        return singleInstance;
    }

    private CurrentUserHandler() {}

    public static void setOwnerActivity(AppCompatActivity activity) {
        ownerActivity = activity;
    }

    public String getCurrentUserId() {
        LiveData<User> userLiveData = userViewModel.getCurrentUser();
        User user = userLiveData.getValue();
        return user != null ? user.getUuid() : null;
    }

    public User getCurrentUser() {
        LiveData<User> userLiveData = userViewModel.getCurrentUser();
        return userLiveData.getValue();
    }

    public void loginWithDeviceId() {
        String deviceId = userViewModel.getDeviceId();
        userViewModel.loadUserByDeviceId(deviceId);
    }

    public void createUser(User user) {
        userViewModel.createUser(user);
    }

    public void updateUser(User user) {
        userViewModel.updateUser(user);
    }

    public void checkAndUpdateFcmToken() {
        String userId = getCurrentUserId();
        if (userId == null) return;

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String newFcmToken = task.getResult();
                        Log.d("CurrentUserHandler", "Retrieved FCM Token: " + newFcmToken);
                        userController.updateNotificationPreferences(userId, true)
                                .addOnSuccessListener(aVoid -> Log.d("CurrentUserHandler", "User FCM token successfully updated."))
                                .addOnFailureListener(e -> Log.e("CurrentUserHandler", "Failed to update user FCM token.", e));
                    } else {
                        Log.e("CurrentUserHandler", "Failed to generate new FCM token.", task.getException());
                    }
                });
    }

    public void joinWaitingList(String eventId) {
        userViewModel.joinWaitingList(eventId);
    }

    public void leaveWaitingList(String eventId) {
        userViewModel.leaveWaitingList(eventId);
    }

    public void updateProfilePicture(String profileImageUrl) {
        String userId = getCurrentUserId();
        if (userId != null) {
            userViewModel.updateProfilePicture(userId, profileImageUrl);
        }
    }

    public void removeProfilePicture() {
        String userId = getCurrentUserId();
        if (userId != null) {
            userViewModel.removeProfilePicture(userId);
        }
    }

    public void updateNotificationPreferences(boolean receiveNotifications) {
        String userId = getCurrentUserId();
        if (userId != null) {
            userViewModel.updateNotificationPreferences(userId, receiveNotifications);
        }
    }

    public LiveData<String> getErrorMessage() {
        return userViewModel.getErrorMessage();
    }
}