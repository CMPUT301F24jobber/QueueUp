package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.Event;

public class AttendeeWaitlistJoinedFragment extends Fragment {
    public AttendeeWaitlistJoinedFragment() {
        super(R.layout.attendee_waitlist_leave);
    }


    private Button leaveWaitlistButton;
    private EventController eventController;
    private AttendeeController attendeeController;
    private CurrentUserHandler currentUserHandler;
    private TextView joinedText;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        leaveWaitlistButton = view.findViewById(R.id.leave_waitlist);
        joinedText = view.findViewById(R.id.waitlist_joined);

        Event event = this.getArguments().getSerializable("event", Event.class);
        Attendee attendee = this.getArguments().getSerializable("attendee", Attendee.class);

        attendeeController = AttendeeController.getInstance();
        eventController = EventController.getInstance();
        currentUserHandler = CurrentUserHandler.getSingleton();
        if (event.getIsDrawn()) {
            joinedText.setText("You will have a chance to be selected if another attendee cancels");
        }
        leaveWaitlistButton.setOnClickListener((v) -> {
            eventController.unregisterFromEvent(event.getEventId(), currentUserHandler.getCurrentUserId());
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
