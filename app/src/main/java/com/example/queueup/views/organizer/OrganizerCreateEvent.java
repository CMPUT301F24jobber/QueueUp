package com.example.queueup.views.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.services.ImageUploader;
import com.example.queueup.viewmodels.EventViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class OrganizerCreateEvent extends AppCompatActivity {

    // Constants for date and time formats
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";

    // UI Components
    private ImageView eventImage;
    private EditText eventNameEditText;
    private EditText startDateEditText, startTimeEditText;
    private EditText endDateEditText, endTimeEditText;
    private EditText locationEditText, descriptionEditText, attendeeLimitEditText;
    private CheckBox unlimitedAttendeeCheckBox;
    private Button submitButton;
    private ImageButton backButton;

    // View Model and Data
    private EventViewModel eventViewModel;
    private Uri imageUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize ViewModel and UI components
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        initializeUIComponents();
        setupDateTimeListeners();
        setupClickListeners();
        setupObservers();

        // Initially disable submit button
        submitButton.setEnabled(false);

        // Setup photo picker
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        imageUri = uri;
                        Glide.with(this).load(uri).circleCrop().into(eventImage);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        // Enable submit button when user is authenticated
        CurrentUserHandler.getSingleton().getCurrentUser().observe(this, user -> {
            if (user != null && user.getUuid() != null) {
                submitButton.setEnabled(true);
            }
        });

        // Setup image picker click listener
        eventImage.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    /**
     * Initializes all UI components by finding their views
     */
    private void initializeUIComponents() {
        eventImage = findViewById(R.id.image_view);
        eventNameEditText = findViewById(R.id.eventNameEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        endTimeEditText = findViewById(R.id.endTimeEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        attendeeLimitEditText = findViewById(R.id.attendeeLimitEditText);
        unlimitedAttendeeCheckBox = findViewById(R.id.unlimitedAttendeeCheckBox);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
    }

    /**
     * Sets up click listeners for various UI components
     */
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        unlimitedAttendeeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                toggleAttendeeLimit(isChecked));
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    /**
     * Sets up date and time picker dialogs for both start and end date/time fields
     */
    private void setupDateTimeListeners() {
        Calendar calendar = Calendar.getInstance();

        // Start Date Picker
        startDateEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(),
                                "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        startDateEditText.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // Start Time Picker
        startTimeEditText.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        String selectedTime = String.format(Locale.getDefault(),
                                "%02d:%02d", hourOfDay, minute);
                        startTimeEditText.setText(selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true); // Use 24-hour format
            timePickerDialog.show();
        });

        // End Date Picker
        endDateEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(),
                                "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        endDateEditText.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // End Time Picker
        endTimeEditText.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        String selectedTime = String.format(Locale.getDefault(),
                                "%02d:%02d", hourOfDay, minute);
                        endTimeEditText.setText(selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true); // Use 24-hour format
            timePickerDialog.show();
        });
    }

    /**
     * Sets up observers for the ViewModel to handle loading states and error messages
     */
    private void setupObservers() {
        eventViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
            }
        });

        eventViewModel.getIsLoadingLiveData().observe(this, isLoading -> {
            if (Boolean.FALSE.equals(isLoading)) {
                eventViewModel.getAllEventsLiveData().observe(this, events -> {
                    if (events != null) {
                        // Create a delay for intent so data loads first
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        showToast("Event created successfully");
                        Intent intent = new Intent(OrganizerCreateEvent.this, OrganizerHome.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast("Event creation failed. Please try again.");
                    }
                });
            } else {
                showToast("Creating event...");
            }
        });
    }

    /**
     * Toggles the attendee limit EditText based on checkbox state
     */
    private void toggleAttendeeLimit(boolean isChecked) {
        attendeeLimitEditText.setEnabled(!isChecked);
        if (isChecked) {
            attendeeLimitEditText.setText("");
        }
    }

    /**
     * Handles the submit button click, validates inputs and creates the event
     */
    private synchronized void handleSubmit() {
        // Get all input values
        String eventName = eventNameEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String startTime = startTimeEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String endTime = endTimeEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String attendeeLimit = attendeeLimitEditText.getText().toString().trim();
        boolean unlimitedAttendees = unlimitedAttendeeCheckBox.isChecked();

        // Validate required fields
        if (!areRequiredFieldsFilled(eventName, location, startDate, startTime, endDate, endTime)) {
            showToast("Please fill in all required fields");
            return;
        }

        // Combine date and time strings
        String startDateTimeStr = startDate + " " + startTime;
        String endDateTimeStr = endDate + " " + endTime;

        // Parse and validate dates
        Date startDateTime = parseDateTime(startDateTimeStr);
        Date endDateTime = parseDateTime(endDateTimeStr);

        if (startDateTime == null || endDateTime == null) {
            showToast("Invalid date/time format");
            return;
        }

        if (!endDateTime.after(startDateTime)) {
            showToast("End date/time must be after start date/time");
            return;
        }

        if (new Date().after(startDateTime)) {
            showToast("Start date/time cannot be in the past");
            return;
        }

        // Validate attendee limit
        int attendeeLimitValue = unlimitedAttendees ? Integer.MAX_VALUE : parseAttendeeLimit(attendeeLimit);
        if (attendeeLimitValue <= 0) {
            showToast("Invalid attendee limit. Please enter a valid number.");
            return;
        }

        // Check for required image and organizer ID
        String organizerId = CurrentUserHandler.getSingleton().getCurrentUserId();
        if (imageUri == null) {
            showToast("Image is missing. Please add an image");
            return;
        }

        // Create and upload event
        String qrCodeId = UUID.randomUUID().toString();
        ImageUploader imageUploader = new ImageUploader();

        Event newEvent = new Event(
                UUID.randomUUID().toString(),
                eventName,
                description,
                null, // Image URL will be set after upload
                location,
                organizerId,
                startDateTime,
                endDateTime,
                attendeeLimitValue,
                true // Active by default
        );

        newEvent.setCheckInQrCodeId(qrCodeId);
        eventViewModel.createEvent(newEvent);

        // Upload the event image
        imageUploader.uploadImage("profile_pictures/", imageUri, new ImageUploader.UploadListener() {
            @Override
            public void onUploadSuccess(String imageUrl) {
                eventViewModel.setEventBannerImage(newEvent.getEventId(), imageUrl);
            }

            @Override
            public void onUploadFailure(Exception exception) {
                showToast("Failed to upload image. Event created without image.");
            }
        });
    }

    /**
     * Validates that all required fields are filled
     */
    private boolean areRequiredFieldsFilled(String eventName, String location,
                                            String startDate, String startTime,
                                            String endDate, String endTime) {
        return !eventName.isEmpty() && !location.isEmpty() &&
                !startDate.isEmpty() && !startTime.isEmpty() &&
                !endDate.isEmpty() && !endTime.isEmpty();
    }

    /**
     * Parses a date-time string into a Date object
     */
    private Date parseDateTime(String dateTimeStr) {
        try {
            return new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).parse(dateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parses the attendee limit string into an integer
     */
    private int parseAttendeeLimit(String attendeeLimit) {
        try {
            return attendeeLimit.isEmpty() ? -1 : Integer.parseInt(attendeeLimit);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Shows a toast message
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}