package com.example.queueup.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Represents an event with comprehensive details including its schedule,
 * location, organizer, and participant information. This class manages
 * event attendance, announcements, and facilitates event check-ins via QR codes.
 */
public class Event implements Serializable {
    private String eventName;
    private String eventLocation;
    private String eventDescription;
    private String organizerId;
    private String eventBannerImageUrl;
    private String eventId;
    private ArrayList<String> attendeeIds;
    private ArrayList<HashMap<String, String>> announcementList;
    private Date eventStartDate;
    private Date eventEndDate;
    private Date creationDate;
    private String checkInQrCodeId;
    private Boolean isActive;
    private int maxCapacity;
    private int currentCapacity;

    /**
     * Constructs a new {@code Event} instance with default values.
     */
    public Event() {}

    /**
     * Retrieves the name of the event.
     *
     * @return the event's name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the name of the event.
     *
     * @param eventName the desired name for the event
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Retrieves the location where the event will take place.
     *
     * @return the event's location
     */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Sets the location for the event.
     *
     * @param eventLocation the desired location for the event
     */
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    /**
     * Retrieves the description of the event.
     *
     * @return the event's description
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Sets the description for the event.
     *
     * @param eventDescription a detailed description of the event
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * Retrieves the unique identifier of the event.
     *
     * @return the event's ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the unique identifier for the event.
     *
     * @param eventId a unique ID for the event
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Retrieves the list of attendee IDs registered for the event.
     *
     * @return a list of attendee IDs
     */
    public ArrayList<String> getAttendeeIds() {
        return attendeeIds;
    }

    /**
     * Retrieves the list of announcements related to the event.
     *
     * @return a list of announcements, each represented as a key-value pair
     */
    public ArrayList<HashMap<String, String>> getAnnouncementList() {
        return announcementList;
    }

    /**
     * Adds a new announcement to the event.
     *
     * @param announcement a map containing announcement details
     */
    public void addAnnouncement(HashMap<String, String> announcement) {
        announcementList.add(announcement);
    }

    /**
     * Sets the list of announcements for the event.
     *
     * @param announcementList a list of announcements to associate with the event
     */
    public void setAnnouncementList(ArrayList<HashMap<String, String>> announcementList) {
        this.announcementList = announcementList;
    }

    /**
     * Retrieves the start date and time of the event.
     *
     * @return the event's start date
     */
    public Date getEventStartDate() {
        return eventStartDate;
    }

    /**
     * Sets the start date and time for the event.
     *
     * @param eventStartDate the desired start date for the event
     */
    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    /**
     * Retrieves the end date and time of the event.
     *
     * @return the event's end date
     */
    public Date getEventEndDate() {
        return eventEndDate;
    }

    /**
     * Sets the end date and time for the event.
     *
     * @param eventEndDate the desired end date for the event
     */
    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    /**
     * Retrieves the creation date of the event record.
     *
     * @return the date when the event was created
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date for the event record.
     *
     * @param creationDate the date when the event was created
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the QR code identifier used for event check-in.
     *
     * @return the QR code ID for event check-in
     */
    public String getCheckInQrCodeId() {
        return checkInQrCodeId;
    }

    /**
     * Sets the QR code identifier for event check-in.
     *
     * @param checkInQrCodeId the QR code ID to be used for check-in
     */
    public void setCheckInQrCodeId(String checkInQrCodeId) {
        this.checkInQrCodeId = checkInQrCodeId;
    }

    /**
     * Retrieves the identifier of the event organizer.
     *
     * @return the organizer's ID
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Sets the identifier for the event organizer.
     *
     * @param organizerId the organizer's unique ID
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * Retrieves the URL of the banner image for the event.
     *
     * @return the banner image URL
     */
    public String getEventBannerImageUrl() {
        return eventBannerImageUrl;
    }

    /**
     * Sets the URL for the event's banner image.
     *
     * @param eventBannerImageUrl the URL of the banner image
     */
    public void setEventBannerImageUrl(String eventBannerImageUrl) {
        this.eventBannerImageUrl = eventBannerImageUrl;
    }

    /**
     * Retrieves the maximum number of attendees allowed for the event.
     *
     * @return the event's maximum capacity
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Sets the maximum capacity for the event.
     *
     * @param maxCapacity the maximum number of attendees as an integer
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Retrieves the current number of attendees registered for the event.
     *
     * @return the current attendee count
     */
    public int getCurrentCapacity() {
        return currentCapacity;
    }

    /**
     * Sets the current number of attendees for the event.
     *
     * @param currentCapacity the current number of attendees as an integer
     */
    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    /**
     * Checks whether the event is currently active.
     *
     * @return {@code true} if the event is active; {@code false} otherwise
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Sets the active status of the event.
     *
     * @param isActive {@code true} to mark the event as active, {@code false} to deactivate
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}