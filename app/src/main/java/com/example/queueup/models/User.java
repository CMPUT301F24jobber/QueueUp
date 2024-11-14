package com.example.queueup.models;

import android.os.Parcelable;
import android.os.Parcel;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.Exclude;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user in the system with attributes such as name, username, email address,
 * phone number, profile image URL, and more. This class provides methods to access and
 * modify these attributes, handle user notifications, and manage waiting list events.
 */
public class User implements Parcelable {
    private String uuid;
    private String firstName;
    private String lastName;
    private String username;
    private String emailAddress;
    private String phoneNumber;
    private Boolean isadmin;
    private String profileImageUrl;
    private HashMap<String, String> notifications;
    private String deviceId;
    private boolean receiveNotifications;
    private List<String> waitingListEvents;
    private String FCMToken;

    /**
     * Default constructor required for Firestore.
     */
    public User() {
        this.notifications = new HashMap<>();
        this.waitingListEvents = new ArrayList<>();
        this.receiveNotifications = true;
    }

    /**
     * Constructs a new User with the specified details.
     *
     * @param firstName    The user's first name.
     * @param lastName     The user's last name.
     * @param username     The user's username.
     * @param emailAddress The user's email address.
     * @param phoneNumber  The user's phone number.
     * @param deviceId     The user's device ID.
     * @param isadmin      The user's admin status.
     */
    public User(String firstName, String lastName, String username, String emailAddress, String phoneNumber, String deviceId, Boolean isadmin) {
        this();
        this.uuid = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.deviceId = deviceId;
        this.isadmin = isadmin;
    }

    protected User(Parcel in) {
        uuid = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        username = in.readString();
        emailAddress = in.readString();
        phoneNumber = in.readString();
        isadmin = in.readByte() != 0;
        profileImageUrl = in.readString();
        deviceId = in.readString();
        receiveNotifications = in.readByte() != 0;
        waitingListEvents = in.createStringArrayList();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * Returns the user's UUID.
     *
     * @return The UUID of the user.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the user's UUID.
     *
     * @param uuid The UUID to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the user's first name.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the initials of the user (first letter of first and last name).
     *
     * @return The initials of the user.
     */
    public String getInitials() {
        return firstName.substring(0, 1).toUpperCase() + lastName.substring(0, 1).toUpperCase();
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the user's last name.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the user's username.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's email address.
     *
     * @return The email address of the user.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the user's email address.
     *
     * @param emailAddress The email address to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Returns the user's phone number.
     *
     * @return The phone number of the user.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phoneNumber The phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns whether the user is an admin.
     *
     * @return True if the user is an admin, otherwise false.
     */
    public Boolean getIsadmin() {
        return isadmin;
    }

    /**
     * Sets the user's admin status.
     *
     * @param isadmin The admin status to set.
     */
    public void setIsadmin(Boolean isadmin) {
        this.isadmin = isadmin;
    }

    /**
     * Returns the URL of the user's profile image.
     *
     * @return The profile image URL.
     */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    /**
     * Sets the URL of the user's profile image.
     *
     * @param profileImageUrl The profile image URL to set.
     */
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        Log.d("User", "Profile Image URL set: " + profileImageUrl);
    }

    /**
     * Returns the user's notifications as a map.
     *
     * @return The notifications map.
     */
    public HashMap<String, String> getNotifications() {
        return notifications;
    }

    /**
     * Sets the user's notifications.
     *
     * @param notifications The notifications map to set.
     */
    public void setNotifications(HashMap<String, String> notifications) {
        this.notifications = notifications;
    }

    /**
     * Returns the user's device ID.
     *
     * @return The device ID.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the user's device ID.
     *
     * @param deviceId The device ID to set.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Returns the user's FCM token.
     *
     * @return The FCM token.
     */
    public String getFCMToken() {
        return FCMToken;
    }

    /**
     * Sets the user's FCM token.
     *
     * @param FCMToken The FCM token to set.
     */
    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    /**
     * Returns whether the user receives notifications.
     *
     * @return True if the user receives notifications, otherwise false.
     */
    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    /**
     * Sets whether the user receives notifications.
     *
     * @param receiveNotifications The receive notifications status to set.
     */
    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    /**
     * Returns the list of waiting list events for the user.
     *
     * @return The list of waiting list events.
     */
    public List<String> getWaitingListEvents() {
        return waitingListEvents;
    }

    /**
     * Sets the list of waiting list events for the user.
     *
     * @param waitingListEvents The list of waiting list events to set.
     */
    public void setWaitingListEvents(List<String> waitingListEvents) {
        this.waitingListEvents = waitingListEvents;
    }

    // Methods

    /**
     * Adds a notification to the user's notifications map.
     *
     * @param notificationId     The notification ID.
     * @param notificationContent The notification content.
     */
    public void addNotification(String notificationId, String notificationContent) {
        this.notifications.put(notificationId, notificationContent);
    }

    /**
     * Removes a notification from the user's notifications map.
     *
     * @param notificationId The notification ID to remove.
     */
    public void removeNotification(String notificationId) {
        this.notifications.remove(notificationId);
    }

    /**
     * Clears all notifications for the user.
     */
    public void clearNotifications() {
        this.notifications.clear();
    }

    /**
     * Adds an event to the user's waiting list.
     *
     * @param eventId The event ID to add.
     */
    public void joinWaitingList(String eventId) {
        if (!this.waitingListEvents.contains(eventId)) {
            this.waitingListEvents.add(eventId);
        }
    }

    /**
     * Removes an event from the user's waiting list.
     *
     * @param eventId The event ID to remove.
     */
    public void leaveWaitingList(String eventId) {
        this.waitingListEvents.remove(eventId);
    }
    /**
     * Removes the user's profile picture by setting the URL to null.
     */
    public void removeProfilePicture() {
        this.profileImageUrl = null;
    }

    /**
     * Returns the first character of the user's first name for display as a profile picture placeholder.
     *
     * @return The first character of the user's first name.
     */
    public String pfp() {
        return this.firstName.substring(0, 1);
    }

    /**
     * Returns the full name of the user. This method is excluded from Firestore serialization.
     *
     * @return The user's full name (first name and last name concatenated).
     */
    @Exclude
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(username);
        parcel.writeString(emailAddress);
        parcel.writeString(phoneNumber);
        parcel.writeByte((byte) (isadmin ? 1 : 0));
        parcel.writeString(profileImageUrl);
        parcel.writeString(deviceId);
        parcel.writeByte((byte) (receiveNotifications ? 1 : 0));
        parcel.writeStringList(waitingListEvents);
    }
}
