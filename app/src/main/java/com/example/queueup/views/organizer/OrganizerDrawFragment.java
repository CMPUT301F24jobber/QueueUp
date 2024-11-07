package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UsersArrayAdapter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OrganizerDrawFragment extends Fragment {
    public OrganizerDrawFragment() {
        super(R.layout.organizer_draw_fragment);
    }

    Event event;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        event = this.getArguments().getSerializable("event", Event.class);
        TextView eventDate = view.findViewById(R.id.event_date);
        TextView locationText = view.findViewById(R.id.event_location);
        TextView timeText = view.findViewById(R.id.event_time);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE MMM dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String date_text = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(dateFormatter);
        String time_text = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).format(timeFormatter);
        eventDate.setText(date_text);
        locationText.setText(event.getEventLocation());
        ImageView posterImage = view.findViewById(R.id.poster_image);

        Glide.with(this).load(event.getEventBannerImageUrl()).into(posterImage);
        timeText.setText(time_text);



    }

}
