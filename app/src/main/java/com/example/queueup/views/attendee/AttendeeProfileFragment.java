package com.example.queueup.views.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.MainActivity;
import com.example.queueup.R;
import com.google.android.material.button.MaterialButton;

public class AttendeeProfileFragment extends Fragment {
    public AttendeeProfileFragment() {
        super(R.layout.attendee_user_profile);
    }
    private MaterialButton switchRole;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchRole = view.findViewById(R.id.switch_role);
        switchRole.setOnClickListener((v) -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            view.getContext().startActivity(intent);
            getActivity().finish();
        });

    }

}