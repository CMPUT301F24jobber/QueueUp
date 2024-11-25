package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.handlers.PushNotificationHandler;
import com.example.queueup.models.Event;

public class OrganizerRedrawFragment extends Fragment {
    public OrganizerRedrawFragment() {
        super(R.layout.organizer_redraw_winners_fragment);
    }


    Button redrawWinners;
    Button cancelWinners;

    ToggleButton winnerNotification;
    ToggleButton cancelNotification;
    private Event event;

    EventController eventController;
    AttendeeController attendeeController;
    CurrentUserHandler currentUserHandler;
    PushNotificationHandler pushNotificationHandler = PushNotificationHandler.getSingleton();

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        event = this.getArguments().getSerializable("event", Event.class);
        redrawWinners = view.findViewById(R.id.redraw_winners);
        cancelWinners = view.findViewById(R.id.cancel_winners);

        winnerNotification = view.findViewById(R.id.notification_winner);
        cancelNotification = view.findViewById(R.id.notification_cancelled);



        redrawWinners.setOnClickListener(v -> {
            if (winnerNotification.isChecked()) {

            }
        });
        cancelWinners.setOnClickListener(v -> {
            if (winnerNotification.isChecked()) {

            }
        });
    }
}
