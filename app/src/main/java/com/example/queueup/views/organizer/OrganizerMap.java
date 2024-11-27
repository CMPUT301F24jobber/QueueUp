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
import com.example.queueup.models.GeoLocation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class OrganizerMap extends AppCompatActivity {
    private static final String TAG = "OrganizerMap";
    private MapView map;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final double DEFAULT_LAT = 0.0;
    private static final double DEFAULT_LON = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request permissions
        checkPermissions();

        // Initialize map configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.map_fragment);

        // Get user from intent
        User selectedUser = getIntent().getParcelableExtra("selected_user");

        // Initialize map
        initializeMap();

        // Add marker if user exists
        if (selectedUser != null) {
            Log.d(TAG, "Selected user: " + selectedUser.getFullName());
            GeoLocation location = selectedUser.getGeoLocation();
            if (location != null) {
                Log.d(TAG, "Location found: " + location.getLatitude() + ", " + location.getLongitude());
                addUserMarker(selectedUser);
            } else {
                Log.e(TAG, "No location data for user");
                Toast.makeText(this, "No location data available", Toast.LENGTH_SHORT).show();
            }
        }

        // Setup back button
        FloatingActionButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initializeMap() {
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // Set default view initially
        GeoPoint defaultPoint = new GeoPoint(DEFAULT_LAT, DEFAULT_LON);
        map.getController().setCenter(defaultPoint);
        map.getController().setZoom(15.0);
    }

    private void addUserMarker(User user) {
        GeoLocation location = user.getGeoLocation();
        if (location != null) {
            // Get coordinates directly as doubles
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Log.d(TAG, "Setting marker at: " + latitude + ", " + longitude);

            // Create marker at user's location
            GeoPoint point = new GeoPoint(latitude, longitude);

            // Center map on point
            map.getController().setCenter(point);
            map.getController().setZoom(15.0);

            // Add marker
            Marker marker = new Marker(map);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(user.getFullName());

            // Clear any existing markers and add new one
            map.getOverlays().clear();
            map.getOverlays().add(marker);

            // Refresh the map
            map.invalidate();
        }
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
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                needsPermission = true;
                break;
            }
        }

        if (needsPermission) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null) {
            map.onDetach();
        }
    }
}