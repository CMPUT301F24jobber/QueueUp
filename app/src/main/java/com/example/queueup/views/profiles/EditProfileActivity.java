package com.example.queueup.views.profiles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.User;
import com.example.queueup.services.ImageUploader;
import com.example.queueup.viewmodels.UserViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


/**
 * Activity class for editing user profile information.

 * Allows the user to update their profile details, including first name, last name,
 * username, email, phone number, and profile picture. Provides options to save changes,
 * select a new profile picture, or remove the current picture.

 */
public class EditProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private User currentUser;
    private EditText editFirstName, editLastName, editUsername, editEmail, editPhone;
    private ImageView profileImageView;
    private TextView profileInitialsTextView;
    private ImageButton backButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;
    private Switch selectedSwitch, notSelectedSwitch, allNotificationSwitch;
    private String deviceId;
    private CurrentUserHandler currentUserHandler;
    private UserViewModel userViewModel;
    private boolean isImageRemoved = false;


    /**
     * Initializes the activity, setting up UI components and loading user data.
     *
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        currentUserHandler = currentUserHandler.getSingleton();
        deviceId = getIntent().getStringExtra("deviceId");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize UI components
        profileImageView = findViewById(R.id.profileImageView);
        profileInitialsTextView = findViewById(R.id.profileInitialsTextView);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> {
            onBackPressed();
        });
        selectedSwitch = findViewById(R.id.notifSelectedSwitch);
        notSelectedSwitch = findViewById(R.id.notifNotSelectedSwitch);
        allNotificationSwitch = findViewById(R.id.notifAllSwitch);

        ImageButton saveButton = findViewById(R.id.saveButton);
        ImageButton editPicButton = findViewById(R.id.editPicButton);
        ImageButton removePicButton = findViewById(R.id.removePicButton);

        // Set up click listeners
        saveButton.setOnClickListener(v -> saveProfileChanges());
        editPicButton.setOnClickListener(v -> selectImage());
        removePicButton.setOnClickListener(v -> removeProfilePicture());

        if (currentUser == null) {
            fetchUserData();
        } else {
            loadUserData();
        }
    }

    /**
     * Fetches the current user's data using the ViewModel.
     */
    private void fetchUserData() {
        userViewModel = new UserViewModel(getApplication());
        userViewModel.loadUserByDeviceId(deviceId);
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                currentUser = user;
                loadUserData();
            }
        });
    }

    /**
     * Opens the gallery for selecting a profile picture.
     *
     */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of an image selection.
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param data        the returned data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            isImageRemoved = false;
            Glide.with(this).load(imageUri).circleCrop().into(profileImageView);

            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Removes the current profile picture and displays initials instead.
     */
    private void removeProfilePicture() {
        imageUri = null;
        isImageRemoved = true;
        profileImageView.setVisibility(View.GONE);
        profileInitialsTextView.setVisibility(View.VISIBLE);
        String initials = (currentUser.getFirstName() != null ? currentUser.getFirstName().substring(0, 1) : "") +
                (currentUser.getLastName() != null ? currentUser.getLastName().substring(0, 1) : "");
        profileInitialsTextView.setText(initials);
    }

    /**
     * Loads the current user's data into the UI components.
     */
    private void loadUserData() {
        if (currentUser != null) {
            editFirstName.setText(currentUser.getFirstName());
            editLastName.setText(currentUser.getLastName());
            editUsername.setText(currentUser.getUsername());
            editEmail.setText(currentUser.getEmailAddress());
            editPhone.setText(currentUser.getPhoneNumber());
            displayProfileImageOrInitials();
            selectedSwitch.setChecked(currentUser.isReceiveChosenNotifications());
            notSelectedSwitch.setChecked(currentUser.isReceiveNotChosenNotifications());
            allNotificationSwitch.setChecked(currentUser.isReceiveAllNotifications());
        }
    }

    /**
     * Displays either the user's profile image or their initials if no image is set.
     */
    private void displayProfileImageOrInitials() {
        if (currentUser.getProfileImageUrl() != null && !isImageRemoved) {
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(currentUser.getProfileImageUrl())
                    .circleCrop()
                    .into(profileImageView);
        } else {
            profileImageView.setVisibility(View.GONE);
            profileInitialsTextView.setVisibility(View.VISIBLE);
            String initials = (currentUser.getFirstName() != null ? currentUser.getFirstName().substring(0, 1) : "") +
                    (currentUser.getLastName() != null ? currentUser.getLastName().substring(0, 1) : "");
            profileInitialsTextView.setText(initials);
        }
    }

    /**
     * Saves the changes made to the user's profile.
     *
     */
    private void saveProfileChanges() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phoneNumber = editPhone.getText().toString().trim();
        String username = editUsername.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || username.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d{10}")) {
            Toast.makeText(EditProfileActivity.this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(EditProfileActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) return;

        // Update user data
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setUsername(username);
        currentUser.setEmailAddress(email);
        currentUser.setPhoneNumber(phoneNumber);
        currentUser.setReceiveChosenNotifications(selectedSwitch.isChecked());
        currentUser.setReceiveNotChosenNotifications(notSelectedSwitch.isChecked());
        currentUser.setReceiveAllNotifications(allNotificationSwitch.isChecked());

        // Handle profile picture changes
        if (imageUri != null) {
            // Upload new image
            ImageUploader imageUploader = new ImageUploader();
            imageUploader.uploadImage("profile_pictures/", imageUri, new ImageUploader.UploadListener() {
                @Override
                public void onUploadSuccess(String imageUrl) {
                    currentUser.setProfileImageUrl(imageUrl);
                    updateUserAndFinish();
                }

                @Override
                public void onUploadFailure(Exception exception) {
                    Toast.makeText(EditProfileActivity.this,
                            "Failed to upload profile picture: " + exception.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (isImageRemoved) {
            // Remove profile picture
            currentUser.setProfileImageUrl(null);
            updateUserAndFinish();
        } else {
            // No changes to profile picture
            updateUserAndFinish();
        }
    }

    /**
     * Updates the user's information in the data source and finishes the activity.
     */
    private void updateUserAndFinish() {
        currentUserHandler.updateUser(currentUser);
        finish();
    }
}