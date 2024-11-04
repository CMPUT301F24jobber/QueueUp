package com.example.queueup.views.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminClickUserFragment extends DialogFragment {

    private TextView userName, userEmail, userPhone, userRole;
    private Button deleteUserButton;
    private FirebaseFirestore db;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the custom view for the dialog
        View view = LayoutInflater.from(getContext()).inflate(R.layout.admin_clicks_user, null);

        db = FirebaseFirestore.getInstance();

        // Retrieve the Parcelable User object from arguments
        User user = getArguments() != null ? getArguments().getParcelable("user") : null;

        if (user == null) {
            Log.e("AdminClickUserFragment", "User data is null");
            return super.onCreateDialog(savedInstanceState); // Return a default dialog if user is null
        }

        // Initialize views using view.findViewById()
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        userPhone = view.findViewById(R.id.user_phone);
        userRole = view.findViewById(R.id.user_role);
        deleteUserButton = view.findViewById(R.id.delete_user_button);

        // Populate the views with user data
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        userName.setText(String.format("%s %s", firstName, lastName).trim());

        userEmail.setText(user.getEmailAddress() != null ? user.getEmailAddress() : "");
        userPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        userRole.setText(user.getRole() != null ? user.getRole() : "");

        // Set up delete button listener
        deleteUserButton.setOnClickListener(v -> deleteUser(user));

        // Build the dialog using AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("User Details")
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void deleteUser(User user) {
        String email = user.getEmailAddress();
        if (email != null) {
            db.collection("users").whereEqualTo("email", email).limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {
                            Toast.makeText(requireContext(), "User not found in Firestore", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        querySnapshot.getDocuments().get(0).getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                                    if (getActivity() != null) {
                                        getActivity().onBackPressed();
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(), "Failed to delete user", Toast.LENGTH_SHORT).show()
                                );
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Failed to search for user", Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(requireContext(), "User email is null, cannot delete", Toast.LENGTH_SHORT).show();
        }
    }
}