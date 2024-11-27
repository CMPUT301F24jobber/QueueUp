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
    private GeoLocation geoLocation;


    public User() {
        this.notifications = new HashMap<>();
        this.waitingListEvents = new ArrayList<>();
        this.receiveNotifications = true;
    }


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
        FCMToken = in.readString();
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

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }


    /**
     * Returns the unique identifier of the user.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the unique identifier of the user.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the initials of the user.
     */
    public String getInitials() {
        return firstName.substring(0, 1).toUpperCase() + lastName.substring(0, 1).toUpperCase();
    }

    /**
     * Sets the first name of the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the email address of the user.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the email address of the user.
     * @param emailAddress
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Returns the phone number of the user.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns whether the user is an admin.
     */
    public Boolean getIsadmin() {
        return isadmin;
    }

    /**
     * Sets whether the user is an admin.
     * @param isadmin
     */
    public void setIsadmin(Boolean isadmin) {
        this.isadmin = isadmin;
    }

    /**
     * Returns the profile image URL of the user.
     */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    /**
     * Sets the profile image URL of the user.
     * @param profileImageUrl
     */
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        Log.d("User", "Profile Image URL set: " + profileImageUrl);
    }

    /**
     * Returns the notifications of the user.
     */
    public HashMap<String, String> getNotifications() {
        return notifications;
    }

    /**
     * Sets the notifications of the user.
     * @param notifications
     */
    public void setNotifications(HashMap<String, String> notifications) {
        this.notifications = notifications;
    }

    /**
     * Returns the device ID of the user.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device ID of the user.
     * @param deviceId
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Returns the FCM token of the user.
     */
    public String getFCMToken() {
        return FCMToken;
    }

    /**
     * Sets the FCM token of the user.
     * @param FCMToken
     */
    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    /**
     * Returns whether the user receives notifications.
     */
    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    /**
     * Sets whether the user receives notifications.
     * @param receiveNotifications
     */
    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    /**
     * Returns the events the user is on the waiting list for.
     */
    public List<String> getWaitingListEvents() {
        return waitingListEvents;
    }

    /**
     * Sets the events the user is on the waiting list for.
     * @param waitingListEvents
     */
    public void setWaitingListEvents(List<String> waitingListEvents) {
        this.waitingListEvents = waitingListEvents;
    }

    /**
     * Adds a notification to the user.
     * @param notificationId
     * @param notificationContent
     */
    public void addNotification(String notificationId, String notificationContent) {
        this.notifications.put(notificationId, notificationContent);
    }

    /**
     * Removes a notification from the user.
     * @param notificationId
     */
    public void removeNotification(String notificationId) {
        this.notifications.remove(notificationId);
    }

    /**
     * Clears all notifications from the user.
     */
    public void clearNotifications() {
        this.notifications.clear();
    }

    /**
     * Adds an event to the user's waiting list.
     * @param eventId
     */
    public void joinWaitingList(String eventId) {
        if (!this.waitingListEvents.contains(eventId)) {
            this.waitingListEvents.add(eventId);
        }
    }

    /**
     * Removes an event from the user's waiting list.
     * @param eventId
     */
    public void leaveWaitingList(String eventId) {
        this.waitingListEvents.remove(eventId);
    }

    /**
     * Removes the user's profile picture.
     */
    public void removeProfilePicture() {
        this.profileImageUrl = null;
    }

    /**
     * Returns the user's profile picture.
     */
    public String pfp() {
        return this.firstName.substring(0, 1);
    }

    /**
     * Returns the full name of the user.
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
        parcel.writeString(FCMToken);
    }
}