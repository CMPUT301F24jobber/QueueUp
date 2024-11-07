package com.example.queueup.viewmodels;

import android.content.Context;
import android.os.Build;
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

public class UsersArrayAdapter extends ArrayAdapter<User> {
    public UsersArrayAdapter(Context context, ArrayList<User> user) {
        super(context, 0, user);
    }

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
        TextView userRole = view.findViewById(R.id.user_role);

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
        userRole.setText(user.getRole());

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