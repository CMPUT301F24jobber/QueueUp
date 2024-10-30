package com.example.queueup.views.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;  // Ensure this model class exists in your project
import com.example.queueup.views.admin.AdminHomeFragment;
import com.example.queueup.views.admin.AdminUsersFragment;
import com.example.queueup.views.admin.AdminGalleryFragment;
import com.example.queueup.views.admin.AdminProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AdminHome extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView titleTextView;
    private String deviceId;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        navigationView = findViewById(R.id.bottom_navigation);
        db = FirebaseFirestore.getInstance();
        titleTextView = findViewById(R.id.titleTextView);  // Assuming you have a TextView for the title

        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());  // Fetch from UserController if not passed
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.admin_activity_fragment, AdminHomeFragment.class, null)
                    .commit();
        }

        // Bottom navigation setup
        navigationView.setOnItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
//                case R.id.navigation_home:
//                    getSupportFragmentManager().beginTransaction()
//                            .setReorderingAllowed(true)
//                            .replace(R.id.admin_activity_fragment, new AdminHomeFragment())
//                            .commit();
//                    break;
//                case R.id.navigation_users:
//                    getSupportFragmentManager().beginTransaction()
//                            .setReorderingAllowed(true)
//                            .replace(R.id.admin_activity_fragment, new AdminUsersFragment())
//                            .commit();
//                    break;
//                case R.id.navigation_gallery:
//                    getSupportFragmentManager().beginTransaction()
//                            .setReorderingAllowed(true)
//                            .replace(R.id.admin_activity_fragment, new AdminGalleryFragment())
//                            .commit();
//                    break;
//                case R.id.navigation_profile:
//                    getSupportFragmentManager().beginTransaction()
//                            .setReorderingAllowed(true)
//                            .replace(R.id.admin_activity_fragment, new AdminProfileFragment())
//                            .commit();
//                    break;
                default:
                    break;
            }
            return true;
        });
    }

    /**
     * Fetches user data from Firestore using the deviceId and updates the UI.
     */
    private void fetchUserData() {
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Device ID found in Firestore
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            String firstName = user.getFirstName();

                            if (firstName != null && !firstName.isEmpty()) {
                                titleTextView.setText("Welcome, " + firstName + "!");
                            } else {
                                titleTextView.setText("Welcome, Admin!");  // Couldn't retrieve first name
                            }
                        }
                    } else {
                        Log.d("AdminHome", "No user found with this device ID");
                        titleTextView.setText("Welcome, Admin!");  // If no user found
                    }
                })
                .addOnFailureListener(e -> Log.e("AdminHome", "Error fetching user data", e));
    }

    /**
     * Called when the activity has become visible. Used here to refresh the user data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        fetchUserData();  // Re-fetch user data to update the UI with any changes made
    }
}
