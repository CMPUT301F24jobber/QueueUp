package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrganizerCreateEvent extends AppCompatActivity {

    private EditText eventNameEditText, startDateEditText, endDateEditText, locationEditText, descriptionEditText, attendeeLimitEditText;
    private CheckBox unlimitedAttendeeCheckBox;
    private Button submitButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        eventNameEditText = findViewById(R.id.eventNameEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        attendeeLimitEditText = findViewById(R.id.attendeeLimitEditText);
        unlimitedAttendeeCheckBox = findViewById(R.id.unlimitedAttendeeCheckBox);
        submitButton = findViewById(R.id.submitButton);

        // Set OnClickListener for submit button
        submitButton.setOnClickListener(v -> saveEventToFirestore());
    }

    private void saveEventToFirestore() {
        // Retrieve input data
        String eventName = eventNameEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String attendeeLimit = attendeeLimitEditText.getText().toString().trim();
        boolean unlimitedAttendees = unlimitedAttendeeCheckBox.isChecked();

        // Check for required fields
        if (eventName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to store event data
        Map<String, Object> event = new HashMap<>();
        event.put("name", eventName);
        event.put("startDate", startDate);
        event.put("endDate", endDate);
        event.put("location", location);
        event.put("description", description);
        event.put("unlimitedAttendees", unlimitedAttendees);

        // If unlimited attendees is unchecked, store the attendee limit
        if (!unlimitedAttendees) {
            event.put("attendeeLimit", attendeeLimit.isEmpty() ? "0" : attendeeLimit);
        }

        // Save to Firestore
        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(OrganizerCreateEvent.this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity after successful submission
                })
                .addOnFailureListener(e ->
                        Toast.makeText(OrganizerCreateEvent.this, "Failed to create event. Please try again.", Toast.LENGTH_SHORT).show()
                );
    }
}
