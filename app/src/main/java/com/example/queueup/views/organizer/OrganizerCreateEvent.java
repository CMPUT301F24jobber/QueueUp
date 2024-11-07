package com.example.queueup.views.organizer;

import static androidx.core.app.ActivityCompat.startActivityForResult;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.queueup.views.SignUp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class OrganizerCreateEvent extends AppCompatActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private ImageView eventImage;
    private EditText eventNameEditText, startDateEditText, endDateEditText, locationEditText, descriptionEditText, attendeeLimitEditText;
    private CheckBox unlimitedAttendeeCheckBox;
    private Button submitButton;
    private EventViewModel eventViewModel;
    private Uri imageUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        eventImage = findViewById(R.id.image_view);
        initializeUIComponents();
        setupObservers();

        // Initially disable the submit button
        submitButton.setEnabled(false);

        // Observe current user to get organizer ID
        CurrentUserHandler.getSingleton().getCurrentUser().observe(this, user -> {
            if (user != null && user.getUuid() != null) {
                // Organizer ID is available, enable submit button
                submitButton.setEnabled(true);
            } else {
                showToast("Organizer ID is missing. Please make sure you are logged in.");
            }
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        unlimitedAttendeeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> toggleAttendeeLimit(isChecked));
        submitButton.setOnClickListener(v -> handleSubmit());
        eventImage.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

    }

    private void initializeUIComponents() {
        eventNameEditText = findViewById(R.id.eventNameEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        attendeeLimitEditText = findViewById(R.id.attendeeLimitEditText);
        unlimitedAttendeeCheckBox = findViewById(R.id.unlimitedAttendeeCheckBox);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupObservers() {
        eventViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showToast(errorMessage);
            }
        });

        eventViewModel.getIsLoadingLiveData().observe(this, isLoading -> {
            if (Boolean.TRUE.equals(isLoading)) {
                showToast("Loading...");
            }
        });

        eventViewModel.getAllEventsLiveData().observe(this, events -> {
            if (events != null) {
                showToast("Event created successfully");
                finish();
            } else {
                showToast("Event creation failed. Please try again.");
            }
        });
    }

    private void toggleAttendeeLimit(boolean isChecked) {
        attendeeLimitEditText.setEnabled(!isChecked);
        if (isChecked) {
            attendeeLimitEditText.setText("");
        }
    }

    private synchronized void handleSubmit() {
        String eventName = eventNameEditText.getText().toString().trim();
        String startDateStr = startDateEditText.getText().toString().trim();
        String endDateStr = endDateEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String attendeeLimit = attendeeLimitEditText.getText().toString().trim();
        boolean unlimitedAttendees = unlimitedAttendeeCheckBox.isChecked();

        if (!areRequiredFieldsFilled(eventName, location)) {
            showToast("Please fill in all required fields");
            return;
        }

        Date startDate = parseDate(startDateStr);
        Date endDate = parseDate(endDateStr);
        if (startDate == null || endDate == null) {
            showToast("Invalid date format. Please use yyyy-MM-dd HH:mm");
            return;
        }

        if (!endDate.after(startDate)) {
            showToast("End Date has to be after Start Date");
            return;
        }
        if (new Date().after(startDate)) {
            showToast("Start Date cannot be in the past");
            return;
        }

        int attendeeLimitValue = unlimitedAttendees ? Integer.MAX_VALUE : parseAttendeeLimit(attendeeLimit);
        if (attendeeLimitValue <= 0) {
            showToast("Invalid attendee limit. Please enter a valid number.");
            return;
        }

        String organizerId = CurrentUserHandler.getSingleton().getCurrentUserId();
        if (organizerId == null || organizerId.isEmpty()) {
            showToast("Organizer ID is missing. Please make sure you are logged in.");
            return;
        }
        if (imageUri == null) {
            showToast("Image is missing. Please add an image");
            return;
        }
        ImageUploader imageUploader = new ImageUploader();


        Event newEvent = new Event(
                UUID.randomUUID().toString(),
                eventName,
                description,
                null, // eventBannerImageUrl can be null for now.
                location,
                organizerId,
                startDate,
                endDate,
                attendeeLimitValue,
                true // Setting the event as active by default.
        );
        eventViewModel.createEvent(newEvent);
        imageUploader.uploadImage("profile_pictures/", imageUri, new ImageUploader.UploadListener() {
            @Override
            public void onUploadSuccess(String imageUrl) {
                eventViewModel.setEventBannerImage(newEvent.getEventId(), imageUrl);
            }

            @Override
            public void onUploadFailure(Exception exception) {
            }
        });
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    imageUri = uri;
                    Glide.with(this).load(uri).circleCrop().into(eventImage);

                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    private boolean areRequiredFieldsFilled(String eventName, String location) {
        return !eventName.isEmpty() && !location.isEmpty();
    }

    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int parseAttendeeLimit(String attendeeLimit) {
        try {
            return attendeeLimit.isEmpty() ? -1 : Integer.parseInt(attendeeLimit);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

