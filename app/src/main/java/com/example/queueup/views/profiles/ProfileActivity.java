package com.example.queueup.views.profiles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String deviceId;
    private User currentUser;

    private TextView profileNameTextView, profileUsernameTextView, profileEmailTextView, profilePhoneTextView, profileInitialsTextView;
    private static final int EDIT_PROFILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        // Initialize Firestore and UI elements
        db = FirebaseFirestore.getInstance();
        profileNameTextView = findViewById(R.id.profileNameTextView);
        profileUsernameTextView = findViewById(R.id.profileUsernameTextView);
        profileEmailTextView = findViewById(R.id.profileEmailTextView);
        profilePhoneTextView = findViewById(R.id.profilePhoneTextView);
        profileInitialsTextView = findViewById(R.id.profileInitialsTextView);

        // Retrieve the deviceId passed from the previous activity (or fetch from a controller)
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = "defaultDeviceId"; // Replace with logic to fetch the device ID if not passed
        }

        // Set up the back button to return to the previous screen
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up the edit button to navigate to EditProfileActivity
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("user", currentUser);  // Pass currentUser to EditProfileActivity
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);  // Use a request code to identify the result
        });

        // Fetch user data from Firestore
        fetchUserData();
    }

    /**
     * Fetches user data from Firestore and updates the UI with the user's profile information.
     */
    private void fetchUserData() {
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            currentUser = document.toObject(User.class);  // Assign the fetched data to currentUser
                            updateProfileUI(currentUser);  // Update the UI with user details
                        }
                    } else {
                        Log.d("AttendeeProfile", "No user found with this device ID");
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AttendeeProfile", "Error fetching user data", e);
                    Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates the UI with the fetched user data.
     *
     * @param user The user whose data is used to populate the profile view.
     */
    private void updateProfileUI(User user) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        profileNameTextView.setText(fullName);
        profileUsernameTextView.setText(user.getUsername());
        profileEmailTextView.setText(user.getEmailAddress());
        profilePhoneTextView.setText(user.getPhoneNumber());
        profileInitialsTextView.setText(user.getInitials());
    }

    /**
     * Called when returning from EditProfileActivity. Checks for updated user data and updates the UI if changes were made.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the updated user object from the result
            User updatedUser = data.getParcelableExtra("updatedUser");
            if (updatedUser != null) {
                currentUser = updatedUser;  // Update the current user reference
                updateProfileUI(currentUser);  // Refresh the UI with updated data
            }
        }
    }
}
