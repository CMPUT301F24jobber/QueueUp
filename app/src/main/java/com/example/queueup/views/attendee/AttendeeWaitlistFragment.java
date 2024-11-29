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
import com.example.queueup.controllers.UserController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.models.GeoLocation;
import com.example.queueup.services.LocationService;
import com.example.queueup.viewmodels.UserViewModel;

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
        if (!isAdded()) return;

        joinWaitlistButton = view.findViewById(R.id.join_waitlist);
        event = this.getArguments().getSerializable("event", Event.class);
        attendeeController = AttendeeController.getInstance();
        eventController = EventController.getInstance();
        currentUserHandler = CurrentUserHandler.getSingleton();

        if (event != null && event.getIsGeoLocationRequried() && getContext() != null) {
            Toast.makeText(getContext(), "Geolocation is required for this event.", Toast.LENGTH_LONG).show();
        }

        joinWaitlistButton.setOnClickListener((v) -> handleJoinWaitlist());
    }

    private void handleJoinWaitlist() {
        if (!isAdded()) return;

        if (event != null && event.getIsGeoLocationRequried()) {
            setupLocationService();
        } else {
            joinWithLocation(null);
        }
    }

    private void setupLocationService() {
            LocationManager locationManager = (LocationManager) requireActivity()
                    .getSystemService(Context.LOCATION_SERVICE);
            locationService = new LocationService(requireContext(), requireActivity(), locationManager);

            locationService.setLocationCallback(new LocationService.LocationCallback() {
                @Override
                public void onLocationReceived(Location location) {
                    if (isAdded()) {
                        joinWithLocation(location);
                    }
                }

                @Override
                public void onLocationError(String error) {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(),
                                "Location required: " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });

            locationService.handleLocationPermissions();
            locationService.getLocation();

    }

    private void joinWithLocation(Location location) {
        if (!isAdded() || event == null) return;

        eventController.registerToEvent(event.getEventId())
                .addOnSuccessListener(task -> {
                    // Then join waitlist with AttendeeController
                    attendeeController.joinWaitingList(
                                    currentUserHandler.getCurrentUserId()+event.getEventId(),
                                    event.getEventId(),
                                    location
                            )
                            .addOnSuccessListener(waitlistTask -> {
                                if (isAdded()) {
                                    joinWaitlistButton.setVisibility(View.INVISIBLE);
                                    navigateToJoinedFragment();
                                }
                            })
                            .addOnFailureListener(this::handleJoinError);
                    if (location != null) {
                    currentUserHandler.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
                        if (user != null) {
                            GeoLocation GeoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
                            user.setGeoLocation(GeoLocation);
                            UserController.getInstance().updateUserById(user.getUuid(), "geoLocation", GeoLocation);
                        }});
                    }

                    })
                    .addOnFailureListener(this::handleJoinError);

    }

    private void handleJoinError(Exception e) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(),
                    "Failed to join waitlist: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToJoinedFragment() {
        if (!isAdded()) return;

        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        getActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.attendee_event_fragment, AttendeeWaitlistJoinedFragment.class, bundle)
                .commit();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}