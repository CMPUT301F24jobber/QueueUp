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
    private String role;
    private String profileImageUrl;
    private HashMap<String, String> notifications;
    private String deviceId;
    private boolean receiveNotifications;
    private List<String> waitingListEvents;


    // Default constructor required for Firestore
    public User() {
        this.notifications = new HashMap<>();
        this.waitingListEvents = new ArrayList<>();
        this.receiveNotifications = true;
    }

    public User(String firstName, String lastName, String username, String emailAddress, String phoneNumber, String deviceId) {
        this();
        this.uuid = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.deviceId = deviceId;
        this.role = "admin"; // Default role set during creation
    }

    protected User(Parcel in) {
        uuid = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        username = in.readString();
        emailAddress = in.readString();
        phoneNumber = in.readString();
        role = in.readString();
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

    // Getters and setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getInitials() {
        return firstName.substring(0, 1).toUpperCase() + lastName.substring(0, 1).toUpperCase();
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        Log.d("User", "Profile Image URL set: " + profileImageUrl);
    }

    public HashMap<String, String> getNotifications() {
        return notifications;
    }

    public void setNotifications(HashMap<String, String> notifications) {
        this.notifications = notifications;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public List<String> getWaitingListEvents() {
        return waitingListEvents;
    }

    public void setWaitingListEvents(List<String> waitingListEvents) {
        this.waitingListEvents = waitingListEvents;
    }

    // Methods
    public void addNotification(String notificationId, String notificationContent) {
        this.notifications.put(notificationId, notificationContent);
    }

    public void removeNotification(String notificationId) {
        this.notifications.remove(notificationId);
    }

    public void clearNotifications() {
        this.notifications.clear();
    }

    public void joinWaitingList(String eventId) {
        if (!this.waitingListEvents.contains(eventId)) {
            this.waitingListEvents.add(eventId);
        }
    }

    public void leaveWaitingList(String eventId) {
        this.waitingListEvents.remove(eventId);
    }

    public void removeProfilePicture() {
        this.profileImageUrl = null;
    }


    public String pfp() {
        return this.firstName.substring(0, 1);
    }

    // Exclude the full name from Firestore
    @Exclude
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role='" + role + '\'' +
                ", receiveNotifications=" + receiveNotifications +
                '}';
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
        parcel.writeString(role);
        parcel.writeString(profileImageUrl);
        parcel.writeString(deviceId);
        parcel.writeByte((byte) (receiveNotifications ? 1 : 0));
        parcel.writeStringList(waitingListEvents);
    }
}