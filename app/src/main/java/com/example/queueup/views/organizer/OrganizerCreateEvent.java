package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

        // back button to navigate back to previous activity
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Unlimited attendee checkBox listener
        unlimitedAttendeeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (unlimitedAttendeeCheckBox.isChecked()) {   // if checkBox is selected then disable attendee limit and erase any previously written values
                    attendeeLimitEditText.setText("");
                    attendeeLimitEditText.setEnabled(false);
                }
                else {
                    attendeeLimitEditText.setEnabled(true);  // Enable attendee limit Edit text field if checkBox is unselected.
                }
            }
        });

        // Set OnClickListener for submit button
        submitButton.setOnClickListener(v -> saveEventToFirestore());
    }

    private void saveEventToFirestore() {
        // Retrieve input data
        String eventName = eventNameEditText.getText().toString().trim();
        String startDateStr = startDateEditText.getText().toString().trim();
        String endDateStr = endDateEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String attendeeLimit = attendeeLimitEditText.getText().toString().trim();
        boolean unlimitedAttendees = unlimitedAttendeeCheckBox.isChecked();

//        // Check for required fields
//        if (eventName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || location.isEmpty()) {
//            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Initialize date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        // Initialize Date variables
        Date startDate;
        Date endDate;

        // Parse the start and end dates
        try {
            startDate = sdf.parse(startDateEditText.getText().toString().trim());
            endDate = sdf.parse(endDateEditText.getText().toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format. Please use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for required fields
        if (eventName.isEmpty() || startDate == null || endDate == null || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }


        // Create a map to store event data
        Map<String, Object> event = new HashMap<>();
        event.put("eventName", eventName);
        event.put("eventStartDate", startDate);
        event.put("eventEndDate", endDate);
        event.put("eventLocation", location);
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
