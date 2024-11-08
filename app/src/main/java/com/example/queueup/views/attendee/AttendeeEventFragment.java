package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;

/**
 * AttendeeEventFragment is a fragment that displays the details of an event for an attendee.
 * It includes the event's date, location, time, and poster image.
 * This fragment is part of the attendee's experience in viewing and interacting with the event.
 */
public class AttendeeEventFragment extends Fragment {
    public AttendeeEventFragment() {
        super(R.layout.attendee_event_activity);
    }
    ImageView poster;


    /**
     * Called when the fragment's view is created. This method is used to initialize the UI components
     * such as the event's date, location, time, and poster image. It also sets the event poster
     * image from a drawable resource.
     *
     * @param view The root view of the fragment's layout.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        TextView eventDate = view.findViewById(R.id.event_date);
        TextView locationText = view.findViewById(R.id.event_date);
        TextView dateText = view.findViewById(R.id.event_date);
        TextView timeText = view.findViewById(R.id.event_time);
        poster = view.findViewById(R.id.poster_image);
        poster.setImageResource(R.drawable.posterimage);
    }

}
