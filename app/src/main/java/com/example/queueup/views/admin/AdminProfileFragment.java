package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;

public class AdminProfileFragment extends Fragment {
    public AdminProfileFragment() {
        super(R.layout.admin_user_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        User e = new User("e","e","e","e","e","e");
    }

}
