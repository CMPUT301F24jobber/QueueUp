package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;

public class AttendeeEventFragment extends Fragment {
    public AttendeeEventFragment() {
        super(R.layout.attendee_poster_fragment);
    }
    ImageView poster;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        poster = view.findViewById(R.id.poster_image);
        poster.setImageResource(R.drawable.posterimage);
    }

}
