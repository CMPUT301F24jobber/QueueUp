package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.Event;

import org.checkerframework.checker.units.qual.A;

import java.time.format.DateTimeFormatter;


public class AttendeeEvent extends AppCompatActivity {
    private ImageButton backButton;
    AttendeeController attendeeController;
    private Event event;
    TextView titleText;
    private Attendee attendee;

    /**
     * The current user handler.
     */
    CurrentUserHandler currentUserHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        event = getIntent().getSerializableExtra("event", Event.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_activity);
        titleText = findViewById(R.id.win_lose_text);
        TextView eventDate = findViewById(R.id.event_date);
        TextView locationText = findViewById(R.id.event_location);
        TextView timeText = findViewById(R.id.event_time);
        TextView descriptionText = findViewById(R.id.description);
        TextView attendeeText = findViewById(R.id.attendees_text);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE MMM dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String date_text = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(dateFormatter);
        String time_text = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).format(timeFormatter);
        currentUserHandler = CurrentUserHandler.getSingleton();
        attendeeController = AttendeeController.getInstance();
        titleText.setText(event.getEventName());
        eventDate.setText(date_text);
        locationText.setText(event.getEventLocation());
        attendeeText.setText("Attendees: " + event.getAttendeeIds().size() + "/" + (event.getMaxCapacity() == Integer.MAX_VALUE? "Unlimited" : event.getMaxCapacity()));

        timeText.setText(time_text);
        descriptionText.setText(event.getEventDescription());

        ImageView posterImage = findViewById(R.id.poster_image);

        Glide.with(this).load(event.getEventBannerImageUrl()).into(posterImage);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });
        navigateToFragment();

    }
    private void navigateToFragment() {
        if (!event.getAttendeeIds().contains(currentUserHandler.getCurrentUserId()+event.getEventId())) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.attendee_event_fragment, AttendeeWaitlistFragment.class, bundle)
                    .commit();
        } else if (event.getIsDrawn()) {
            attendeeController.getAttendanceById(currentUserHandler.getCurrentUserId()+event.getEventId())
                    .addOnSuccessListener(querySnapshot -> {
                        attendee = querySnapshot.toObject(Attendee.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                bundle.putSerializable("attendee", attendee);

                switch (attendee.getStatus()) {
                    case "enrolled":
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.attendee_event_fragment, AttendeeWaitlistTextFragment.class, bundle)
                                .commit();
                        break;

                    case "cancelled":
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.attendee_event_fragment, AttendeeWaitlistTextFragment.class, bundle)
                                .commit();
                        break;
                    case "selected":
                        titleText.setText("You Won the Draw!!");
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.attendee_event_fragment, AttendeeWaitlistChoiceFragment.class, bundle)
                                .commit();
                        break;

                    case "not selected":
                        titleText.setText("You Lost the Draw");
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.attendee_event_fragment, AttendeeWaitlistChoiceFragment.class, bundle)
                                .commit();
                        break;
                    default:
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.attendee_event_fragment, AttendeeWaitlistJoinedFragment.class, bundle)
                                .commit();
                        break;
                }

            });
        }

    }

}