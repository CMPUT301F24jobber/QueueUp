package com.example.queueup.views.admin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * DialogFragment that displays user details and allows an admin to delete a user.
 * This fragment is used to view the details of a user and remove them from the database.
 * The fragment communicates with its parent fragment to refresh the list of users after deletion.
 */
public class AdminClickUserFragment extends DialogFragment {

    private TextView userName, userEmail, userPhone, userRole;
    private FirebaseFirestore db;
    private RefreshUsersListener listener;

    /**
     * Creates the dialog and sets up the user details for viewing.
     *
     * @param savedInstanceState The saved instance state.
     * @return A Dialog object representing the user details dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.admin_clicks_user, null);

        db = FirebaseFirestore.getInstance();

        User user = getArguments() != null ? getArguments().getParcelable("user") : null;

        if (user == null) {
            Log.e("AdminClickUserFragment", "User data is null");
            return super.onCreateDialog(savedInstanceState);
        }

        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        userPhone = view.findViewById(R.id.user_phone);
        userRole = view.findViewById(R.id.user_role);

        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        userName.setText(String.format("%s %s", firstName, lastName).trim());

        userEmail.setText(user.getEmailAddress() != null ? user.getEmailAddress() : "");
        userPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        userRole.setText(user.getIsadmin() ? "Admin" : "User");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("User Details")
                .setNeutralButton("Exit View", null)
                .setPositiveButton("Remove User", (dialog, which) -> {
                    deleteUser(user);
                    if (listener != null) {
                        listener.refreshFragment();
                    }
                });

        return builder.create();


    }

    /**
     * Attaches the fragment to its parent and ensures the parent implements the RefreshUsersListener.
     *
     * @param context The context of the fragment.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (RefreshUsersListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment must implement RefreshUsersListener");
        }
    }

    /**
     * Interface for the parent fragment to refresh the user list after a user is deleted.
     */
    interface RefreshUsersListener {
        void refreshFragment();
    }


    /**
     * Deletes a user from Firestore based on their email address.
     * Displays appropriate toast messages based on success or failure.
     *
     * @param user The user to delete.
     */
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
                                    dismiss(); // test to see if dialog closes and app doesn't crash when deleting user
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