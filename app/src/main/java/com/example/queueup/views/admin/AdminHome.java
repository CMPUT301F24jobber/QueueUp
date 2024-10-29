package com.example.queueup.views.admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

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
        navigationView.setOnItemSelectedListener( menuItem -> {
            String title = String.valueOf(menuItem.getTitle());
            switch (title) {
                case "Home":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.admin_activity_fragment, AdminHomeFragment.class, null)
                            .commit();
                    break;
                case "Users":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.admin_activity_fragment, AdminUsersFragment.class, null)
                            .commit();
                    break;
                case "Gallery":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.admin_activity_fragment, AdminGalleryFragment.class, null)
                            .commit();
                    break;
                case "Profile":
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.admin_activity_fragment, AdminProfileFragment.class, null)
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });

    }



}

    private void fetchUserData() {
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // if dev ID found in Firestore
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
                        titleTextView.setText("Welcome, Admin!");  // if no user found
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

