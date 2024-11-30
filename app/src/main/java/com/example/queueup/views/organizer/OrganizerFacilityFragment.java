package com.example.queueup.views.organizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.queueup.MainActivity;
import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UserViewModel;
import com.example.queueup.views.profiles.EditProfileActivity;
import com.google.android.material.button.MaterialButton;


public class OrganizerFacilityFragment extends Fragment {
    public OrganizerFacilityFragment() {
        super(R.layout.facility_page);
    }

    private TextView facilityName;
    private Button editButton;
    private User user;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        facilityName = view.findViewById(R.id.facility_name);
        editButton = view.findViewById(R.id.editButton);
        user = CurrentUserHandler.getSingleton().getCurrentUser().getValue();
        facilityName.setText(user.getFacility());
        editButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            OrganizerCreateFacility facilityDialog = new OrganizerCreateFacility();
            facilityDialog.setArguments(bundle);
            transaction.add(android.R.id.content, facilityDialog)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        });
    }
}
