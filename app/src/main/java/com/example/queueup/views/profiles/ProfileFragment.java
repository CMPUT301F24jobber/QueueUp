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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.queueup.MainActivity;
import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UserViewModel;
import com.google.android.material.button.MaterialButton;


public class ProfileFragment extends Fragment {
    private static final int EDIT_PROFILE_REQUEST_CODE = 1;

    private TextView profileNameTextView;
    private TextView profileUsernameTextView;
    private TextView profileEmailTextView;
    private TextView profilePhoneTextView;
    private TextView profileInitialsTextView;
    private ImageView profileImageView;
    private MaterialButton switchRoleButton;
    private Button editButton;
    private UserViewModel userViewModel;
    private User currentUser;

    private String deviceId;

    public ProfileFragment() {
    }


    /**
     * Called when the fragment is created. Initializes the ViewModel.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    /**
     * Called when the fragment's view is created. Initializes UI elements and sets up click listeners.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.profile_page, container, false);

        // Initialize UI elements
        profileNameTextView = view.findViewById(R.id.profileNameTextView);
        profileUsernameTextView = view.findViewById(R.id.profileUsernameTextView);
        profileEmailTextView = view.findViewById(R.id.profileEmailTextView);
        profilePhoneTextView = view.findViewById(R.id.profilePhoneTextView);
        profileInitialsTextView = view.findViewById(R.id.profileInitialsTextView);
        profileImageView = view.findViewById(R.id.profileImageView);
        switchRoleButton = view.findViewById(R.id.switch_role);
        editButton = view.findViewById(R.id.editButton);

        // Retrieve Device ID
        deviceId = UserController.getInstance().getDeviceId(requireContext());

        // Observe LiveData from UserViewModel
        observeViewModel();

        fetchUserData();

        // Set up Edit Button Click Listener
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
        });

        // Set up Switch Role Button Click Listener
        switchRoleButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    /**
     * Observes LiveData from the UserViewModel.
     */
    private void observeViewModel() {
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                updateUIWithUserData();
            }
        });

        userViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * Fetches the current user's data using the ViewModel.
     */
    private void fetchUserData() {
        userViewModel.loadUserByDeviceId(deviceId);
    }

    /**
     * Updates the UI elements with the current user's data.
     */
    private void updateUIWithUserData() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "User data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set Full Name
        String firstName = currentUser.getFirstName() != null ? currentUser.getFirstName() : "";
        String lastName = currentUser.getLastName() != null ? currentUser.getLastName() : "";
        String fullName = (firstName + " " + lastName).trim();
        profileNameTextView.setText(fullName.isEmpty() ? "No Name" : fullName);

        // Set Username
        String username = currentUser.getUsername() != null ? currentUser.getUsername() : "Unknown";
        profileUsernameTextView.setText("@" + username);

        // Set Email Address
        String email = currentUser.getEmailAddress() != null ? currentUser.getEmailAddress() : "No Email";
        profileEmailTextView.setText("Email: " + email);

        // Set Phone Number
        String phone = currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "No Phone";
        profilePhoneTextView.setText("Phone Number: " + phone);

        // Display Profile Image or Initials
        displayProfileImageOrInitials();
    }

    /**
     * Displays the user's profile image if available, or their initials if not.
     */
    private void displayProfileImageOrInitials() {
        if (currentUser == null) {
            // Safety check
            profileImageView.setVisibility(View.GONE);
            profileInitialsTextView.setVisibility(View.VISIBLE);
            profileInitialsTextView.setText("??");
            return;
        }

        String profileImageUrl = currentUser.getProfileImageUrl();

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            // Display Profile Image
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(profileImageUrl)
                    .circleCrop()
                    .into(profileImageView);
        } else {
            // Display Initials
            profileImageView.setVisibility(View.GONE);
            profileInitialsTextView.setVisibility(View.VISIBLE);

            String initials = "";
            if (currentUser.getFirstName() != null && !currentUser.getFirstName().isEmpty()) {
                initials += currentUser.getFirstName().substring(0, 1).toUpperCase();
            }
            if (currentUser.getLastName() != null && !currentUser.getLastName().isEmpty()) {
                initials += currentUser.getLastName().substring(0, 1).toUpperCase();
            }
            profileInitialsTextView.setText(initials.isEmpty() ? "??" : initials);
        }
    }

    /**
     * Called when the fragment is resumed. Fetches the user's data.
     */
    @Override
    public void onResume() {
        super.onResume();
        fetchUserData();
    }

    /**
     * Called when the fragment receives a result from another activity.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from EditProfileActivity
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fetchUserData();
        }
    }
}
