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

import com.example.queueup.viewmodels.UserViewModel;
import com.example.queueup.views.SignUp;
import com.example.queueup.views.admin.AdminHome;
import com.example.queueup.views.attendee.AttendeeHome;
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

        adminButton = findViewById(R.id.adminButton);
        organizerButton = findViewById(R.id.organizerButton);
        attendeeButton = findViewById(R.id.attendeeButton);

        // Set up role selection buttons
        setupRoleSelection();

        // Auto-redirect based on device ID
        checkDeviceIdAndRedirect();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Checks if the device ID exists in Firestore and redirects the user accordingly.
     */
    private void checkDeviceIdAndRedirect() {
        String deviceId = userViewModel.getDeviceId();  // Get the device ID from the ViewModel

        // Check Firestore for a document with the current device ID
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Device ID found in Firestore
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String role = document.getString("role");
                            Log.d("MainActivity", "Device ID found with role: " + role);
                            if (role != null) {
                                redirectToRoleBasedActivity(role);
                            } else {
                                Toast.makeText(MainActivity.this, "User role not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error checking device ID: ", e);
                    Toast.makeText(MainActivity.this, "Error checking device ID", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Redirect to the activity based on user role.
     *
     * @param role The role of the user (Admin, Organizer, Attendee).
     */
    private void redirectToRoleBasedActivity(String role) {
        Intent intent = null;
        switch (role) {
            case "Admin":
                intent = new Intent(MainActivity.this, AdminHome.class);
                intent.putExtra("deviceId", userViewModel.getDeviceId());
                break;
//            case "Organizer":
//                intent = new Intent(MainActivity.this, OrganizerHome.class);
//                break;
            case "Attendee":
            default:
                intent = new Intent(MainActivity.this, AttendeeHome.class);
                intent.putExtra("deviceId", userViewModel.getDeviceId());
                break;
        }
        startActivity(intent);
        finish(); // Close MainActivity to prevent going back
    }

    /**
     * Navigates to the signup page, passing the selected role (if any).
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
        adminButton.setOnClickListener(v -> navigateToSignupPage("Admin"));

        organizerButton.setOnClickListener(v -> navigateToSignupPage("Organizer"));

        attendeeButton.setOnClickListener(v -> navigateToSignupPage("Attendee"));
    }
}