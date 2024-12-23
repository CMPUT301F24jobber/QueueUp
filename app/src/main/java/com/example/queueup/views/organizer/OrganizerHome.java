package com.example.queueup.views.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.views.profiles.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;


public class OrganizerHome extends AppCompatActivity {

    private String deviceId;
    private BottomNavigationView navigationView;
    private ImageButton plusButton, backButton;

    /**
     * Called when the activity is created. Initializes the UI elements and sets up the navigation bar.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity);
        CurrentUserHandler.getSingleton().getCurrentUser();

        // Initialize views
        navigationView = findViewById(R.id.bottom_navigation);
        plusButton = findViewById(R.id.plusButton);
        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());
        }
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });
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
                            .addToBackStack("Home")
                            .commit();
                    break;
                case "QR Code":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .addToBackStack("QR Code")
                            .replace(R.id.organizer_activity_fragment, OrganizerQRCodesFragment.class, null)
                            .commit();
                    break;
                case "Profile":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .addToBackStack("Profile")
                            .replace(R.id.organizer_activity_fragment, OrganizerFacilityFragment.class, null)
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });

    }


}