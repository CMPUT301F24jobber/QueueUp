package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.Event;

public class AttendeeWaitlistChoiceFragment extends Fragment {
    public AttendeeWaitlistChoiceFragment() {
        super(R.layout.attendee_buttons_fragment);
    }


    private Button buttonOne, leaveButton;
    private EventController eventController;
    private AttendeeController attendeeController;
    private CurrentUserHandler currentUserHandler;
    private Event event;
    private Attendee attendee;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        event = this.getArguments().getSerializable("event", Event.class);
        attendee = this.getArguments().getSerializable("attendee", Attendee.class);
        eventController = EventController.getInstance();
        attendeeController = AttendeeController.getInstance();
        buttonOne = view.findViewById(R.id.button_one);
        leaveButton = view.findViewById(R.id.button_two);


        if (attendee.getStatus().equals("selected")) {
            buttonOne.setText("Enroll in Event");
            leaveButton.setText("Decline");
        } else {
            buttonOne.setText("Get a chance to be redrawn");
            leaveButton.setText("Decline");
        }
        leaveButton.setOnClickListener(v -> {
            attendeeController.setAttendeeStatus(attendee.getId(), "cancelled");
            getActivity().onBackPressed();
        });

    }
}
