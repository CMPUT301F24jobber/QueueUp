package com.example.queueup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UserViewModel;
import com.example.queueup.views.SignUp;
import com.example.queueup.views.admin.AdminHome;
import com.example.queueup.views.attendee.AttendeeHome;
import com.example.queueup.views.organizer.OrganizerHome;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * MainActivity is the entry point of the app, where the user selects their role (Admin, Organizer, or Attendee) to proceed further.
 * It handles device ID checks, user role-based navigation, and role-specific UI updates.
 */
public class MainActivity extends AppCompatActivity {

    private MaterialButton adminButton;
    private MaterialButton organizerButton;
    private MaterialButton attendeeButton;
    private UserViewModel userViewModel;
    private FirebaseFirestore db;
    User user;
    private Boolean isAdmin = false;

    /**
     * Called when the activity is created. Initializes Firebase, sets up UI elements,
     * and handles edge-to-edge UI configurations. It also checks for an existing user.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        // Initialize Firestore and UserViewModel
        db = FirebaseFirestore.getInstance();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize buttons
        adminButton = findViewById(R.id.adminButton);
        organizerButton = findViewById(R.id.organizerButton);
        attendeeButton = findViewById(R.id.attendeeButton);

        // Initialize CurrentUserHandler Singleton
        CurrentUserHandler.setOwnerActivity(this);
        CurrentUserHandler.getSingleton();


        // Set up role selection buttons
        setupRoleSelection();

        // Check if user is already logged in and set up UI accordingly
        checkExistingUser();

        // isAdmin = true IS REQUIRED TO SEE ADMIN BUTTON AND ENTER ADMIN MODE
        // Handle window insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Checks if a user is already registered by their device ID and updates the UI accordingly.
     * If no user is found, it will redirect to the sign-up page.
     */
    private void checkExistingUser() {
        String deviceId = userViewModel.getDeviceId();
        if (deviceId != null && !deviceId.isEmpty()) {
            db.collection("users")
                    .whereEqualTo("deviceId", deviceId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if (user != null) {
                                    isAdmin = user.getIsadmin();
                                    CurrentUserHandler.getSingleton().loginWithDeviceId(null);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Checks the device ID and redirects the user to the appropriate activity based on their selected role.
     * If no user is found with the given device ID, the user is redirected to the sign-up page.
     *
     * @param selectedRole The role selected by the user (Admin, Organizer, Attendee).
     */
    private void checkDeviceIdAndRedirect(String selectedRole) {
        String deviceId = userViewModel.getDeviceId();

        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(this, "Device ID not available.", Toast.LENGTH_SHORT).show();
            navigateToSignupPage(selectedRole);
            return;
        }

        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                isAdmin = user.getIsadmin();
                                CurrentUserHandler.getSingleton().loginWithDeviceId(() ->
                                        redirectToRoleBasedActivity(selectedRole, user)
                                );
                                return;
                            }
                        }
                        navigateToSignupPage(selectedRole);
                    } else {
                        navigateToSignupPage(selectedRole);
                    }
                })
                .addOnFailureListener(e -> {
                    navigateToSignupPage(selectedRole);
                });
    }

    /**
     * Redirects the user to the appropriate activity based on their role.
     *
     * @param role The role of the user (Admin, Organizer, Attendee).
     * @param user The user object containing user details.
     */
    private void redirectToRoleBasedActivity(String role, User user) {
        Intent intent = null;
        switch (role) {
            case "Admin":
                if (user.getIsadmin()) {
                    intent = new Intent(this, AdminHome.class);
                }
                break;
            case "Organizer":
                intent = new Intent(this, OrganizerHome.class);
                break;
            case "Attendee":
                intent = new Intent(this, AttendeeHome.class);
                break;
            default:
                Toast.makeText(this, "Invalid role selected.", Toast.LENGTH_SHORT).show();
                return;
        }

        if (intent != null) {
            intent.putExtra("deviceId", userViewModel.getDeviceId());
            startActivity(intent);
            finish();
        }
    }

    /**
     * Navigates the user to the sign-up page, passing the selected role as an extra.
     *
     * @param role The role of the user (Admin, Organizer, Attendee).
     */
    private void navigateToSignupPage(String role) {
        Intent intent = new Intent(this, SignUp.class);
        if (role != null) {
            intent.putExtra("role", role);
        }
        startActivity(intent);
        finish();
    }

    /**
     * Sets up the role selection buttons and their corresponding click listeners.
     */
    private void setupRoleSelection() {
        adminButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Admin"));
        organizerButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Organizer"));
        attendeeButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Attendee"));
    }
}