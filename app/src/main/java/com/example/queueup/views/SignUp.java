package com.example.queueup.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UserViewModel;
import com.example.queueup.views.admin.AdminHome;
import com.example.queueup.views.attendee.AttendeeHome;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;

import java.util.UUID;

public class SignUp extends AppCompatActivity {

    private TextView titleTextView;
    private TextView subtitleTextView;
    private TextInputLayout firstNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout phoneNumberInputLayout;
    private TextInputLayout usernameInputLayout;
    private MaterialButton submitButton;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_fragment);
        FirebaseApp.initializeApp(this);

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView);
        subtitleTextView = findViewById(R.id.subtitleTextView);
        firstNameInputLayout = findViewById(R.id.firstNameInputLayout);
        lastNameInputLayout = findViewById(R.id.lastNameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneNumberInputLayout = findViewById(R.id.phoneNumberInputLayout);
        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        submitButton = findViewById(R.id.submitButton);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Set text programmatically
        titleTextView.setText("Your Information");
        subtitleTextView.setText("Please enter your details");

        // Set up submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitUserInformation();
            }
        });
    }

    /**
     * Collect user information and submit it to Firestore.
     */
    private void submitUserInformation() {
        // Get input values from TextInputEditText fields
        EditText firstNameEditText = firstNameInputLayout.getEditText();
        EditText lastNameEditText = lastNameInputLayout.getEditText();
        EditText emailEditText = emailInputLayout.getEditText();
        EditText phoneNumberEditText = phoneNumberInputLayout.getEditText();
        EditText usernameEditText = usernameInputLayout.getEditText();

        if (firstNameEditText != null && lastNameEditText != null && emailEditText != null && phoneNumberEditText != null && usernameEditText != null) {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();

            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || username.isEmpty()) {
                Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get role from intent
            String role = getIntent().getStringExtra("role");
            if (role == null) {
                role = "Attendee"; // Default role if not provided
            }

            // Create a new user object
            String userId = UUID.randomUUID().toString();
            String deviceId = userViewModel.getDeviceId();
            User user = new User(firstName, lastName, username, email, phoneNumber, deviceId);
            user.setRole(role);

            // Save user to Firestore using UserViewModel
            String finalRole = role;
            userViewModel.createUser(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    // Redirect based on role
                    redirectToRoleBasedActivity(finalRole, user);
                } else {
                    Toast.makeText(SignUp.this, "Failed to register user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Redirect to the appropriate activity based on the user's role.
     */

    private void redirectToRoleBasedActivity(String role, User user) {
        Intent intent = null;
        switch (role) {
//            case "Admin":
//                intent = new Intent(SignUp.this, AdminHome.class); // Navigate to AdminHome
//                break;
//            case "Organizer":
//                intent = new Intent(SignUp.this, OrganizerHome.class); // Navigate to OrganizerHome
//                break;
            case "Attendee":
            default:
                intent = new Intent(SignUp.this, AttendeeHome.class); // Navigate to AttendeeHome
                intent.putExtra("deviceId", user.getDeviceId());
                break;
        }
        startActivity(intent);
        finish(); // Close the current activity
    }
}
