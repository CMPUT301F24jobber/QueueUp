package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * OrganizerEvent activity displays the details of an event and allows the organizer to manage different aspects of the event.
 * The activity includes a bottom navigation menu to switch between the "Waiting List" and "Draw Winner" fragments.
 */
public class OrganizerEvent extends AppCompatActivity {
    private ImageButton backButton;
    private BottomNavigationView navigationView;
    Event event;

    /**
     * Called when the activity is first created. This method sets up the bottom navigation view and initializes
     * the fragments for managing the event (Waiting List and Draw Winner).
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_activity);
        navigationView = findViewById(R.id.bottom_navigation);
        event = getIntent().getSerializableExtra("event", Event.class);
        Log.d("ewio", event.getEventId());
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.organizer_event_fragment, OrganizerWaitingListFragment.class, bundle)
                    .commit();
        }
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });
        navigationView.setOnItemSelectedListener(menuItem -> {
            String title = String.valueOf(menuItem.getTitle());
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            switch (title) {
                case "Waiting List":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.organizer_event_fragment, OrganizerWaitingListFragment.class, bundle)
                            .commit();
                    break;

                case "Draw Winner":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.organizer_event_fragment, OrganizerDraw.class, bundle)
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });
    }
}