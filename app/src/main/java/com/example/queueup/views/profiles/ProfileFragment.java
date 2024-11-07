package com.example.queueup.views.profiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.queueup.MainActivity;
import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {
    private static final int EDIT_PROFILE_REQUEST_CODE = 1;


    private String deviceId;
    private TextView profileNameTextView, profileUsernameTextView, profileEmailTextView, profilePhoneTextView, profileInitialsTextView;
    private ImageView profileImageView;
    private User currentUser;
    private MaterialButton switchRoleButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_page, container, false);

        db = FirebaseFirestore.getInstance();
        profileNameTextView = view.findViewById(R.id.profileNameTextView);
        profileUsernameTextView = view.findViewById(R.id.profileUsernameTextView);
        profileEmailTextView = view.findViewById(R.id.profileEmailTextView);
        profilePhoneTextView = view.findViewById(R.id.profilePhoneTextView);
        profileInitialsTextView = view.findViewById(R.id.profileInitialsTextView);
        profileImageView = view.findViewById(R.id.profileImageView);
        switchRoleButton = view.findViewById(R.id.switch_role);
        deviceId = UserController.getInstance().getDeviceId(requireContext());
        fetchUserData();
        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
        });

        switchRoleButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fetchUserData();
        }
    }

    private void fetchUserData() {
        currentUser = CurrentUserHandler.getSingleton().getCurrentUser().getValue();

        String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
        profileNameTextView.setText(fullName);
        profileUsernameTextView.setText(currentUser.getUsername());
        profileEmailTextView.setText(currentUser.getEmailAddress());
        profilePhoneTextView.setText(currentUser.getPhoneNumber());

        displayProfileImageOrInitials();
    }

    private void displayProfileImageOrInitials() {
        if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentUser.getProfileImageUrl()).circleCrop().into(profileImageView);
        } else {
            profileImageView.setVisibility(View.GONE);
            profileInitialsTextView.setVisibility(View.VISIBLE);
            String initials = (currentUser.getFirstName() != null ? currentUser.getFirstName().substring(0, 1) : "") +
                    (currentUser.getLastName() != null ? currentUser.getLastName().substring(0, 1) : "");
            profileInitialsTextView.setText(initials);
        }
    }
}
