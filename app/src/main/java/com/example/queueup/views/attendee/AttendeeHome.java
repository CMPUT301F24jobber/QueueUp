package com.example.queueup.views.attendee;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.views.profiles.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendeeHome extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_home_activity);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load the default HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.attendee_activity_fragment, new AttendeeHomeFragment())
                    .commit();
        }

        // Set up the BottomNavigationView listener
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            Fragment selectedFragment = null;

            if (menuItem.getItemId() == R.id.nav_home) {
                selectedFragment = new AttendeeHomeFragment();
            } else if (menuItem.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();  // Launch ProfileFragment
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.attendee_activity_fragment, selectedFragment)
                        .commit();
            }

            return true;
        });

    }
}
