package com.example.queueup.views.profiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private User currentUser;

    private EditText editFirstName, editLastName, editUsername, editEmail, editPhone;
    private ImageView profileImageView;
    private TextView profileInitialsTextView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Retrieve the User object passed from AttendeeProfileActivity
        currentUser = getIntent().getParcelableExtra("user");

        if (currentUser == null) {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firestore, Firebase Storage, and UI components
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

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

        // Load the user data into the fields and display profile image or initials
        loadUserData();

        // Set up button listeners
        saveButton.setOnClickListener(v -> saveProfileChanges());
        removePicButton.setOnClickListener(v -> removeProfilePicture());
        editPicButton.setOnClickListener(v -> selectImage());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d("EditProfileActivity", "Selected Image URI: " + imageUri.toString());

            // Display the selected image immediately
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUri).circleCrop().into(profileImageView);

            Toast.makeText(this, "Image selected. Save changes to apply permanently.", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeProfilePicture() {
        if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
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
     * Loads the current user's data into the EditText fields and displays profile image or initials.
     */
    private void loadUserData() {
        if (currentUser != null) {
            editFirstName.setText(currentUser.getFirstName());
            editLastName.setText(currentUser.getLastName());
            editUsername.setText(currentUser.getUsername());
            editEmail.setText(currentUser.getEmailAddress());
            editPhone.setText(currentUser.getPhoneNumber());
            displayProfileImageOrInitials();  // Display image or initials after loading user data
        }
    }

    /**
     * Displays the profile image if available; otherwise, displays user initials.
     */
    private void displayProfileImageOrInitials() {
        if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
            profileInitialsTextView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentUser.getProfileImageUrl()).circleCrop().into(profileImageView);
        } else {
            profileImageView.setVisibility(View.GONE);
            profileInitialsTextView.setVisibility(View.VISIBLE);

            // Display initials if no profile image
            String initials = (currentUser.getFirstName() != null ? currentUser.getFirstName().substring(0, 1) : "") +
                    (currentUser.getLastName() != null ? currentUser.getLastName().substring(0, 1) : "");
            profileInitialsTextView.setText(initials);
        }
    }

    /**
     * Saves the profile changes made by the user and updates Firestore.
     */
    private void saveProfileChanges() {
        currentUser.setFirstName(editFirstName.getText().toString().trim());
        currentUser.setLastName(editLastName.getText().toString().trim());
        currentUser.setUsername(editUsername.getText().toString().trim());
        currentUser.setEmailAddress(editEmail.getText().toString().trim());
        currentUser.setPhoneNumber(editPhone.getText().toString().trim());

        if (imageUri != null) {
            StorageReference fileReference = storage.getReference("profileImages").child(currentUser.getUuid() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                Log.d("EditProfileActivity", "Download URL: " + downloadUrl);
                                currentUser.setProfileImageUrl(downloadUrl);
                                updateFirestoreUser();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("EditProfileActivity", "Failed to get image URL", e);
                                Toast.makeText(EditProfileActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e -> {
                        Log.e("EditProfileActivity", "Failed to upload profile picture", e);
                        Toast.makeText(EditProfileActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                    });
        } else {
            updateFirestoreUser();
        }
    }

    private void updateFirestoreUser() {
        db.collection("users").document(currentUser.getUuid()).set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EditProfileActivity", "Firestore updated successfully");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedUser", currentUser);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> Log.e("EditProfileActivity", "Failed to update Firestore", e));
    }
}
