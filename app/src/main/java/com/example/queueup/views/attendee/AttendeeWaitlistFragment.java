package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.view.View;
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

public class AttendeeWaitlistFragment extends Fragment {
    public AttendeeWaitlistFragment() {
        super(R.layout.attendee_join_waitlist_fragment);
    }
    Button joinWaitlistButton;
    EventController eventController;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        joinWaitlistButton = view.findViewById(R.id.join_waitlist);
        Event event = this.getArguments().getSerializable("event", Event.class);
        eventController = EventController.getInstance();
        joinWaitlistButton.setOnClickListener((v) -> {
            eventController.registerToEvent(event.getEventId());
            joinWaitlistButton.setVisibility(View.INVISIBLE);
        });
    }

}
