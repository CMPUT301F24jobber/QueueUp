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
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UsersArrayAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AttendeeWaitlistFragment extends Fragment {
    public AttendeeWaitlistFragment() {
        super(R.layout.attendee_join_waitlist_fragment);
    }
    Button joinWaitlistButton;
    EventController eventController;
    AttendeeController attendeeController;

    /**
     * Called when the fragment is created.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        joinWaitlistButton = view.findViewById(R.id.join_waitlist);
        Event event = this.getArguments().getSerializable("event", Event.class);
        attendeeController = AttendeeController.getInstance();
        eventController = EventController.getInstance();
        joinWaitlistButton.setOnClickListener((v) -> {
            eventController.registerToEvent(event.getEventId());
            joinWaitlistButton.setVisibility(View.INVISIBLE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.attendee_event_fragment, AttendeeWaitlistJoinedFragment.class, bundle)
                    .commit();
        });
    }

}
