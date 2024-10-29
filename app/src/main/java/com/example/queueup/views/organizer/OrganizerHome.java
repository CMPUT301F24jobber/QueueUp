package com.example.queueup.views.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.example.queueup.views.attendee.AttendeeHome;
import com.example.queueup.views.profiles.ProfileActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class OrganizerHome extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView titleTextView;
    private TextView profileInitialsTextView;
    private FrameLayout profileInitialsFrame;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_home_fragment);

        db = FirebaseFirestore.getInstance();

        // Initialize local variables for views
        TextView titleTextView = findViewById(R.id.organizerHomeText);
        ImageButton plusButton = findViewById(R.id.plusButton);
        profileInitialsTextView = findViewById(R.id.profileInitialsTextView);  // Find the initials TextView
        profileInitialsFrame = findViewById(R.id.profileInitialsFrame);

        // Get device ID from intent or from UserController
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());
        }



        profileInitialsFrame.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerHome.this, ProfileActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivity(intent);
        });


        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OrganizerHome", "Plus button clicked!");
            }
        });


        // Fetch user data and update the title text
        fetchUserData(titleTextView);
    }

    private void fetchUserData(TextView titleTextView) {
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Device ID found in Firestore
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            String firstName = user.getFirstName();

                            if (firstName != null && !firstName.isEmpty()) {
                                titleTextView.setText("Welcome, " + firstName + "!");
                            } else {
                                titleTextView.setText("Welcome, Organizer!");  // Fallback if first name is null
                            }
                        }
                    } else {
                        Log.d("OrganizerHome", "No user found with this device ID");
                        titleTextView.setText("Welcome, Organizer!");  // Fallback if no user document found
                    }
                })
                .addOnFailureListener(e -> Log.e("OrganizerHome", "Error fetching user data", e));
    }
}