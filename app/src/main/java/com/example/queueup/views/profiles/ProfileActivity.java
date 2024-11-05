package com.example.queueup.views.profiles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * The AttendeeProfileActivity class represents the profile screen for an attendee.
 * It displays the attendee's full profile information, including name, username, email, and phone number.
 */
public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String deviceId;
    private TextView profileNameTextView, profileUsernameTextView, profileEmailTextView, profilePhoneTextView, profileInitialsTextView;
    private ImageView profileImageView;
<<<<<<<< HEAD:app/src/main/java/com/example/queueup/views/profiles/AttendeeProfileActivity.java

========
    private User currentUser;
    private static final int EDIT_PROFILE_REQUEST_CODE = 1;
>>>>>>>> profilePicUpload:app/src/main/java/com/example/queueup/views/profiles/ProfileActivity.java
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firestore and UI elements
        db = FirebaseFirestore.getInstance();
        initializeUI();

        // Get the deviceId from the intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());
        }

        // Log the deviceId to ensure it's correct
        Log.d("AttendeeProfile", "Device ID: " + deviceId);

<<<<<<<< HEAD:app/src/main/java/com/example/queueup/views/profiles/AttendeeProfileActivity.java
        // Fetch and display the user data
========
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // This will go back to the previous activity

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("user", currentUser);  // Pass currentUser to EditProfileActivity
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);  // Use a request code to identify the result
        });

        onResume();
    }

    @Override
    protected void onResume() { // Resource used: https://stackoverflow.com/questions/15658687/how-to-use-onresume
        super.onResume();
>>>>>>>> profilePicUpload:app/src/main/java/com/example/queueup/views/profiles/ProfileActivity.java
        fetchUserData();
    }

    /**
     * Initializes UI elements like TextViews, ImageView, and Button.
     */
    private void initializeUI() {
        profileNameTextView = findViewById(R.id.profileNameTextView);
        profileUsernameTextView = findViewById(R.id.profileUsernameTextView);
        profileEmailTextView = findViewById(R.id.profileEmailTextView);
        profilePhoneTextView = findViewById(R.id.profilePhoneTextView);
        profileInitialsTextView = findViewById(R.id.profileInitialsTextView);
        profileImageView = findViewById(R.id.profileImageView);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Go back to the previous activity
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
                            currentUser = document.toObject(User.class); // Assign user to currentUser
                            User user = document.toObject(User.class);
                            updateUIWithUserData(user);
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

    /**
     * Updates the UI with the user's profile information.
     *
     * @param user The User object containing profile information.
     */
    private void updateUIWithUserData(User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String profileImageUrl = user.getProfileImageUrl();

        // Log the user data to ensure it is being retrieved correctly
        Log.d("AttendeeProfile", "User retrieved: " + firstName + " " + lastName);

        // Set full name
        String fullName = firstName + " " + lastName;
        profileNameTextView.setText(fullName);

        // Set username, email, and phone
        profileUsernameTextView.setText(user.getUsername());
        profileEmailTextView.setText(user.getEmailAddress());
        profilePhoneTextView.setText(user.getPhoneNumber());

        // Set profile picture or initials
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(profileImageUrl).circleCrop().into(profileImageView);
        } else {
            profileImageView.setVisibility(View.GONE);
            profileInitialsTextView.setVisibility(View.VISIBLE);
            setInitials(firstName, lastName);
        }
    }

    /**
     * Sets the initials TextView based on the user's first and last name.
     *
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     */
    private void setInitials(String firstName, String lastName) {
        if (firstName != null && lastName != null) {
            String initials = firstName.substring(0, 1).toUpperCase() + lastName.substring(0, 1).toUpperCase();
            profileInitialsTextView.setText(initials);
        } else if (firstName != null) {
            profileInitialsTextView.setText(firstName.substring(0, 1).toUpperCase());
        }
    }
}