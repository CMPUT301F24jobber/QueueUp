package com.example.queueup.views.creatingEvent;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.queueup.R;

public class OrganizerHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_home_fragment);

        // Initialize the plus button
        ImageButton plusButton = findViewById(R.id.plusButton);

        // Set a click listener for the plus button
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OrganizerHome", "Plus button clicked!");
                // Navigate to EventDetailsActivity
                Intent intent = new Intent(OrganizerHome.this, EventDetailsActivity.class);
                startActivity(intent);
            }
        });
    }
}
