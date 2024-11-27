package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.queueup.R;
import com.example.queueup.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class OrganizerMap extends AppCompatActivity {
    private MapView mapView;
    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);

        // Get selected user from intent
        selectedUser = getIntent().getParcelableExtra("selected_user");

        // Configure OSMdroid
        Configuration.getInstance().load(
                this,
                PreferenceManager.getDefaultSharedPreferences(this)
        );

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        setupMap();

        // Setup back button
        FloatingActionButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void setupMap() {
        if (selectedUser != null && selectedUser.getGeoLocation() != null) {
            // Set up the map
            mapView.setMultiTouchControls(true);
            mapView.getController().setZoom(15.0);

            try {
                // Create location point from user coordinates
                GeoPoint userLocation = new GeoPoint(
                        selectedUser.getGeoLocation().getLatitude(),
                        selectedUser.getGeoLocation().getLongitude()
                );

                // Center map on user location
                mapView.getController().setCenter(userLocation);

                // Add marker for user location
                Marker userMarker = new Marker(mapView);
                userMarker.setPosition(userLocation);
                userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                userMarker.setTitle(selectedUser.getFullName());

                // Add marker to map
                mapView.getOverlays().add(userMarker);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}