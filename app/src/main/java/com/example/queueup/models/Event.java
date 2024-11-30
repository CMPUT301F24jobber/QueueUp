package com.example.queueup.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Event implements Serializable {
    private String eventName;
    private String eventLocation;
    private String eventDescription;
    private String organizerId;
    private String eventBannerImageUrl;
    private String eventId;
    private ArrayList<String> attendeeIds;
    private Date eventStartDate;
    private Date eventEndDate;
    private Date creationDate;
    private String checkInQrCodeId;
    private Boolean isActive;
    private Boolean isDrawn;
    private Boolean redrawEnabled;
    private Boolean redrawNotificationEnabled;

    private Boolean isGeoLocationRequried;


    private int maxCapacity;
    private int currentCapacity;

    public Event() {
    }
    public Event(String eventId, String eventName, String eventDescription, String eventBannerImageUrl,
                 String eventLocation, String organizerId, Date eventStartDate, Date eventEndDate,
                 int maxCapacity, Boolean isActive, Boolean isDrawn, Boolean isGeoLocationRequried) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventBannerImageUrl = eventBannerImageUrl;
        this.eventLocation = eventLocation;
        this.organizerId = organizerId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.maxCapacity = maxCapacity;
        this.isActive = isActive;
        this.attendeeIds = new ArrayList<>();
        this.currentCapacity = 0;
        this.creationDate = new Date();
        this.isDrawn = isDrawn;
        this.isGeoLocationRequried = isGeoLocationRequried;
        this.redrawEnabled = false;
        this.redrawNotificationEnabled = false;
    }

    public Boolean getIsGeoLocationRequried() {
        return isGeoLocationRequried;
    }

    public void setIsGeoLocationRequried(Boolean isGeoLocationRequried) {
        this.isGeoLocationRequried = isGeoLocationRequried;
    }

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
     * @param eventName t
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Get the location of the event.
     *
     * @return the event's location
     */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Sets the location for the event.
     *
     * @param eventLocation
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
     * @param eventDescription
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
     * @param eventId
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
     * @param eventStartDate
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
     * @param eventEndDate
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
     * @param creationDate
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the QR code
     *
     * @return the QR code ID for event check-in
     */
    public String getCheckInQrCodeId() {
        return checkInQrCodeId;
    }

    /**
     * Sets the QR code
     *
     * @param checkInQrCodeId
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
     * @param organizerId
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
     * @param eventBannerImageUrl
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
     * @param maxCapacity
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
     * @param currentCapacity
     */
    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    /**
     * Checks whether the event is currently active.
     *
     * @return bool
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Sets the active status of the event.
     *
     * @param isActive
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Checks whether the event has been drawn.
     *
     * @return bool
     */
    public Boolean getIsDrawn() {
        return isDrawn;
    }

    /**
     * Set drawn status of event.
     *
     * @param isDrawn {@code true} to mark the event as active, {@code false} to deactivate
     */
    public void setIsDrawn(Boolean isDrawn) {
        this.isDrawn = isDrawn;
    }
    /**
     * Checks if attendees are being redrawn when one cancels.
     *
     * @return bool
     */
    public Boolean getRedrawEnabled() {
        return redrawEnabled;
    }

    /**
     * Sets if attendees are being redrawn when one cancels.
     *
     * @param redrawEnabled
     */
    public void setRedrawEnabled(Boolean redrawEnabled) {
        this.redrawEnabled = redrawEnabled;
    }
    /**
     * Checks if attendees are notified when winning though redraw.
     *
     * @return bool
     */
    public Boolean getRedrawNotificationEnabled() {
        return redrawNotificationEnabled;
    }

    /**
     * Sets if attendees are notified when winning through redraw.
     *
     * @param redrawNotificationEnabled
     */
    public void setRedrawNotificationEnabled(Boolean redrawNotificationEnabled) {
        this.redrawNotificationEnabled = redrawNotificationEnabled;
    }
}