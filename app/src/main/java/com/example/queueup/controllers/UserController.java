package com.example.queueup.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.example.queueup.handlers.PushNotificationHandler;
import com.example.queueup.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class UserController {
    private static UserController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userCollectionReference = db.collection("users");
    private static final String PREFS_NAME = "QueueUpPrefs";
    private static final String DEVICE_ID_KEY = "device_id";

    private UserController() {}

    public static UserController getInstance() {
        if (singleInstance == null) {
            singleInstance = new UserController();
        }
        return singleInstance;
    }

    /**
     * Get all users from the Firestore database.
     *
     * @return all users.
     */
    public Task<QuerySnapshot> getAllUsers() {
        return userCollectionReference.get();
    }

    /**
     * Get a user by their user ID.
     *
     * @param userId
     * @return user
     */
    public Task<DocumentSnapshot> getUserById(String userId) {
        return userCollectionReference.document(userId).get();
    }

    /**
     * Delete a user by their user ID.
     *
     * @param userId
     * @return delete user
     */
    public Task<Void> deleteUserById(String userId) {
        return userCollectionReference.document(userId).delete();
    }

    /**
     * Update a user by their user ID.
     *
     * @param userId
     * @param field
     * @param value
     * @return updated user
     */
    public Task<Void> updateUserById(String userId, String field, Object value) {
        return userCollectionReference.document(userId).update(field, value);
    }

    /**
     * Create a new user
     *
     * @param user
     * @return new user
     */
    public Task<Void> createUser(User user) {
        return userCollectionReference.document(user.getUuid()).set(user);
    }

    /**
     * Update a user in the Firestore database.
     *
     * @param user
     * @return updated user
     */
    public Task<Void> updateUser(User user) {
        return userCollectionReference.document(user.getUuid()).set(user);
    }


    /**
     * Update a user's profile picture.
     *
     * @param userId
     * @param profileImageUrl
     * @return updated user
     */
    public Task<Void> updateProfilePicture(String userId, String profileImageUrl) {
        return userCollectionReference.document(userId).update("profileImageUrl", profileImageUrl);
    }

    /**
     * Remove a user's profile picture.
     *
     * @param userId
     * @return updated user
     */
    public Task<Void> removeProfilePicture(String userId) {
        return userCollectionReference.document(userId).update("profileImageUrl", null);
    }

    /**
     * Update a user's notification preferences.
     *
     * @param userId
     * @param receiveNotifications
     * @return updated user
     */
    public Task<Void> updateNotificationPreferences(String userId, boolean receiveNotifications) {
        return userCollectionReference.document(userId).update("receiveNotifications", receiveNotifications);
    }

    /**
     * Get the device ID for the current device.
     *
     * @param context
     * @return The device ID.
     */
    public String getDeviceId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String deviceId = prefs.getString(DEVICE_ID_KEY, null);
        if (deviceId == null) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            prefs.edit().putString(DEVICE_ID_KEY, deviceId).apply();
        }
        return deviceId;
    }

    /**
     * Get a user by their device ID.
     *
     * @param deviceId
     * @return user
     */
    public Task<QuerySnapshot> getUserByDeviceId(String deviceId) {
        return userCollectionReference.whereEqualTo("deviceId", deviceId).get();
    }



    /**
     * Checks if a user has enabled a specific type of notification.
     *
     * @param userId
     * @return Task<Boolean>
     */
    public Task<Boolean> isNotificationEnabled(String userId, PushNotificationHandler.NotificationType type) {
        return getUserById(userId).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                User user = task.getResult().toObject(User.class);
                if (user != null) {
                    return user.isReceiveAllNotifications();
                }
                return false;
            } else {
                throw new RuntimeException("User not found or failed to retrieve.");
            }
        });
    }
    public Task<Void> notifyUserById(String userId, String notification) {
        return userCollectionReference.document(userId).update("notifications", FieldValue.arrayUnion(notification));
    }

}
