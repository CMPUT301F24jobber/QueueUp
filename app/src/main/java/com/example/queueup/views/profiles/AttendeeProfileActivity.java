package com.example.queueup.views.profiles;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * The AttendeeProfileActivity class represents the profile screen for an attendee.
 * It displays the attendee's full profile information, including name, username, email, and phone number.
 */
public class AttendeeProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String deviceId;
    private TextView profileNameTextView, profileUsernameTextView, profileEmailTextView, profilePhoneTextView, profileInitialsTextView;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_profile);

        // Initialize Firestore and the TextViews
        db = FirebaseFirestore.getInstance();
        profileNameTextView = findViewById(R.id.profileNameTextView);
        profileUsernameTextView = findViewById(R.id.profileUsernameTextView);
        profileEmailTextView = findViewById(R.id.profileEmailTextView);
        profilePhoneTextView = findViewById(R.id.profilePhoneTextView);
        profileInitialsTextView = findViewById(R.id.profileInitialsTextView); // Initials TextView

        // Get the deviceId from the intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());
        }

        // Log the deviceId to ensure it's correct
        Log.d("AttendeeProfile", "Device ID: " + deviceId);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // This will go back to the previous activity

        // Fetch and display the user data
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
                            User user = document.toObject(User.class);

                            // Log the user data to make sure it's being retrieved correctly
                            Log.d("AttendeeProfile", "User retrieved: " + user.getFirstName() + " " + user.getLastName());

                            // Set full name
                            String fullName = user.getFirstName() + " " + user.getLastName();
                            profileNameTextView.setText(fullName);

                            // Set username, email, and phone
                            profileUsernameTextView.setText(user.getUsername());
                            profileEmailTextView.setText(user.getEmailAddress());
                            profilePhoneTextView.setText(user.getPhoneNumber());

                            // Extract and set initials
                            String initials = user.getFirstName().substring(0, 1).toUpperCase()
                                    + user.getLastName().substring(0, 1).toUpperCase();

                            // Log the initials to ensure they are generated correctly
                            Log.d("AttendeeProfile", "User initials: " + initials);

                            // Set the initials in the TextView
                            profileInitialsTextView.setText(initials);  // Display initials in circle
                        }
                    } else {
                        // Log a message if no user is found
                        Log.d("AttendeeProfile", "No user found with this device ID");
                    }
                })
                .addOnFailureListener(e -> {
                    // Log any error that occurs during Firestore retrieval
                    Log.e("AttendeeProfile", "Error fetching user data", e);
                });
    }
}
