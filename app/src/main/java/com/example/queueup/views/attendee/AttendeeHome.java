package com.example.queueup.views.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.example.queueup.views.profiles.AttendeeProfileActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * The AttendeeHome class represents the home screen for attendees
 * Displays a welcome message, allows navigation to the attendee's profile, and fetches user data.
 */
public class AttendeeHome extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView titleTextView;
    private TextView profileInitialsTextView;  // TextView to hold the initials
    private FrameLayout profileInitialsFrame;  // FrameLayout for the initials circle
    private String deviceId;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_home_fragment);

        // Initialize Firestore and TextView
        db = FirebaseFirestore.getInstance();
        titleTextView = findViewById(R.id.titleTextView);
        profileInitialsTextView = findViewById(R.id.profileInitialsTextView);  // Find the initials TextView
        profileInitialsFrame = findViewById(R.id.profileInitialsFrame);  // Find the FrameLayout

        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());  // Fetch from UserController if not passed
        }

        // Set up listener for profile initials (similar to a button)
        profileInitialsFrame.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeHome.this, AttendeeProfileActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivity(intent);
        });

        // Fetch user data
        fetchUserData();
    }

    /**
     * Fetches user data from Firestore and updates the UI with the user's first name and initials.
     */
    private void fetchUserData() {
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            String firstName = user.getFirstName();
                            String lastName = user.getLastName();

                            // Set the welcome text
                            if (firstName != null && !firstName.isEmpty()) {
                                titleTextView.setText("Welcome, " + firstName + "!");
                            } else {
                                titleTextView.setText("Welcome, Attendee!");
                            }

                            // Set the initials in the TextView
                            if (firstName != null && lastName != null) {
                                String initials = firstName.substring(0, 1) + lastName.substring(0, 1);
                                profileInitialsTextView.setText(initials);  // Set the extracted initials
                            } else if (firstName != null) {
                                profileInitialsTextView.setText(firstName.substring(0, 1));  // Only first name available
                            }
                        }
                    } else {
                        Log.d("AttendeeHome", "No user found with this device ID");
                        titleTextView.setText("Welcome, Attendee!");  // if no user found
                    }
                })
                .addOnFailureListener(e -> Log.e("AttendeeHome", "Error fetching user data", e));
    }
}