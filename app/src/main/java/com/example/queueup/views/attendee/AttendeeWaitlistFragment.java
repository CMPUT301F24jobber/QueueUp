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
import com.example.queueup.models.Attendee;
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

        joinWaitlistButton = view.findViewById(R.id.join_waitlist);
        event = this.getArguments().getSerializable("event", Event.class);
        attendeeController = AttendeeController.getInstance();
        eventController = EventController.getInstance();
        currentUserHandler = CurrentUserHandler.getSingleton();

        if (event != null && event.getIsGeoLocationRequried() && getContext() != null) {
            Toast.makeText(getContext(), "Geolocation is required for this event.", Toast.LENGTH_LONG).show();
        }

        if (event.getIsGeoLocationRequried()) setupLocationService();

        joinWaitlistButton.setOnClickListener((v) -> handleJoinWaitlist());
    }

    private void handleJoinWaitlist() {
        if (event.getIsGeoLocationRequried()) {
            joinWithLocation(locationService.getLocation());
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
    }

    private void joinWithLocation(Location location) {
        if (!isAdded() || event == null) return;
        Attendee attendee = new Attendee(currentUserHandler.getCurrentUserId(), event.getEventId());
        attendee.setStatus("waiting");
        if (location != null) {
            GeoLocation geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
            attendee.setLocation(geoLocation);
        }
        eventController.registerToEvent(event.getEventId())
                .addOnSuccessListener(task -> {
                    // Then join waitlist with AttendeeController
                        attendeeController.updateAttendance(attendee);
                    });
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        bundle.putSerializable("attendee", attendee);
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