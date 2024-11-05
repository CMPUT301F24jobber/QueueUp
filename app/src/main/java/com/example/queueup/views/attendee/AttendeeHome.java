package com.example.queueup.views.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.example.queueup.views.admin.AdminGalleryFragment;
import com.example.queueup.views.admin.AdminHomeFragment;
import com.example.queueup.views.admin.AdminProfileFragment;
import com.example.queueup.views.admin.AdminUsersFragment;
import com.example.queueup.views.profiles.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.bumptech.glide.Glide;

/**
 * The AttendeeHome class represents the home screen for attendees.
 * Displays a welcome message, allows navigation to the attendee's profile, and fetches user data.
 */
public class AttendeeHome extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView titleTextView;
    private TextView profileInitialsTextView;  // TextView to hold the initials
    private FrameLayout profileInitialsFrame;  // FrameLayout for the initials circle
    private String deviceId;
    private ImageView profileImageView;  // ImageView to display the profile picture
    private BottomNavigationView navigationView;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_activity);

        // Initialize Firebase and TextView
        db = FirebaseFirestore.getInstance();
        navigationView = findViewById(R.id.bottom_navigation);


        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());  // Fetch from UserController if not passed
        }

        if (savedInstanceState == null) {
            Fragment fragment = new AdminHomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.attendee_activity_fragment, AttendeeHomeFragment.class, null)
                    .commit();
        }
        navigationView.setOnItemSelectedListener( menuItem -> {
            String title = String.valueOf(menuItem.getTitle());
            switch (title) {
                case "Home":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_activity_fragment, AdminHomeFragment.class, null)
                            .addToBackStack("Home")

                            .commit();
                    break;
                case "Users":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_activity_fragment, AdminUsersFragment.class, null)
                            .addToBackStack("Users")
                            .commit();
                    break;
                case "Gallery":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)

                            .replace(R.id.admin_activity_fragment, AdminGalleryFragment.class, null)
                            .addToBackStack("Gallery")

                            .commit();
                    break;
                case "Profile":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_activity_fragment, AdminProfileFragment.class, null)
                            .addToBackStack("Profile")

                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });
//        // Set up listener for profile initials (similar to a button)
//        profileInitialsFrame.setOnClickListener(v -> {
//            Intent intent = new Intent(AttendeeHome.this, ProfileActivity.class);
//            intent.putExtra("deviceId", deviceId);
//            startActivity(intent);
//        });
   }
//
//    /**
//     * Called when the activity has become visible. Used here to refresh the user data.
//     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        fetchUserData();  // Re-fetch user data to update the UI with any changes made
//    }
//
//    /**
//     * Fetches user data from Firestore and updates the UI with the user's first name and initials.
//     */
//    private void fetchUserData() {
//        db.collection("users")
//                .whereEqualTo("deviceId", deviceId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            User user = document.toObject(User.class);
//                            String firstName = user.getFirstName();
//                            String lastName = user.getLastName();
//                            String profileImageUrl = user.getProfileImageUrl();  // getting profile image URL
//
//                            // Set the welcome text
//                            if (firstName != null && !firstName.isEmpty()) {
//                                titleTextView.setText("Welcome, " + firstName + "!");
//                            } else {
//                                titleTextView.setText("Welcome, Attendee!");
//                            }
//
//                            // seeing if profile pic exists, if not then proceeding with initials
//                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
//                                profileInitialsTextView.setVisibility(View.GONE);
//                                profileImageView.setVisibility(View.VISIBLE);  // show profile image
//                                Glide.with(this).load(profileImageUrl).circleCrop().into(profileImageView); // Resource used: https://www.geeksforgeeks.org/image-loading-caching-library-android-set-2/
//                            } else {
//                                // if no profile image, then display initials instead
//                                profileImageView.setVisibility(View.GONE);
//                                profileInitialsTextView.setVisibility(View.VISIBLE);
//
//                                // Setting initials
//                                if (firstName != null && lastName != null) {
//                                    String initials = firstName.substring(0, 1) + lastName.substring(0, 1);
//                                    profileInitialsTextView.setText(initials);
//                                } else if (firstName != null) {
//                                    profileInitialsTextView.setText(firstName.substring(0, 1));
//                                }
//                            }
//                        }
//                    } else {
//                        Log.d("AttendeeHome", "No user found with this device ID");
//                        titleTextView.setText("Welcome, Attendee!");  // if no user found
//                    }
//                })
//                .addOnFailureListener(e -> Log.e("AttendeeHome", "Error fetching user data", e));
//    }
}