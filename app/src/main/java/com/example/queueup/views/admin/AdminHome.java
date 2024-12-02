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


public class AdminHome extends AppCompatActivity {

    private String deviceId;
    private BottomNavigationView navigationView;
    private ImageButton backButton;

    /**
     * Called when the activity is created. Initializes the UI elements and sets up the navigation bar.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        navigationView = findViewById(R.id.bottom_navigation);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });
        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());
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
            Fragment fragment = null;
            String tag = null;

            switch (title) {
                case "Home":
                    fragment = new AdminHomeFragment();
                    tag = "Home";
                    break;
                case "Users":
                    fragment = new AdminUsersFragment();
                    tag = "Users";
                    break;
                case "Gallery":
                    fragment = new AdminGalleryFragment();
                    tag = "Gallery";
                    break;
                case "Profile":
                    fragment = new ProfileFragment();
                    tag = "Profile";
                    break;
                default:
                    break;
            }

            if (fragment != null && getSupportFragmentManager().findFragmentByTag(tag) == null) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.admin_activity_fragment, fragment, tag)
                        .addToBackStack(tag)
                        .commit();
            }
            return true;
        });
    }

}