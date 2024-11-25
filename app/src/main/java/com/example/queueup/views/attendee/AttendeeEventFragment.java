package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;

public class AttendeeEventFragment extends Fragment {
    public AttendeeEventFragment() {
        super(R.layout.attendee_event_activity);
    }
    ImageView poster;


    /**
     * Called when the fragment is created.
     *
     * @param view
     * @param savedInstanceState
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
