package com.example.queueup.views.profiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class EditProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private User currentUser;
    private Uri imageUri;

    private EditText editFirstName, editLastName, editUsername, editEmail, editPhone;
    private ImageView profileImageView;
    private TextView profileInitialsTextView;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_profile, container, false);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        profileImageView = view.findViewById(R.id.profileImageView);
        profileInitialsTextView = view.findViewById(R.id.profileInitialsTextView);
        editFirstName = view.findViewById(R.id.editFirstName);
        editLastName = view.findViewById(R.id.editLastName);
        editUsername = view.findViewById(R.id.editUsername);
        editEmail = view.findViewById(R.id.editEmail);
        editPhone = view.findViewById(R.id.editPhone);

        Button saveButton = view.findViewById(R.id.saveButton);
        Button editPicButton = view.findViewById(R.id.editPicButton);
        Button removePicButton = view.findViewById(R.id.removePicButton);

        saveButton.setOnClickListener(v -> saveProfileChanges());
        editPicButton.setOnClickListener(v -> selectImage());
        removePicButton.setOnClickListener(v -> removeProfilePicture());

        loadUserData();
        return view;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private void loadUserData() {
        // Logic to fetch and display user data
    }

    private void saveProfileChanges() {
        // Logic to save profile changes
    }

    private void removeProfilePicture() {
        // Logic to remove profile picture
    }
}
