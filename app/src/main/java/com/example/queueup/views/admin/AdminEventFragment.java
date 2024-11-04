package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;

public class AdminEventFragment extends Fragment {
    public AdminEventFragment() {
        super(R.layout.admin_event_fragment);
    }

    ToggleButton qrToggle;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        User e = new User("e","e","e","e","e","e");
        qrToggle = getView().findViewById(R.id.delete_qr_button);
        qrToggle.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                qrToggle.setBackgroundResource(R.drawable.filled_button);
            } else {
                qrToggle.setBackgroundResource(R.drawable.hollow_button);
            }

        });
    }

}
