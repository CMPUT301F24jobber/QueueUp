package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.services.ImageUploader;
import com.example.queueup.viewmodels.EventViewModel;
import com.google.android.material.button.MaterialButton;

import java.time.format.DateTimeFormatter;

/**
 * OrganizerDrawFragment represents the fragment where the organizer can view event details,
 * including event date, time, location, and poster image. The organizer can also update the event's
 * poster image by selecting a new image from their device.
 */
public class OrganizerDrawFragment extends Fragment {
    public OrganizerDrawFragment() {
        super(R.layout.organizer_draw_fragment);
    }

    Event event;
    ImageUploader imageUploader;
    private EventViewModel eventViewModel;

    private MaterialButton drawWinnerButton;


    /**
     * Called when the fragment's view is created. It initializes the UI components, binds data to the views,
     * and sets up the functionality for changing the event poster image.
     *
     * @param view The root view for the fragment.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state, if any.
     */
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
        drawWinnerButton = view.findViewById(R.id.draw_winner_button);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        Glide.with(this).load(event.getEventBannerImageUrl()).into(posterImage);
        timeText.setText(time_text);
        imageUploader = new ImageUploader();
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        Glide.with(this).load(uri).into(posterImage);
                        imageUploader.uploadImage("profile_pictures/", uri, new ImageUploader.UploadListener() {
                            @Override
                            public void onUploadSuccess(String imageUrl) {
                                eventViewModel.setEventBannerImage(event.getEventId(), imageUrl);
                            }

                            @Override
                            public void onUploadFailure(Exception exception) {
                            }
                        });
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
        posterImage.setOnClickListener( v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        drawWinnerButton.setOnClickListener(v -> {
            eventViewModel.drawLottery(event.getEventId(), event.getMaxCapacity());
        });



    }

}
