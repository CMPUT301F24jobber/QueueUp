package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;
import com.example.queueup.services.ImageUploader;
import com.example.queueup.viewmodels.EventViewModel;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.time.format.DateTimeFormatter;


public class OrganizerDraw extends Fragment {
    public OrganizerDraw() {
        super(R.layout.organizer_draw_fragment);
    }

    private Event event;
    private ImageUploader imageUploader;
    private EventController eventController;
    private EventViewModel eventViewModel;
    private Button drawWinners;
    private ToggleButton winnerNotification;
    private ToggleButton loserNotification;
    private ToggleButton everyoneNotification;
    private TextInputLayout numDraw;

    private Button cancelWinners;
    private SwitchMaterial redrawSwitch;
    private ConstraintLayout drawLayout;
    private ConstraintLayout redrawLayout;

    private ToggleButton rewinnerNotification;

    private ToggleButton cancelNotification;

    /**
     * Called when the fragment's view has been created.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        event = this.getArguments().getSerializable("event", Event.class);
        TextView eventDate = view.findViewById(R.id.event_date);
        eventController = EventController.getInstance();
        TextView locationText = view.findViewById(R.id.event_location);
        TextView timeText = view.findViewById(R.id.event_time);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE MMM dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String date_text = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(dateFormatter);
        String time_text = event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).format(timeFormatter);
        eventDate.setText(date_text);
        locationText.setText(event.getEventLocation());
        ImageView posterImage = view.findViewById(R.id.poster_image);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        drawWinners = view.findViewById(R.id.draw_winners);
        numDraw = view.findViewById(R.id.draw_num_attendee);

        winnerNotification = view.findViewById(R.id.notification_winner);
        loserNotification = view.findViewById(R.id.notification_loser);
        everyoneNotification =  view.findViewById(R.id.notification_everyone);

        redrawSwitch = view.findViewById(R.id.redraw_switch);
        rewinnerNotification = view.findViewById(R.id.notification_redraw_winner);
        cancelWinners = view.findViewById(R.id.cancel_winners);
        cancelNotification = view.findViewById(R.id.notification_cancelled);
        drawLayout = view.findViewById(R.id.draw_layout);
        redrawLayout = view.findViewById(R.id.redraw_layout);

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
        winnerNotification.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                winnerNotification.setBackgroundResource(R.drawable.filled_button);
            } else {
                winnerNotification.setBackgroundResource(R.drawable.hollow_button);
            }

        });
        loserNotification.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                loserNotification.setBackgroundResource(R.drawable.filled_button);
            } else {
                loserNotification.setBackgroundResource(R.drawable.hollow_button);
            }

        });
        everyoneNotification.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                everyoneNotification.setBackgroundResource(R.drawable.filled_button);
            } else {
                everyoneNotification.setBackgroundResource(R.drawable.hollow_button);
            }

        });
        rewinnerNotification.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                rewinnerNotification.setBackgroundResource(R.drawable.filled_button);
            } else {
                rewinnerNotification.setBackgroundResource(R.drawable.hollow_button);
            }
        });
        cancelNotification.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                cancelNotification.setBackgroundResource(R.drawable.filled_button);
            } else {
                cancelNotification.setBackgroundResource(R.drawable.hollow_button);
            }
        });

        if (event.getIsDrawn()) {
            redrawLayout.setVisibility(View.VISIBLE);
            drawLayout.setVisibility(View.INVISIBLE);
            redrawSwitch.setOnCheckedChangeListener((v, toggled) -> {
                if (rewinnerNotification.isChecked()) {
                }
                if (cancelNotification.isChecked()) {
                }
            });
        } else {
            redrawLayout.setVisibility(View.INVISIBLE);
            drawLayout.setVisibility(View.VISIBLE);
            drawWinners.setOnClickListener(v -> {
                if (everyoneNotification.isChecked()) {

                } else {
                    if (loserNotification.isChecked()) {

                    }
                    if (winnerNotification.isChecked()) {

                    }
                }
                if (!numDraw.getEditText().getText().toString().isEmpty()) {
                    redrawLayout.setVisibility(View.VISIBLE);
                    drawLayout.setVisibility(View.INVISIBLE);
                    eventController.drawLottery(event.getEventId(), Integer.valueOf(numDraw.getEditText().getText().toString().trim()));
                    eventController.setIsDrawn(event.getEventId());
                } else {
                    Toast.makeText(getContext(), "The number of winners is invalid", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

}