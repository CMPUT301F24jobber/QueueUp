package com.example.queueup.handlers;

import android.util.Log;
import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.controllers.UserController;
import com.example.queueup.viewmodels.UserViewModel;
import com.example.queueup.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class CurrentUserHandler {
    private static CurrentUserHandler singleInstance = null;
    public static UserViewModel userViewModel;
    private static AppCompatActivity ownerActivity;
    private static UserController userController = UserController.getInstance();
    private static String cachedUserId = null;

    private static void setSingleton() {
        if (ownerActivity == null) {
            throw new RuntimeException("Owner activity must be set in MainActivity.");
        }
        singleInstance = new CurrentUserHandler();
        userViewModel = new ViewModelProvider(ownerActivity).get(UserViewModel.class);

        userViewModel.getCurrentUser().observe(ownerActivity, User -> {
            if (User != null) {
                singleInstance.cachedUserId = User.getUuid();
            }

        });
    }


    public static CurrentUserHandler getSingleton() {
        if (singleInstance == null) {
            setSingleton();
        }
        if (userViewModel.getCurrentUser() != null && userViewModel.getCurrentUser().getValue() != null) {
            cachedUserId = userViewModel.getCurrentUser().getValue().getUuid();
        }
        return singleInstance;
    }

    private CurrentUserHandler() {}

    /**
     * Sets the owner activity for the handler.
     *
     * @param activity
     */
    public static void setOwnerActivity(AppCompatActivity activity) {
        ownerActivity = activity;
    }

    /**
     * Retrieves the current user's ID.
     *
     * @return The current user's UUID
     */
    public String getCurrentUserId() {
        return cachedUserId;
    }

    /**
     * Retrieves the current User object.
     *
     * @return The current User
     */
    public LiveData<User> getCurrentUser() {
        return userViewModel.getCurrentUser();
    }

    /**
     * Initiates login by device ID.
     */
    public void loginWithDeviceId(Runnable loginCallback) {
        String deviceId = userViewModel.getDeviceId();
        userViewModel.loadUserByDeviceId(deviceId);

        // Observe for the loaded user data
        userViewModel.getCurrentUser().observe(ownerActivity, user -> {
            if (user != null) {
                singleInstance.cachedUserId = user.getUuid();
                if (loginCallback != null) {
                    loginCallback.run();
                }
            }
        });
    }

    /**
     * Creates a new user.
     *
     * @param user The user to create.
     */
    public Task<Void> createUser(User user) {
        return userViewModel.createUser(user);
    }


    /**
     * Updates an existing user.
     *
     * @param user The user to update.
     */
    public void updateUser(User user) {
        userViewModel.updateUser(user);
    }

    /**
     * Retrieves error messages from the ViewModel.
     *
     * @return error message
     */
    public LiveData<String> getErrorMessage() {
        return userViewModel.getErrorMessage();
    }

    /**
     * Retrieves LiveData for the current user.
     *
     * @return user
     */
    public LiveData<User> getCurrentUserLiveData() {
        return userViewModel.getCurrentUser();
    }

    /**
     * Retrieves the device ID.
     *
     * @return The device ID.
     */
    public String getDeviceId() {
        return userViewModel.getDeviceId();
    }

}