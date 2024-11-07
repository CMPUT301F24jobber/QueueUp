package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminUserFragment extends Fragment {
    public AdminUserFragment() {
        super(R.layout.admin_user_fragment);
    }
    private FirebaseFirestore db;
    private AdminClickUserFragment.RefreshUsersListener listener;
    private MaterialButton deleteButton;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        deleteButton = view.findViewById(R.id.delete_button);

        TextView titleText = view.findViewById(R.id.profile_title);
        TextView displayName = view.findViewById(R.id.display_name);
        TextView userName = view.findViewById(R.id.username);
        TextView emailText = view.findViewById(R.id.email);
        TextView phoneText = view.findViewById(R.id.phone_number);
        User user = this.getArguments().getParcelable("user", User.class);

        titleText.setText(user.getUsername()+ "'s Profile");
        displayName.setText(user.getFullName());
        userName.setText("@"+user.getUsername());
        emailText.setText("Email: " + user.getEmailAddress());
        phoneText.setText("Phone: " + user.getPhoneNumber());
        deleteButton.setOnClickListener((v) -> {
            deleteUser(user);
            if (listener != null) {
                listener.refreshFragment();
            }
        });
    }
    private void deleteUser(User user) {
        String email = user.getEmailAddress();

        if (email == null) {
            if (isAdded()) {
                Toast.makeText(requireContext(), "User email is null, cannot delete", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        db.collection("users").whereEqualTo("emailAddress", email).limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "User not found in Firestore", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    querySnapshot.getDocuments().get(0).getReference().delete()
                            .addOnSuccessListener(aVoid -> {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                                    listener.refreshFragment(); // test to see if AdminUsersFragment is refreshed
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Failed to search for user", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}