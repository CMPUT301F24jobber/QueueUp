package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.views.attendee.AttendeeWaitlistFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrganizerEvent extends AppCompatActivity {
    private ImageButton backButton;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_activity);
        navigationView = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.organizer_event_fragment, OrganizerWaitingListFragment.class, null)
                    .commit();
        }
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });
        navigationView.setOnItemSelectedListener(menuItem -> {
            String title = String.valueOf(menuItem.getTitle());
            switch (title) {
                case "Waiting List":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.organizer_event_fragment, OrganizerWaitingListFragment.class, null)
                            .commit();
                    break;
                case "Draw Winner":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.organizer_event_fragment, OrganizerDrawFragment.class, null)
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });
    }
}