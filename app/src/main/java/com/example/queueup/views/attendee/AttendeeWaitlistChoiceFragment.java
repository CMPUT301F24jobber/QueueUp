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
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AttendeeWaitlistChoiceFragment extends Fragment {
    public AttendeeWaitlistChoiceFragment() {
        super(R.layout.attendee_buttons_fragment);
    }


    private Button buttonOne, leaveButton;
    private EventController eventController;
    private AttendeeController attendeeController;
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
            buttonOne.setOnClickListener( v -> {
                attendeeController.setAttendeeStatus(attendee.getId(), "enrolled");
                attendee.setStatus("enrolled");
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                bundle.putSerializable("attendee", attendee);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.attendee_event_fragment, AttendeeWaitlistTextFragment.class, bundle)
                        .commit();
            });
            leaveButton.setText("Decline");
            leaveButton.setOnClickListener( v -> {
                attendeeController.setAttendeeStatus(attendee.getId(), "cancelled");
                eventController.getEventById(event.getEventId()).addOnSuccessListener(querySnapshot -> {
                        event = querySnapshot.toObject(Event.class);
                        if (event.getRedrawEnabled()) eventController.handleReplacement(attendee.getUserId(), event.getEventId(), event.getEventName());
                    });
                attendee.setStatus("cancelled");

                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                bundle.putSerializable("attendee", attendee);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.attendee_event_fragment, AttendeeWaitlistTextFragment.class, bundle)
                        .commit();
            });
        } else {
            buttonOne.setText("Redraw me");
            buttonOne.setOnClickListener(v -> {
                attendeeController.setAttendeeStatus(attendee.getId(), "waiting");
                attendee.setStatus("waiting");

                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                bundle.putSerializable("attendee", attendee);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.attendee_event_fragment, AttendeeWaitlistJoinedFragment.class, bundle)
                        .commit();
            });
            leaveButton.setText("Decline");
            leaveButton.setOnClickListener(v -> {
                eventController.unregisterFromEvent(event.getEventId(), attendee.getUserId());
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.attendee_event_fragment, AttendeeWaitlistFragment.class, bundle)
                        .commit();
            });
        }


    }
}
