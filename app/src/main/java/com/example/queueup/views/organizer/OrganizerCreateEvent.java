package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Date;

public class OrganizerCreateEvent extends AppCompatActivity {

	private FirebaseFirestore db;
	private EditText  eventNameEditText, startDateEditText, endDateEditText, locationEditText, descriptionEditText, attendeeLimitEditText;
	private CheckBox unlimitedAttendeeCheckBox;
	private Button submitButton;


	private EventViewModel eventViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.organizer_create_event);

		// Initialize views
		eventNameEditText = findViewById(R.id.eventNameEditText);
		startDateEditText = findViewById(R.id.startDateEditText);
		endDateEditText = findViewById(R.id.endDateEditText);
		locationEditText = findViewById(R.id.locationEditText);
		descriptionEditText = findViewById(R.id.descriptionEditText);
		attendeeLimitEditText = findViewById(R.id.attendeeLimitEditText);
		unlimitedAttendeeCheckBox = findViewById(R.id.unlimitedAttendeeCheckBox);


		// Initialize ViewModel
		eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

		// back button to navigate back to previous activity
		ImageButton backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(v -> finish());

		// submit button click listener
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submitEvent();
			}

		});
	}

	/**
	 * save event to firestore.
	 */
	private void submitEvent() {
		String name = eventNameEditText.getText().toString().trim();
		String startDate = startDateEditText.getText().toString().trim();
		String endDate = endDateEditText.getText().toString().trim();
		String location = locationEditText.getText().toString().trim();
		String description = descriptionEditText.getText().toString().trim();
		boolean unlimitedAttendee = unlimitedAttendeeCheckBox.isChecked();

		if (name.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || location.isEmpty() || description.isEmpty()) {
			Toast.makeText(OrganizerCreateEvent.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
			return;
		}


		Event event = new Event(id, name, description, "", 0.0, 0.0, startDate, endDate);

		//save event to firestore using EventViewModel
		eventViewModel.createEvent(event).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Toast.makeText(OrganizerCreateEvent.this, "Event created successfully", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(OrganizerCreateEvent.this, "Failed to create even:t" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}
}
