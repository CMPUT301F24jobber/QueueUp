package com.example.queueup.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.example.queueup.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserController {
    private static UserController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userCollectionReference = db.collection("users");
    private static final String PREFS_NAME = "QueueUpPrefs";
    private static final String DEVICE_ID_KEY = "device_id";

    private UserController() {}

    /**
     * Get the singleton instance of the UserController.
     *
     * @return The UserController instance.
     */
    public static UserController getInstance() {
        if (singleInstance == null) {
            singleInstance = new UserController();
        }
        return singleInstance;
    }

    /**
     * Get all users from the Firestore database.
     *
     * @return A task that resolves with a QuerySnapshot containing all users.
     */
    public Task<QuerySnapshot> getAllUsers() {
        return userCollectionReference.get();
    }

    /**
     * Get a user by their user ID.
     *
     * @param userId The user ID to search for.
     * @return A task that resolves with a DocumentSnapshot containing the user.
     */
    public Task<DocumentSnapshot> getUserById(String userId) {
        return userCollectionReference.document(userId).get();
    }

    /**
     * Delete a user by their user ID.
     *
     * @param userId The user ID to delete.
     * @return A task that resolves when the user is deleted.
     */
    public Task<Void> deleteUserById(String userId) {
        return userCollectionReference.document(userId).delete();
    }

    /**
     * Update a user by their user ID.
     *
     * @param userId The user ID to update.
     * @param field The field to update.
     * @param value The value to update the field to.
     * @return A task that resolves when the user is updated.
     */
    public Task<Void> updateUserById(String userId, String field, Object value) {
        return userCollectionReference.document(userId).update(field, value);
    }

    /**
     * Create a new user in the Firestore database.
     *
     * @param user The user to create.
     * @return A task that resolves when the user is created.
     */
    public Task<Void> createUser(User user) {
        return userCollectionReference.document(user.getUuid()).set(user);
    }

    /**
     * Update a user in the Firestore database.
     *
     * @param user The user to update.
     * @return A task that resolves when the user is updated.
     */
    public Task<Void> updateUser(User user) {
        return userCollectionReference.document(user.getUuid()).set(user);
    }

    /**
     * Update a user's profile picture.
     *
     * @param userId The user ID to update the profile picture for.
     * @param profileImageUrl The URL of the new profile picture.
     * @return A task that resolves when the profile picture is updated.
     */
    public Task<Void> updateProfilePicture(String userId, String profileImageUrl) {
        return userCollectionReference.document(userId).update("profileImageUrl", profileImageUrl);
    }

    /**
     * Remove a user's profile picture.
     *
     * @param userId The user ID to remove the profile picture for.
     * @return A task that resolves when the profile picture is removed.
     */
    public Task<Void> removeProfilePicture(String userId) {
        return userCollectionReference.document(userId).update("profileImageUrl", null);
    }

    /**
     * Update a user's notification preferences.
     *
     * @param userId The user ID to update the notification preferences for.
     * @param receiveNotifications Whether the user should receive notifications.
     * @return A task that resolves when the notification preferences are updated.
     */
    public Task<Void> updateNotificationPreferences(String userId, boolean receiveNotifications) {
        return userCollectionReference.document(userId).update("receiveNotifications", receiveNotifications);
    }

    /**
     * Get the device ID for the current device.
     *
     * @param context The context to use to get the device ID.
     * @return The device ID for the current device.
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
     * @param deviceId The device ID to search for.
     * @return A task that resolves with a QuerySnapshot containing the user.
     */
    public Task<QuerySnapshot> getUserByDeviceId(String deviceId) {
        return userCollectionReference.whereEqualTo("deviceId", deviceId).get();
    }
}
