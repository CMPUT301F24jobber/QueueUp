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
        super(R.layout.organizer_draw_winners_fragment);
    }


    Button drawWinners;
    ToggleButton winnerNotification;
    ToggleButton loserNotification;
    ToggleButton everyoneNotification;
    private Event event;

    EventController eventController;
    AttendeeController attendeeController;
    CurrentUserHandler currentUserHandler;
    PushNotificationHandler pushNotificationHandler = PushNotificationHandler.getSingleton();

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        event = this.getArguments().getSerializable("event", Event.class);
        drawWinners = view.findViewById(R.id.draw_winners);
        winnerNotification = view.findViewById(R.id.notification_winner);
        loserNotification = view.findViewById(R.id.notification_loser);
        everyoneNotification =  view.findViewById(R.id.notification_everyone);


        drawWinners.setOnClickListener(v -> {
            if (everyoneNotification.isChecked()) {

            } else {
                if (loserNotification.isChecked()) {

                }
                if (winnerNotification.isChecked()) {

                }
            }

        });
    }
}
