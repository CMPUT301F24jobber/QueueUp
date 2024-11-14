package com.example.queueup.views.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.views.profiles.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * OrganizerHome activity represents the home screen of an organizer, providing access to different functionalities
 * such as managing events, viewing QR codes, and accessing the user's profile.
 * The activity includes a bottom navigation view for switching between different sections.
 */
public class OrganizerHome extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView titleTextView;
    private String deviceId;
    private BottomNavigationView navigationView;
    private ImageButton plusButton;

    /**
     * Called when the activity is first created. This method initializes the views, sets up the bottom navigation
     * menu, and sets up the listener for navigating to the event creation screen.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity);

        // Initialize views
        navigationView = findViewById(R.id.bottom_navigation);
        db = FirebaseFirestore.getInstance();
        plusButton = findViewById(R.id.plusButton);  // Add the plusButton from layout

        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());  // Fetch from UserController if not passed
        }

        // Set the OnClickListener to navigate to OrganizerCreateEvent
        plusButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerHome.this, OrganizerCreateEvent.class);
            startActivity(intent);
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.organizer_activity_fragment, OrganizerHomeFragment.class, null)
                    .commit();
        }

        // Set navigation view item selected listener
        navigationView.setOnItemSelectedListener(menuItem -> {
            String title = String.valueOf(menuItem.getTitle());
            switch (title) {
                case "Home":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.organizer_activity_fragment, OrganizerHomeFragment.class, null)
                            .commit();
                    break;
                case "QR Code":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.organizer_activity_fragment, OrganizerQRCodesFragment.class, null)
                            .commit();
                    break;
                case "Profile":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.organizer_activity_fragment, ProfileFragment.class, null) // Changed to ProfileFragment
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });

    }


}