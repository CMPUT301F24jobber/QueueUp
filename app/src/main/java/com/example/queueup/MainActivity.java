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

public class MainActivity extends AppCompatActivity {

    private MaterialButton adminButton;
    private MaterialButton organizerButton;
    private MaterialButton attendeeButton;
    private UserViewModel userViewModel;
    private FirebaseFirestore db;
    User user;
    private Boolean isAdmin = false;

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
        if (user != null && user.getIsadmin()) {
            isAdmin = false;
        } else {
            adminButton.setVisibility(View.INVISIBLE);
        }
        // Handle window insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

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
                                    if (isAdmin) {
                                        adminButton.setVisibility(View.VISIBLE);
                                    }
                                    CurrentUserHandler.getSingleton().loginWithDeviceId(null);
                                }
                            }
                        }
                    });
        }
    }


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

    private void navigateToSignupPage(String role) {
        Intent intent = new Intent(this, SignUp.class);
        if (role != null) {
            intent.putExtra("role", role);
        }
        startActivity(intent);
        finish();
    }

    private void setupRoleSelection() {
        adminButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Admin"));
        organizerButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Organizer"));
        attendeeButton.setOnClickListener(v -> checkDeviceIdAndRedirect("Attendee"));
    }
}