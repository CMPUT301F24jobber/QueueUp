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

    private static final String TAG = "OrganizerCreateEvent";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";

    private ImageView eventImage;
    private EditText eventNameEditText;
    private EditText startDateEditText, startTimeEditText;
    private EditText endDateEditText, endTimeEditText;
    private EditText locationEditText, descriptionEditText, attendeeLimitEditText;
    private CheckBox unlimitedAttendeeCheckBox;
    private Button submitButton;
    private ImageButton backButton;
    private CheckBox isGeolocationRequired;
    private EventViewModel eventViewModel;
    private Uri imageUri = null;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize ViewModel
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Initialize UI and setup listeners
        initializeUIComponents();
        setupPhotoPickerLauncher();
        setupDateTimeListeners();
        setupClickListeners();
        setupObservers();

        // Initially disable submit button until user is authenticated
        submitButton.setEnabled(false);

        // Enable submit button when user is authenticated
        CurrentUserHandler.getSingleton().getCurrentUser().observe(this, user -> {
            submitButton.setEnabled(user != null);
        });
    }

    private void setupPhotoPickerLauncher() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d(TAG, "Selected URI: " + uri);
                imageUri = uri;
                Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .into(eventImage);
            } else {
                Log.d(TAG, "No media selected");
            }
        });
    }

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
        isGeolocationRequired = findViewById(R.id.geolocationRequiredCheckBox);
    }

    private void setupClickListeners() {
        eventImage.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        backButton.setOnClickListener(v -> finish());

        unlimitedAttendeeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                toggleAttendeeLimit(isChecked));

        submitButton.setOnClickListener(v -> handleSubmit());
    }

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
                    true);
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
                    true);
            timePickerDialog.show();
        });
    }

    private void setupObservers() {
        // Observe error messages
        eventViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showToast(errorMessage);
            }
        });

        // Observe loading state and event creation success
        eventViewModel.getIsLoadingLiveData().observe(this, isLoading -> {
            if (Boolean.FALSE.equals(isLoading)) {
                eventViewModel.getAllEventsLiveData().observe(this, events -> {
                    if (events != null) {
                        navigateToHome();
                    } else {
                        showToast("Event creation failed. Please try again.");
                        submitButton.setEnabled(true);
                    }
                });
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(OrganizerCreateEvent.this, OrganizerHome.class);
        startActivity(intent);
        finish();
    }

    private void toggleAttendeeLimit(boolean isUnlimited) {
        attendeeLimitEditText.setEnabled(!isUnlimited);
        if (isUnlimited) {
            attendeeLimitEditText.setText("");
        }
    }

    private void handleSubmit() {
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
        boolean geolocationRequired = isGeolocationRequired.isChecked();

        // Validate required fields
        if (!areRequiredFieldsFilled(eventName, location, startDate, startTime, endDate, endTime)) {
            showToast("Please fill in all required fields");
            return;
        }
        if (description == "test") {
            imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/queueup-b3c43.appspot.com/o/event_images%2F56adea3b-fe6b-43ce-aa8f-15e9d62b5d13%2F75c9519b-9fe3-4f37-a2c0-6a7c230877ba?alt=media&token=aea43782-c87c-4a46-babd-46e11e982842");
        }
        // Check for image
        if (imageUri == null) {
            showToast("Please select an event image");
            return;
        }

        // Validate dates
        String startDateTimeStr = startDate + " " + startTime;
        String endDateTimeStr = endDate + " " + endTime;
        Date startDateTime = parseDateTime(startDateTimeStr);
        Date endDateTime = parseDateTime(endDateTimeStr);

        if (!areDatesValid(startDateTime, endDateTime)) {
            return;
        }

        // Validate attendee limit
        int attendeeLimitValue = unlimitedAttendees ? Integer.MAX_VALUE : parseAttendeeLimit(attendeeLimit);
        if (attendeeLimitValue <= 0) {
            showToast("Invalid attendee limit. Please enter a valid number.");
            return;
        }

        // Disable submit button to prevent double submission
        submitButton.setEnabled(false);

        // First upload the image
        String eventId = UUID.randomUUID().toString();
        String qrCodeId = UUID.randomUUID().toString();
        ImageUploader imageUploader = new ImageUploader();

        imageUploader.uploadImage("event_images/" + eventId + "/", imageUri, new ImageUploader.UploadListener() {
            @Override
            public void onUploadSuccess(String imageUrl) {
                // Create event object with the uploaded image URL
                Event newEvent = new Event(
                        eventId,
                        eventName,
                        description,
                        imageUrl,
                        location,
                        CurrentUserHandler.getSingleton().getCurrentUserId(),
                        startDateTime,
                        endDateTime,
                        attendeeLimitValue,
                        true,
                        false,
                        geolocationRequired
                );

                newEvent.setCheckInQrCodeId(qrCodeId);

                // Create the event in database
                eventViewModel.createEvent(newEvent);
                showToast("Creating event...");
            }

            @Override
            public void onUploadFailure(Exception exception) {
                showToast("Failed to upload image. Please try again.");
                submitButton.setEnabled(true);
                Log.e(TAG, "Image upload failed", exception);
            }
        });
    }

    private boolean areRequiredFieldsFilled(String eventName, String location,
                                            String startDate, String startTime,
                                            String endDate, String endTime) {
        return !eventName.isEmpty() && !location.isEmpty() &&
                !startDate.isEmpty() && !startTime.isEmpty() &&
                !endDate.isEmpty() && !endTime.isEmpty();
    }

    private boolean areDatesValid(Date startDateTime, Date endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            showToast("Invalid date/time format");
            return false;
        }

        if (!endDateTime.after(startDateTime)) {
            showToast("End date/time must be after start date/time");
            return false;
        }

        if (new Date().after(startDateTime)) {
            showToast("Start date/time cannot be in the past");
            return false;
        }

        return true;
    }

    private Date parseDateTime(String dateTimeStr) {
        try {
            return new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).parse(dateTimeStr);
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error", e);
            return null;
        }
    }

    private int parseAttendeeLimit(String attendeeLimit) {
        try {
            return attendeeLimit.isEmpty() ? -1 : Integer.parseInt(attendeeLimit);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Number parsing error", e);
            return -1;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}