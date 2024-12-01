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

public class AttendeeWaitlistTextFragment extends Fragment {
    public AttendeeWaitlistTextFragment() {
        super(R.layout.attendee_waitlist_text_fragment);
    }

    private Event event;
    private Attendee attendee;
    private TextView textView;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        event = this.getArguments().getSerializable("event", Event.class);
        attendee = this.getArguments().getSerializable("attendee", Attendee.class);
        textView = view.findViewById(R.id.waitlist_text);
        if (attendee.getStatus().equals("enrolled")) {
            textView.setText("You have enrolled in this event!");
        } else {
            textView.setText("You invitation has been cancelled.");
        }
    }
}
