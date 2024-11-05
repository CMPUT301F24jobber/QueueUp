package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.google.android.material.button.MaterialButton;

public class AdminProfileFragment extends Fragment {
    public AdminProfileFragment() {
        super(R.layout.admin_profile);
    }

    private MaterialButton switchRole;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchRole = view.findViewById(R.id.switch_role);
        switchRole.setOnClickListener((v) -> {
            // GO TO SWITCH ROLE PAGE
        });

    }

}
