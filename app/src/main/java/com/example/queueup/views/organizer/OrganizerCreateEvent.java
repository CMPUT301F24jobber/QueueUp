package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class OrganizerCreateEvent extends AppCompatActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private EditText eventNameEditText, startDateEditText, endDateEditText, locationEditText, descriptionEditText, attendeeLimitEditText;
    private CheckBox unlimitedAttendeeCheckBox;
    private Button submitButton;
    private EventViewModel eventViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
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

        int attendeeLimitValue = unlimitedAttendees ? Integer.MAX_VALUE : parseAttendeeLimit(attendeeLimit);
        if (attendeeLimitValue == -1) {
            showToast("Invalid attendee limit. Please enter a valid number.");
            return;
        }

        String organizerId = CurrentUserHandler.getSingleton().getCurrentUserId();
        if (organizerId == null || organizerId.isEmpty()) {
            showToast("Organizer ID is missing. Please make sure you are logged in.");
            return;
        }


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
    }


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
