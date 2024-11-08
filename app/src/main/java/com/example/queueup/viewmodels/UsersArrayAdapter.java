package com.example.queueup.viewmodels;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.models.User;
import com.example.queueup.views.admin.AdminUserFragment;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying user data in a list. This adapter handles the
 * display of user information such as name, phone number, email address, and profile image.
 * If no profile image is available, the user's initials are displayed instead.
 */
public class UsersArrayAdapter extends ArrayAdapter<User> {

    /**
     * Constructs a new UsersArrayAdapter.
     *
     * @param context the context of the application
     * @param user the list of User objects to be displayed
     */
    public UsersArrayAdapter(Context context, ArrayList<User> user) {
        super(context, 0, user);
    }

    /**
     * Gets the view for a user item in the list, inflating the layout and populating the
     * view with user details such as name, phone number, email, and profile image.
     *
     * @param position the position of the item within the adapter's data set
     * @param convertView a recycled view that can be reused (if available)
     * @param parent the parent view that the new view will be attached to
     * @return the view for the item at the specified position
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_user_content,
                    parent, false);
        } else {
            view = convertView;
        }
        User user = getItem(position);
        TextView userName = view.findViewById(R.id.user_name);
        TextView userPhone = view.findViewById(R.id.user_phone);
        TextView userEmail = view.findViewById(R.id.user_email);
        ImageView userImage = view.findViewById(R.id.user_image);

        if (user != null) {
            String firstName = user.getFirstName() != null ? user.getFirstName() : "";
            String lastName = user.getLastName() != null ? user.getLastName() : "";
            userName.setText(String.format("%s %s", firstName, lastName).trim());
        } else {
            userName.setText("");
        }
        assert user != null;
        userPhone.setText(user.getPhoneNumber());
        userEmail.setText(user.getEmailAddress());
        TextView userInitials = view.findViewById(R.id.user_initials);


        String profileImageUrl = user.getProfileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(getContext())
                    .load(profileImageUrl)
                    .circleCrop()
                    .into(userImage);
            userImage.setVisibility(View.VISIBLE);
            userInitials.setVisibility(View.GONE);
        } else {
            // if no profile pic then display initials
            userImage.setVisibility(View.GONE);
            userInitials.setVisibility(View.VISIBLE);

            String initials = "";
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                initials += user.getFirstName().substring(0, 1).toUpperCase();
            }
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                initials += user.getLastName().substring(0, 1).toUpperCase();
            }
            userInitials.setText(initials);
        }


        return view;
    }

}