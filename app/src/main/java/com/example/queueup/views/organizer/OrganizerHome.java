package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class OrganizerHome extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView titleTextView;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_home_fragment);

        db = FirebaseFirestore.getInstance();
        titleTextView = findViewById(R.id.organizerHomeText);

        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UserController.getInstance().getDeviceId(getApplicationContext());
        }

        fetchUserData();
    }
    private void fetchUserData() {
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // if device ID found in Firestore
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            String firstName = user.getFirstName();

                            if (firstName != null && !firstName.isEmpty()) {
                                titleTextView.setText("Welcome, " + firstName + "!");
                            } else {
                                titleTextView.setText("Welcome, Organizer!");  // Couldn't retrieve first name
                            }
                        }
                    } else {
                        Log.d("OrganizerHome", "No user found with this device ID");
                        titleTextView.setText("Welcome, Organizer!");  // if no user found
                    }
                })
                .addOnFailureListener(e -> Log.e("OrganizerHome", "Error fetching user data", e));
    }
}