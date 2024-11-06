package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;

public class AdminUserFragment extends Fragment {
    public AdminUserFragment() {
        super(R.layout.admin_user_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView displayName = view.findViewById(R.id.display_name);
        TextView userName = view.findViewById(R.id.username);
        TextView emailText = view.findViewById(R.id.email);
        TextView phoneText = view.findViewById(R.id.phone_number);
        User user = this.getArguments().getParcelable("user", User.class);

        displayName.setText(user.getFullName());
        userName.setText("@"+user.getUsername());
        emailText.setText("Email: " + user.getEmailAddress());
        phoneText.setText("Phone: " + user.getPhoneNumber());
    }

}