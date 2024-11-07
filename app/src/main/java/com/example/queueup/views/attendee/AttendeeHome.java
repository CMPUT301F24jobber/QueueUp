package com.example.queueup.views.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.views.admin.AdminHomeFragment;
import com.example.queueup.views.profiles.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The AttendeeHome class represents the home screen for attendees.
 * Displays a welcome message, allows navigation to the attendee's profile, and fetches user data.
 */
public class AttendeeHome extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView titleTextView;
    private TextView profileInitialsTextView;  // TextView to hold the initials
    private FrameLayout profileInitialsFrame;  // FrameLayout for the initials circle
    private String deviceId;
    private ImageView profileImageView;  // ImageView to display the profile picture
    private BottomNavigationView navigationView;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_activity);

        // Initialize Firebase and TextView
        db = FirebaseFirestore.getInstance();
        navigationView = findViewById(R.id.bottom_navigation);


        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());  // Fetch from UserController if not passed
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.attendee_activity_fragment, AttendeeHomeFragment.class, null)
                    .commit();
        }
        navigationView.setOnItemSelectedListener( menuItem -> {
            String title = String.valueOf(menuItem.getTitle());
            switch (title) {
                case "Home":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.attendee_activity_fragment, AttendeeHomeFragment.class, null)
                            .addToBackStack("Home")

                            .commit();
                    break;
                case "Camera":
                    Intent intent = new Intent(AttendeeHome.this, AttendeeQRscanFragment.class);
                    startActivity(intent);
                    break;
                case "Profile":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.attendee_activity_fragment, ProfileFragment.class, null)
                            .addToBackStack("Profile")

                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });
    }
}