package com.example.queueup.views.organizer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class OrganizerMap extends AppCompatActivity {
    private MapView map;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final double DEFAULT_LAT = 0.0; // Default latitude
    private static final double DEFAULT_LON = 0.0; // Default longitude

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request permissions
        checkPermissions();

        // Initialize map configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.map_fragment);

        // Get user from intent
        User selectedUser = getIntent().getParcelableExtra("selected_user");

        // Initialize map
        setupMap(selectedUser);

        // Set up back button
        FloatingActionButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        };

        boolean needsPermission = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                needsPermission = true;
                break;
            }
        }

        if (needsPermission) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void setupMap(User selectedUser) {
        // Initialize map view
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // Get location from user or use default
        double latitude = DEFAULT_LAT;
        double longitude = DEFAULT_LON;

        if (selectedUser != null && selectedUser.getGeoLocation() != null) {
            try {
                latitude = selectedUser.getGeoLocation().getLatitude();
                longitude = selectedUser.getGeoLocation().getLongitude();
                Log.d("Map", "Using user location: " + latitude + ", " + longitude);
            } catch (NumberFormatException e) {
                Log.e("Map", "Error parsing location, using default", e);
                Toast.makeText(this, "Error reading location, using default", Toast.LENGTH_SHORT).show();
            }
        }

        // Create point and center map
        GeoPoint point = new GeoPoint(latitude, longitude);
        map.getController().setCenter(point);
        map.getController().setZoom(15.0);

        // Add marker
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        if (selectedUser != null) {
            marker.setTitle(selectedUser.getFullName());
        }

        map.getOverlays().add(marker);
        map.invalidate(); // Refresh map
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null) map.onDetach();
    }
}