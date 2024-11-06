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
import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileFragment extends Fragment {
    private static final int EDIT_PROFILE_REQUEST_CODE = 1;

    private FirebaseFirestore db;
    private String deviceId;
    private TextView profileNameTextView, profileUsernameTextView, profileEmailTextView, profilePhoneTextView, profileInitialsTextView;
    private ImageView profileImageView;
    private User currentUser;

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

        deviceId = UserController.getInstance().getDeviceId(requireContext());

        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            // Start EditProfileActivity for result
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("deviceId", deviceId);  // Pass the device ID if needed
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
        });

        fetchUserData();  // Initial data fetch
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Profile was updated, so re-fetch user data to display the latest changes
            fetchUserData();
        }
    }

    private void fetchUserData() {
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            currentUser = document.toObject(User.class);
                            String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
                            profileNameTextView.setText(fullName);
                            profileUsernameTextView.setText(currentUser.getUsername());
                            profileEmailTextView.setText(currentUser.getEmailAddress());
                            profilePhoneTextView.setText(currentUser.getPhoneNumber());

                            displayProfileImageOrInitials();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void displayProfileImageOrInitials() {
        if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentUser.getProfileImageUrl()).circleCrop().into(profileImageView);
        } else {
            profileImageView.setVisibility(View.GONE);
            profileInitialsTextView.setVisibility(View.VISIBLE);

            // Display initials if no profile image
            String initials = (currentUser.getFirstName() != null ? currentUser.getFirstName().substring(0, 1) : "") +
                    (currentUser.getLastName() != null ? currentUser.getLastName().substring(0, 1) : "");
            profileInitialsTextView.setText(initials);
        }
    }
}
