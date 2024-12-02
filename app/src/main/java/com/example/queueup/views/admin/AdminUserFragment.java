package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.controllers.EventController;
import com.example.queueup.controllers.UserController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;


public class AdminUserFragment extends Fragment {
    public AdminUserFragment() {
        super(R.layout.admin_user_fragment);
    }


    /**
     * Called when the fragment's view has been created.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout facilityLayout = view.findViewById(R.id.facility_layout);
        MaterialButton deleteButton = view.findViewById(R.id.delete_button);
        MaterialButton deleteFacility = view.findViewById(R.id.delete_facility);

        TextView titleText = view.findViewById(R.id.profile_title);
        TextView facilityName = view.findViewById(R.id.facility_name);
        TextView displayName = view.findViewById(R.id.display_name);
        TextView userName = view.findViewById(R.id.username);
        TextView emailText = view.findViewById(R.id.email);
        TextView phoneText = view.findViewById(R.id.phone_number);
        TextView profileInitials = view.findViewById(R.id.profile_initials);
        ImageView profileImage = view.findViewById(R.id.profile_image);

        User user = this.getArguments().getParcelable("user", User.class);

        titleText.setText(user.getUsername() + "'s Profile");
        profileInitials.setText(user.getInitials());
        displayName.setText(user.getFullName());
        userName.setText("@" + user.getUsername());
        emailText.setText("Email: " + user.getEmailAddress());
        phoneText.setText("Phone: " + user.getPhoneNumber());
        if (user.getFacility() == null || user.getFacility().isEmpty()) {
            facilityLayout.setVisibility(View.GONE);
        } else {
            facilityName.setText(user.getFacility());
            deleteFacility.setOnClickListener( v -> {
                user.setFacility(null);
                EventController.getInstance().deleteEventsByUser(user.getUuid());

                UserController.getInstance().updateUser(user);
                facilityLayout.setVisibility(View.GONE);
            });
        }

        // Load profile picture using Glide (add Glide dependency if not already added)
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            profileInitials.setVisibility(View.INVISIBLE);
            Glide.with(requireContext())
                    .load(user.getProfileImageUrl())
                    .circleCrop()
                    .into(profileImage);
        }

        deleteButton.setOnClickListener((v) -> {
            EventController.getInstance().deleteEventsByUser(user.getUuid());

            UserController.getInstance().deleteUserById(user.getUuid()).addOnSuccessListener((stuf) -> {
                getActivity().onBackPressed();
            });
        });
    }
}