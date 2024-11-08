package com.example.queueup.views.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.views.profiles.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * AdminHome activity serves as the main screen for the admin interface. It provides navigation to different
 * sections such as Home, Users, Gallery, and Profile through a BottomNavigationView. It also manages fragment
 * transactions and device ID handling.
 */
public class AdminHome extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView titleTextView;
    private String deviceId;
    private BottomNavigationView navigationView;
    private ImageButton backButton;

    /**
     * Called when the activity is first created. Initializes views, sets up the back button,
     * and handles fragment transactions based on the selected bottom navigation item.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           (Otherwise it is null.)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        navigationView = findViewById(R.id.bottom_navigation);
        db = FirebaseFirestore.getInstance();
        titleTextView = findViewById(R.id.titleTextView);  // Assuming you have a TextView for the title
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });
        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());  // Fetch from UserController if not passed
        }

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.admin_activity_fragment);
        NavController navController = navHostFragment.getNavController();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.admin_activity_fragment, AdminHomeFragment.class, null)
                    .commit();
        }


        navigationView.setOnItemSelectedListener(menuItem -> {
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
                            .replace(R.id.admin_activity_fragment, ProfileFragment.class, null) // Changed to ProfileFragment
                            .addToBackStack("Profile")
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });

    }

}