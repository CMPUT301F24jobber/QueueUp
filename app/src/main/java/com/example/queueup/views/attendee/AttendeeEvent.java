package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;

public class AttendeeEvent extends AppCompatActivity {
    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.attendee_event_fragment, AttendeeWaitlistFragment.class, null)
                    .commit();
        }
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });

    }
}