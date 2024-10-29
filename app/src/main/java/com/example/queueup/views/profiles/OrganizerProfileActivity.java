package com.example.queueup.views.profiles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class OrganizerProfileActivity extends AppCompatActivity {
	private FirebaseFirestore db;
	private String deviceID;
	private User currentUser;
	private static final int EDIT_PROFILE_REQUEST_CODE = 1;
	private TextView organizerProfileInitialsTextView,  organizerProfileNameTextView, organizerProfileUsernameTextView, organizerProfileEmailTextView, organizerProfilePhoneTextView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.organizer_user_profile);

		// Initialize firestore and UI elements
		db = FirebaseFirestore.getInstance();
		organizerProfileInitialsTextView = findViewById(R.id.organizerProfileInitialsTextView);
		organizerProfileNameTextView = findViewById(R.id.organizerProfileNameTextView);
		organizerProfileUsernameTextView = findViewById(R.id.organizerProfileUsernameTextView);
		organizerProfileEmailTextView = findViewById(R.id.organizerProfileEmailTextView);
		organizerProfilePhoneTextView = findViewById(R.id.organizerProfilePhoneTextView);

		// Retrieve deviceID passed from the previous activity
		deviceID = getIntent().getStringExtra("deviceID");
		if (deviceID == null || deviceID.isEmpty()) {
			deviceID = UserController.getInstance().getDeviceId(getApplicationContext());
		}

		Log.d("OrganizerProfile", "Device ID" + deviceID);

		// Back button to return to previous screen
		ImageButton backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(v -> finish());

		//have to implement
		Button inviteButton = findViewById(R.id.inviteButton);

		// Edit button to navigate to EditProfileActivity
		Button editButton = findViewById(R.id.editButton);
		editButton.setOnClickListener(v -> {
			Intent intent = new Intent(OrganizerProfileActivity.this, EditProfileActivity.class);
			intent.putExtra("user", currentUser);
			startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
		});

		// Get user data from the Firestore
		fetchUserData();
	}

	/**
	 * Fetches user data from Firestore and updates the UI with the user's profile information
	 */

	private void fetchUserData() {
		db.collection("users")
				.whereEqualTo("deviceID", deviceID)
				.get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful() && !task.getResult().isEmpty()) {
						for (QueryDocumentSnapshot document : task.getResult()) {
							User user = document.toObject(User.class);

							Log.d("OrganizerProfile", "User retrieved: " + user.getFirstName() + " " + user.getLastName());

							String fullName = user.getFirstName() + " " + user.getLastName();

							organizerProfileNameTextView.setText(fullName);
							organizerProfileUsernameTextView.setText(user.getUsername());
							organizerProfilePhoneTextView.setText(user.getPhoneNumber());
							organizerProfileEmailTextView.setText(user.getEmailAddress());

							String initials = user.getFirstName().substring(0, 1).toUpperCase() + user.getLastName().substring(0,1).toUpperCase();

							Log.d("OrganizerProfile", "User initials: " + initials);

							organizerProfileInitialsTextView.setText(initials);
						}
					} else {

						Log.d("OrganizerProfile","No user found with this device ID");
					}
				})
				.addOnFailureListener(e -> {

					Log.e("OrganizerProfile", "Error fetching user data", e);
				});

	}

	/**
	 * Updates UI with fetched user data.
	 * @param user The user whose data i used to populate organizer profile view.
	 */

	private void updateOrganizerProfile(User user) {
		String fullName = user.getFirstName() + " " + user.getLastName();
		organizerProfileNameTextView.setText(fullName);
		organizerProfileUsernameTextView.setText(user.getUsername());
		organizerProfilePhoneTextView.setText(user.getPhoneNumber());
		organizerProfileEmailTextView.setText(user.getEmailAddress());
		organizerProfileInitialsTextView.setText(user.getInitials());
	}

	/**
	 * Called when returning from EditProfileActivity. checks for updates user data and updates UI.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
			User updatedUser = data.getParcelableExtra("updatedUser");
			if (updatedUser != null) {
				currentUser = updatedUser;
				updateOrganizerProfile(currentUser);
			}
		}
	}
}
