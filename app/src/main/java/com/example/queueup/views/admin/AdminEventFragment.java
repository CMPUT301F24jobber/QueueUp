package com.example.queueup.views.admin;

import static android.content.Intent.getIntent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.google.android.material.button.MaterialButton;

import java.time.format.DateTimeFormatter;


public class AdminEventFragment extends Fragment {
    public AdminEventFragment() {
        super(R.layout.admin_event_fragment);
    }

    ToggleButton qrToggle;
    TextView eventTitle;
    TextView startDate;
    TextView endDate;
    TextView eventLocation;
    ImageView eventImage;
    MaterialButton deleteButton;

    /**
     * call when the fragment's view has been created.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        qrToggle = view.findViewById(R.id.delete_qr_button);
        eventTitle = view.findViewById(R.id.event_title);
        startDate = view.findViewById(R.id.start_date);
        endDate = view.findViewById(R.id.end_date);
        eventLocation = view.findViewById(R.id.event_location);
        eventImage = view.findViewById(R.id.image_view);
        deleteButton = view.findViewById(R.id.delete_button);
        Event event = this.getArguments().getSerializable("event", Event.class);

        qrToggle.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                qrToggle.setBackgroundResource(R.drawable.filled_button);
            } else {
                qrToggle.setBackgroundResource(R.drawable.hollow_button);
            }

        });



        eventTitle.setText(event.getEventName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE MMM dd, uuuu");
        String startDateText = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(formatter);
        String endDateText = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(formatter);
        Glide.with(this).load(event.getEventBannerImageUrl()).into(eventImage);

        startDate.setText(startDateText);
        endDate.setText(endDateText);
        eventLocation.setText(event.getEventLocation());
        deleteButton.setOnClickListener(v -> {
            EventController.getInstance().deleteEvent(event.getEventId()).addOnSuccessListener(stuf -> {
                getActivity().onBackPressed();
            });
        });

    }

}
