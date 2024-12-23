package com.example.queueup.views.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.views.profiles.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;


public class AttendeeHome extends AppCompatActivity {
    private String deviceId;
    private BottomNavigationView navigationView;

    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_activity);

        // Initialize Firebase and TextView
        navigationView = findViewById(R.id.bottom_navigation);
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });

        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());
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
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.attendee_activity_fragment, AttendeeQRscanFragment.class, null)
                            .addToBackStack("QR")
                            .commit();
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