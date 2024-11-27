package com.example.queueup.views.organizer;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.queueup.R;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.Event;
import com.example.queueup.models.GeoLocation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class OrganizerMap extends Fragment {
    private MapView mapView;
    private Attendee attendee;
    private Event event;
    private static final double DEFAULT_LAT = 53.5461; // Edmonton's latitude
    private static final double DEFAULT_LON = -113.4938; // Edmonton's longitude
    private static final int DEFAULT_ZOOM = 15;

    public OrganizerMap() {
        super(R.layout.map_fragment);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Initialize OSMDroid configuration
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get data from arguments
        if (getArguments() != null) {
            attendee = (Attendee) getArguments().getSerializable("attendee");
            event = (Event) getArguments().getSerializable("event");
        }

        // Initialize map view
        mapView = view.findViewById(R.id.mapView);
        setupMap();

        // Initialize back button
        FloatingActionButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupMap() {
        // Configure map settings
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Set initial position
        GeoPoint point;
        if (attendee != null && attendee.getLocation() != null) {
            GeoLocation location = attendee.getLocation();
            point = new GeoPoint(location.getLatitude(), location.getLongitude());
        } else {
            // Default to Edmonton if no location available
            point = new GeoPoint(DEFAULT_LAT, DEFAULT_LON);
        }

        mapView.getController().setZoom(DEFAULT_ZOOM);
        mapView.getController().setCenter(point);

        // Add marker for attendee location
        addMarker(point, "Attendee Location");
    }

    private void addMarker(GeoPoint location, String title) {
        Marker marker = new Marker(mapView);
        marker.setPosition(location);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}