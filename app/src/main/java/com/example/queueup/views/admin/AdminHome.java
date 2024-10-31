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