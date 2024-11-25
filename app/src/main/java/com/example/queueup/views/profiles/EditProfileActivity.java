package com.example.queueup.views.profiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private User currentUser;
    private EditText editFirstName, editLastName, editUsername, editEmail, editPhone;
    private ImageView profileImageView;
    private TextView profileInitialsTextView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;
    private String deviceId;
    private CurrentUserHandler currentUserHandler;
    private UserViewModel userViewModel;
    private boolean isImageRemoved = false;

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

        Button saveButton = findViewById(R.id.saveButton);
        Button editPicButton = findViewById(R.id.editPicButton);
        Button removePicButton = findViewById(R.id.removePicButton);

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

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            isImageRemoved = false;
            profileImageView.setImageURI(imageUri);
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
        }
    }

    private void removeProfilePicture() {
        imageUri = null;
        isImageRemoved = true;
        profileImageView.setVisibility(View.GONE);
        profileInitialsTextView.setVisibility(View.VISIBLE);
        String initials = (currentUser.getFirstName() != null ? currentUser.getFirstName().substring(0, 1) : "") +
                (currentUser.getLastName() != null ? currentUser.getLastName().substring(0, 1) : "");
        profileInitialsTextView.setText(initials);
    }

    private void loadUserData() {
        if (currentUser != null) {
            editFirstName.setText(currentUser.getFirstName());
            editLastName.setText(currentUser.getLastName());
            editUsername.setText(currentUser.getUsername());
            editEmail.setText(currentUser.getEmailAddress());
            editPhone.setText(currentUser.getPhoneNumber());
            displayProfileImageOrInitials();
        }
    }

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

    private void updateUserAndFinish() {
        currentUserHandler.updateUser(currentUser);
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}