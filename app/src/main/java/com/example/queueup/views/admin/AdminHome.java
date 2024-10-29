package com.example.queueup.views.admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminHome extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView titleTextView;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        db = FirebaseFirestore.getInstance();

        // Get deviceId from intent or UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());  // Fetch from UserController if not passed
        }
        if (savedInstanceState == null) {


            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.admin_activity_fragment, AdminHomeFragment.class, null)
                    .commit();
        }
    }


}