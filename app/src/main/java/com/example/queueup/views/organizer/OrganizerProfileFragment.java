package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;

public class OrganizerProfileFragment extends Fragment {
    public OrganizerProfileFragment() {
        super(R.layout.organizer_user_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        User e = new User("e","e","e","e","e","e");
    }

}