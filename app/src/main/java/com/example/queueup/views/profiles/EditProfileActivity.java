package com.example.queueup.views.profiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private User currentUser;

    private EditText editFirstName, editLastName, editUsername, editEmail, editPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Retrieve the User object passed from AttendeeProfileActivity
        currentUser = getIntent().getParcelableExtra("user");

        if (currentUser == null) {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            finish();  // Exit the activity if data is missing
            return;
        }

        // Initialize Firestore and UI elements, then load data
        db = FirebaseFirestore.getInstance();
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        Button saveButton = findViewById(R.id.saveButton);

        // Load the user data into the fields
        loadUserData();

        // Set up the save button
        saveButton.setOnClickListener(v -> saveProfileChanges());
    }


    /**
     * Loads the current user's data into the EditText fields.
     */
    private void loadUserData() {
        if (currentUser != null) {
            editFirstName.setText(currentUser.getFirstName());
            editLastName.setText(currentUser.getLastName());
            editUsername.setText(currentUser.getUsername());
            editEmail.setText(currentUser.getEmailAddress());
            editPhone.setText(currentUser.getPhoneNumber());
        }
    }

    /**
     * Saves the profile changes made by the user and updates Firestore.
     */
    private void saveProfileChanges() {
        // Update the currentUser object with the new values
        currentUser.setFirstName(editFirstName.getText().toString().trim());
        currentUser.setLastName(editLastName.getText().toString().trim());
        currentUser.setUsername(editUsername.getText().toString().trim());
        currentUser.setEmailAddress(editEmail.getText().toString().trim());
        currentUser.setPhoneNumber(editPhone.getText().toString().trim());

        // Update Firestore with the new data
        db.collection("users").document(currentUser.getUuid()).set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    // Return the updated user object to the calling activity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedUser", currentUser);
                    setResult(RESULT_OK, resultIntent);

                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity and return to the profile view
                })
                .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
}
