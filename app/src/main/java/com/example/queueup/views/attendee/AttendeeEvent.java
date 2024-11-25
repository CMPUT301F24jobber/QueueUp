package com.example.queueup.views.attendee;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.controllers.UserController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.List;


public class AttendeeEvent extends AppCompatActivity {
    private ImageButton backButton;
    AttendeeController attendeeController;
    private Event event;

    /**
     * The current user handler.
     */
    CurrentUserHandler currentUserHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        event = getIntent().getSerializableExtra("event", Event.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_activity);
        TextView eventDate = findViewById(R.id.event_date);
        TextView locationText = findViewById(R.id.event_location);
        TextView timeText = findViewById(R.id.event_time);
        TextView descriptionText = findViewById(R.id.description);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE MMM dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String date_text = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(dateFormatter);
        String time_text = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).format(timeFormatter);
        currentUserHandler = CurrentUserHandler.getSingleton();
        attendeeController = AttendeeController.getInstance();
        eventDate.setText(date_text);
        locationText.setText(event.getEventLocation());

        timeText.setText(time_text);
        descriptionText.setText(event.getEventDescription());

        ImageView posterImage = findViewById(R.id.poster_image);

        Glide.with(this).load(event.getEventBannerImageUrl()).into(posterImage);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });

        if (!event.getAttendeeIds().contains(currentUserHandler.getCurrentUserId())) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.attendee_event_fragment, AttendeeWaitlistFragment.class, bundle)
                    .commit();
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.attendee_event_fragment, AttendeeWaitlistJoinedFragment.class, bundle)
                    .commit();
        }
    }


}