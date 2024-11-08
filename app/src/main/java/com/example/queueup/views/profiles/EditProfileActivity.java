package com.example.queueup.views.profiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * EditProfileActivity provides functionality for users to edit their profile information,
 * including updating their first name, last name, username, email, and phone number.
 * The activity also supports changing the user's profile picture by uploading it to Firebase Storage.
 */
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

    /**
     * Called when the activity is first created. Initializes UI components,
     * sets up click listeners for buttons, and retrieves user data if available.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise, it is null.
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

        Button saveButton = findViewById(R.id.saveButton);
        Button removePicButton = findViewById(R.id.removePicButton);
        Button editPicButton = findViewById(R.id.editPicButton);

        // Set up click listeners
        saveButton.setOnClickListener(v -> saveProfileChanges());
        removePicButton.setOnClickListener(v -> removeProfilePicture());
        editPicButton.setOnClickListener(v -> selectImage());

        if (currentUser == null) {
            fetchUserData();
        } else {
            loadUserData();
        }
    }

    /**
     * Fetches the current user data from Firestore based on the device ID.
     */
    private void fetchUserData() {
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            currentUser = document.toObject(User.class);
                            loadUserData();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Error fetching user data", e);
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    /**
     * Opens the device's gallery to select an image for the profile picture.
     */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image selection activity.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult().
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).circleCrop().into(profileImageView);
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Image selected. Save changes to apply permanently.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Removes the profile picture of the current user by updating the Firestore database.
     */
    private void removeProfilePicture() {
        if (currentUser != null && currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
            currentUser.setProfileImageUrl(null);
            db.collection("users").document(currentUser.getUuid())
                    .update("profileImageUrl", null)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfileActivity.this, "Profile picture removed successfully", Toast.LENGTH_SHORT).show();
                        displayProfileImageOrInitials();  // Update UI to show initials
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to remove profile picture", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No profile picture to remove", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads the current user data into the UI components for editing.
     */
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

    /**
     * Displays the profile image if available; otherwise, displays initials.
     */
    private void displayProfileImageOrInitials() {
        if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentUser.getProfileImageUrl()).circleCrop().into(profileImageView);
        } else {
            profileImageView.setVisibility(View.GONE);
            profileInitialsTextView.setVisibility(View.VISIBLE);
            String initials = (currentUser.getFirstName() != null ? currentUser.getFirstName().substring(0, 1) : "") +
                    (currentUser.getLastName() != null ? currentUser.getLastName().substring(0, 1) : "");
            profileInitialsTextView.setText(initials);
        }
    }

    /**
     * Saves changes made to the user profile, including updating the Firestore database and
     * uploading a new profile picture to Firebase Storage.
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

        // Handle profile picture upload
        if (imageUri != null) {
            StorageReference fileReference = storage.getReference("profileImages").child(currentUser.getUuid() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                currentUser.setProfileImageUrl(downloadUrl);
                                Glide.with(this).load(downloadUrl).circleCrop().into(profileImageView);
                                profileInitialsTextView.setVisibility(View.GONE);
                                profileImageView.setVisibility(View.VISIBLE);
                                currentUserHandler.updateUser(currentUser);
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show());
        } else {
            currentUserHandler.updateUser(currentUser);
            finish();
        }
    }
}
