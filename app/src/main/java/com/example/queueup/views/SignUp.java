package com.example.queueup.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.MainActivity;
import com.example.queueup.R;
import com.example.queueup.models.User;
import com.example.queueup.services.ImageUploader;
import com.example.queueup.viewmodels.UserViewModel;
import com.example.queueup.views.admin.AdminHome;
import com.example.queueup.views.attendee.AttendeeHome;
import com.example.queueup.views.organizer.OrganizerHome;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.UUID;

/**
 * SignUp activity allows new users to register by providing their details, uploading a profile picture,
 * and selecting their role. It collects the user's information, validates it, and stores the data in Firestore.
 */
public class SignUp extends AppCompatActivity {

    private TextView titleTextView;
    private TextView subtitleTextView;
    private TextInputLayout firstNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout phoneNumberInputLayout;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout passwordInputLayout;
    private MaterialButton submitButton;
    private ImageView profileImageView;
    private Button uploadImageButton;
    private Uri profileImageUri;
    private ImageButton backButton;
    private UserViewModel userViewModel;

    private static final int IMAGE_PICK_REQUEST_CODE = 100;


    private String role;

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
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.back_button);

        profileImageView = findViewById(R.id.profilePicImage);
        uploadImageButton = findViewById(R.id.profilePicButton);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Set text programmatically
        titleTextView.setText("Your Information");
        subtitleTextView.setText("Please enter your details");

        // Get role from intent inside onCreate
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("role")) {
            role = intent.getStringExtra("role");
        } else {
            role = null;
            Toast.makeText(SignUp.this, "Role not specified. Redirecting to main screen.", Toast.LENGTH_SHORT).show();
            // Redirect to main activity or handle accordingly
            Intent mainIntent = new Intent(SignUp.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
            return;
        }

        if (role.equals("Admin")) {
            passwordInputLayout.setVisibility(View.VISIBLE);
        } else {
            passwordInputLayout.setVisibility(View.GONE);
        }

        // Set up back button click listener
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up image upload button click listener
        uploadImageButton.setOnClickListener(v -> selectImage());

        // Set up submit button click listener
        submitButton.setOnClickListener(v -> submitUserInformation());
    }

    /**
     * Launches an intent to select an image from the device.
     */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            profileImageUri = data.getData();
            profileImageView.setImageURI(profileImageUri); // Display chosen image
        }
    }

    /**
     * Collects user information and submits it to Firestore.
     */
    private void submitUserInformation() {
        // Get input values from TextInputEditText fields
        EditText firstNameEditText = firstNameInputLayout.getEditText();
        EditText lastNameEditText = lastNameInputLayout.getEditText();
        EditText emailEditText = emailInputLayout.getEditText();
        EditText phoneNumberEditText = phoneNumberInputLayout.getEditText();
        EditText usernameEditText = usernameInputLayout.getEditText();

        if (firstNameEditText == null || lastNameEditText == null || emailEditText == null ||
                phoneNumberEditText == null || usernameEditText == null) {
            Toast.makeText(SignUp.this, "Unexpected error: Missing input fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        Boolean isadmin = false;
        String password = passwordInputLayout.getEditText().getText().toString().trim();

        if(role.equals("Admin") && !password.equals("123456")) {
            Toast.makeText(SignUp.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || username.isEmpty()) {
            Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d{10}")) {
            Toast.makeText(SignUp.this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignUp.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (role.equals("Admin")) {
            isadmin = true;
        }

        // Create a new user object
        String userId = UUID.randomUUID().toString();
        String deviceId = userViewModel.getDeviceId();

        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(SignUp.this, "Device ID not available.", Toast.LENGTH_SHORT).show();
            // You might want to handle this case appropriately
            return;
        }

        User user = new User(firstName, lastName, username, email, phoneNumber, deviceId, isadmin);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                user.setFCMToken(task.getResult());
            } else {
                Toast.makeText(SignUp.this, "Failed to get FCM token: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Check if a profile image is selected
        if (profileImageUri != null) {
            // Upload image to Firebase Storage
            ImageUploader imageUploader = new ImageUploader();
            imageUploader.uploadImage("profile_pictures/", profileImageUri, new ImageUploader.UploadListener() {
                @Override
                public void onUploadSuccess(String imageUrl) {
                    user.setProfileImageUrl(imageUrl);
                    proceedToSaveUser(user);
                }

                @Override
                public void onUploadFailure(Exception exception) {
                    Toast.makeText(SignUp.this, "Failed to upload profile picture: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If no image, proceed without a profile picture
            proceedToSaveUser(user);
        }
    }

    /**
     * Saves the user to Firestore and handles post-save actions.
     *
     * @param user The user object to save.
     */
    private void proceedToSaveUser(User user) {
        userViewModel.createUser(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                // Redirect based on role
                redirectToRoleBasedActivity(role, user);
            } else {
                Toast.makeText(SignUp.this, "Failed to register user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Redirects to the appropriate activity based on the user's role.
     *
     * @param role The role of the user (Admin, Organizer, Attendee).
     * @param user The user object containing user details.
     */
    private void redirectToRoleBasedActivity(String role, User user) {
        Intent intent;
        switch (role) {
            case "Admin":
                intent = new Intent(SignUp.this, AdminHome.class); // Navigate to AdminHome
                break;
            case "Organizer":
                intent = new Intent(SignUp.this, OrganizerHome.class); // Navigate to OrganizerHome
                break;
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
