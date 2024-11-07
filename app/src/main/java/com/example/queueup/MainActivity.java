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
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.handlers.CurrentUserHandler; // Import the handler
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

        // Set up role selection buttons
        setupRoleSelection();

        // **Removed Auto-Redirect to prevent immediate redirection on app launch**
        // checkDeviceIdAndRedirect(); // This line is removed

        // Handle window insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Checks if the device ID exists in Firestore and redirects the user accordingly.
     *
     * @param selectedRole The role selected by the user (Admin, Organizer, Attendee).
     */
    private synchronized void checkDeviceIdAndRedirect(String selectedRole) {
        String deviceId = userViewModel.getDeviceId();  // Get the device ID from the ViewModel

        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(this, "Device ID not available.", Toast.LENGTH_SHORT).show();
            navigateToSignupPage(selectedRole);
            return;
        }

        // Query Firestore for a user with the matching deviceId and role
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .whereEqualTo("role", selectedRole)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Device ID and role found in Firestore
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String role = document.getString("role");
                                Log.d("MainActivity", "Device ID found with role: " + role);
                                if (role != null) {
                                    // Initialize user session using CurrentUserHandler
                                    CurrentUserHandler.getSingleton().loginWithDeviceId(() -> {
                                        redirectToRoleBasedActivity(role);
                                    });
                                    return; // Exit after handling the first valid document
                                }
                            }
                            // If role is not found in the retrieved documents
                            Toast.makeText(MainActivity.this, "User role not found.", Toast.LENGTH_SHORT).show();
                            navigateToSignupPage(selectedRole);
                        } else {
                            // Device ID and role combination not found
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
     */
    private void redirectToRoleBasedActivity(String role) {
        Intent intent;
        switch (role) {
            case "Admin":
                intent = new Intent(MainActivity.this, AdminHome.class);
                break;
            case "Organizer":
                intent = new Intent(MainActivity.this, OrganizerHome.class);
                break;
            case "Attendee":
            default:
                intent = new Intent(MainActivity.this, AttendeeHome.class);
                break;
        }
        intent.putExtra("deviceId", userViewModel.getDeviceId());
        startActivity(intent);
        finish(); // Close MainActivity to prevent going back
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
        adminButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Admin"));

        organizerButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Organizer"));

        attendeeButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Attendee"));
    }
}
