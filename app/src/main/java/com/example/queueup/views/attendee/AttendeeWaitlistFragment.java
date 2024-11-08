package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UsersArrayAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * AttendeeWaitlistFragment represents the fragment where an attendee can join an event's waitlist.
 * It provides a button to register the user to the event's waitlist and transitions to another fragment once registered.
 */
public class AttendeeWaitlistFragment extends Fragment {
    public AttendeeWaitlistFragment() {
        super(R.layout.attendee_join_waitlist_fragment);
    }
    Button joinWaitlistButton;
    EventController eventController;

    /**
     * Called when the fragment's view has been created. This method sets up the button for joining the waitlist
     * and initializes the EventController to handle the waitlist registration logic.
     *
     * @param view The view returned by {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState A Bundle containing the fragment's previous saved state, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        joinWaitlistButton = view.findViewById(R.id.join_waitlist);
        Event event = this.getArguments().getSerializable("event", Event.class);
        eventController = EventController.getInstance();
        joinWaitlistButton.setOnClickListener((v) -> {
            eventController.registerToEvent(event.getEventId());
            joinWaitlistButton.setVisibility(View.INVISIBLE);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.attendee_event_fragment, AttendeeWaitlistJoinedFragment.class, null)
                    .commit();
        });
    }

}
