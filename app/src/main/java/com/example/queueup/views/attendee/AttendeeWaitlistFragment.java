package com.example.queueup.views.attendee;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.services.LocationService;
import com.google.android.gms.tasks.Task;

public class AttendeeWaitlistFragment extends Fragment {
    private Button joinWaitlistButton;
    private EventController eventController;
    private AttendeeController attendeeController;
    private LocationService locationService;
    private Event event;
    private CurrentUserHandler currentUserHandler;

    public AttendeeWaitlistFragment() {
        super(R.layout.attendee_join_waitlist_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        joinWaitlistButton = view.findViewById(R.id.join_waitlist);
        event = this.getArguments().getSerializable("event", Event.class);
        attendeeController = AttendeeController.getInstance();
        eventController = EventController.getInstance();
        currentUserHandler = CurrentUserHandler.getSingleton();
        LocationManager locationManager = (LocationManager) requireActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        locationService = new LocationService(requireContext(), requireActivity(), locationManager);

        if(event.getIsGeoLocationRequried()) {
            Toast.makeText(getContext(), "Geolocation is required for this event.", Toast.LENGTH_LONG).show();
        }

        joinWaitlistButton.setOnClickListener((v) -> handleJoinWaitlist());
    }

    private void handleJoinWaitlist() {
        if (event.getIsGeoLocationRequried()) {
            locationService.setLocationCallback(new LocationService.LocationCallback() {
                @Override
                public void onLocationReceived(Location location) {
                    joinWithLocation(location);
                }

                @Override
                public void onLocationError(String error) {
                    Toast.makeText(getContext(),
                            "Location required: " + error, Toast.LENGTH_LONG).show();
                }
            });

            locationService.handleLocationPermissions();
            locationService.getLocation();
        } else {
            joinWithLocation(null);
        }
    }

    private void joinWithLocation(Location location) {
        attendeeController.joinWaitingList(currentUserHandler.getCurrentUserId(), event.getEventId(), location)
                .addOnSuccessListener(task -> {
                    joinWaitlistButton.setVisibility(View.INVISIBLE);
                    navigateToJoinedFragment();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to join waitlist: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void navigateToJoinedFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.attendee_event_fragment, AttendeeWaitlistJoinedFragment.class, bundle)
                .commit();
    }
}