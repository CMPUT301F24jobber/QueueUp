package com.example.queueup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.queueup.views.SignUp;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private MaterialButton adminButton;
    private MaterialButton organizerButton;
    private MaterialButton attendeeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);


        adminButton = findViewById(R.id.adminButton);
        organizerButton = findViewById(R.id.organizerButton);
        attendeeButton = findViewById(R.id.attendeeButton);

        // Set up role selection buttons
        setupRoleSelection();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Sets up button click listeners for role selection and navigation to the signup page.
     */
    private void setupRoleSelection() {
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignupPage("Admin");
            }
        });

        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignupPage("Organizer");
            }
        });

        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignupPage("Attendee");
            }
        });
    }

    /**
     * Navigates to the signup page, passing the selected role.
     *
     * @param role The selected role (Admin, Organizer, Attendee).
     */
    private void navigateToSignupPage(String role) {
        Intent intent = new Intent(this, SignUp.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }
}
