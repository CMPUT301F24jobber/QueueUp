package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;

public class AttendeeWaitlistJoinedFragment extends Fragment {
    public AttendeeWaitlistJoinedFragment() {
        super(R.layout.attendee_waitlist_joined);
    }


    Button leaveWaitlistButton;
    EventController eventController;
    AttendeeController attendeeController;
    CurrentUserHandler currentUserHandler;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        leaveWaitlistButton = view.findViewById(R.id.leave_waitlist);
        Event event = this.getArguments().getSerializable("event", Event.class);
        attendeeController = AttendeeController.getInstance();
        eventController = EventController.getInstance();
        currentUserHandler = CurrentUserHandler.getSingleton();
        leaveWaitlistButton.setOnClickListener((v) -> {
            attendeeController.setAttendeeStatus(currentUserHandler.getCurrentUserId(), "cancelled");
            eventController.unregisterFromEvent( event.getEventId(), currentUserHandler.getCurrentUserId());
            leaveWaitlistButton.setVisibility(View.INVISIBLE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.attendee_event_fragment, AttendeeWaitlistFragment.class, bundle)
                    .commit();

        });
    }
}
