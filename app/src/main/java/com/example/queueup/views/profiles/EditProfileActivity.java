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
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        deviceId = getIntent().getStringExtra("deviceId");

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

        saveButton.setOnClickListener(v -> saveProfileChanges());
        removePicButton.setOnClickListener(v -> removeProfilePicture());
        editPicButton.setOnClickListener(v -> selectImage());

        if (currentUser == null) {
            fetchUserData();
        } else {
            loadUserData();
        }
    }

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

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

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

    private void saveProfileChanges() {
        if (currentUser == null) return;

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
                                currentUser.setProfileImageUrl(downloadUrl);
                                Glide.with(this).load(downloadUrl).circleCrop().into(profileImageView);
                                profileInitialsTextView.setVisibility(View.GONE);
                                profileImageView.setVisibility(View.VISIBLE);
                                updateFirestoreUser();
                            })
                            .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show());
        } else {
            updateFirestoreUser();
        }
    }

    private void updateFirestoreUser() {
        db.collection("users").document(currentUser.getUuid()).set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> Log.e("EditProfileActivity", "Failed to update Firestore", e));
    }
}
