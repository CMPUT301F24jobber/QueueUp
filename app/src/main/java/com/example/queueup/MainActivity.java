package com.example.queueup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    private MaterialButton adminButton;
    private MaterialButton organizerButton;
    private MaterialButton attendeeButton;
    private UserViewModel userViewModel;
    private FirebaseFirestore db;
    String role = "";

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

        // **Initialize CurrentUserHandler Singleton**
        CurrentUserHandler.setOwnerActivity(this);
        CurrentUserHandler.getSingleton(); // Ensure singleton is initialized

        // Observe the current user to update UI accordingly
        observeCurrentUser();

        // Set up role selection buttons
        setupRoleSelection();

        // Handle window insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Observes the current user and updates the admin button's visibility based on isAdmin flag.
     */
    private void observeCurrentUser() {
        CurrentUserHandler.getSingleton().getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null && user.getIsadmin()) {
                    adminButton.setEnabled(true);
                    adminButton.setVisibility(MaterialButton.VISIBLE);

                }
            }
        });
    }

    /**
     * Checks if the device ID exists in Firestore and redirects the user accordingly.
     *
     * @param selectedRole The role selected by the user (Admin, Organizer, Attendee).
     */
    private void checkDeviceIdAndRedirect(String selectedRole) {
        String deviceId = userViewModel.getDeviceId();  // Get the device ID from the ViewModel

        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(this, "Device ID not available.", Toast.LENGTH_SHORT).show();
            navigateToSignupPage(selectedRole);
            return;
        }

        // Query Firestore for a user with the matching deviceId and role
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Device ID found in Firestore
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if (user != null) {
                                    // Initialize user session using CurrentUserHandler
                                    CurrentUserHandler.getSingleton().loginWithDeviceId(() -> {
                                        redirectToRoleBasedActivity(selectedRole, user);
                                    });
                                    return; // Exit after handling the first valid document
                                }
                            }
                            // If user object is not properly retrieved
                            Toast.makeText(MainActivity.this, "User data is corrupted.", Toast.LENGTH_SHORT).show();
                            navigateToSignupPage(selectedRole);
                        } else {
                            // Device ID and role combination not found
                            Toast.makeText(MainActivity.this, "Device not registered for the selected role. Please sign up.", Toast.LENGTH_SHORT).show();
                            navigateToSignupPage(selectedRole);
                        }
                    } else {
                        Log.e("MainActivity", "Error checking device ID: ", task.getException());
                        Toast.makeText(MainActivity.this, "Error checking device ID", Toast.LENGTH_SHORT).show();
                        navigateToSignupPage(selectedRole);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error checking device ID: ", e);
                    Toast.makeText(MainActivity.this, "Error checking device ID", Toast.LENGTH_SHORT).show();
                    navigateToSignupPage(selectedRole);
                });
    }

    /**
     * Redirect to the activity based on user role.
     *
     * @param role The role of the user (Admin, Organizer, Attendee).
     * @param user The current user object.
     */
    private void redirectToRoleBasedActivity(String role, User user) {
        if ("Admin".equals(role) && user.getIsadmin()) {
            Intent intent = new Intent(this, AdminHome.class);
            intent.putExtra("deviceId", userViewModel.getDeviceId());
            startActivity(intent);
            finish(); // Close MainActivity to prevent going back
        } else if ("Organizer".equals(role)) {
            Intent intent = new Intent(this, OrganizerHome.class);
            intent.putExtra("deviceId", userViewModel.getDeviceId());
            startActivity(intent);
            finish();
        } else if ("Attendee".equals(role)) {
            Intent intent = new Intent(this, AttendeeHome.class);
            intent.putExtra("deviceId", userViewModel.getDeviceId());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid role selected or insufficient permissions.", Toast.LENGTH_SHORT).show();
            navigateToSignupPage(role);
        }
    }


    /**
     * Navigates to the signup page, passing the selected role (if any).
     *
     * @param role The role selected by the user (Admin, Organizer, Attendee).
     */
    private void navigateToSignupPage(String role) {
        Intent intent = new Intent(this, SignUp.class);
        if (role != null) {
            intent.putExtra("role", role);
        }
        startActivity(intent);
        finish(); // Close MainActivity
    }

    /**
     * Sets up button click listeners for role selection and navigation to the signup page.
     */
    private void setupRoleSelection() {
        adminButton.setOnClickListener(v -> {
            CurrentUserHandler.getSingleton().getCurrentUser().observe(this, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null && user.getIsadmin()) {
                        checkDeviceIdAndRedirect("Admin");
                    } else {
                        Toast.makeText(MainActivity.this, "You do not have admin privileges.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        organizerButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Organizer"));

        attendeeButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Attendee"));
    }
}
