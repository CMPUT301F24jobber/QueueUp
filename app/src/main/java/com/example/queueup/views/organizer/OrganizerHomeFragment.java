package com.example.queueup.views.organizer;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventArrayAdapter;
import com.example.queueup.viewmodels.EventViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrganizerHomeFragment extends Fragment {

    // Constructor for the fragment, loading the layout file
    public OrganizerHomeFragment() {
        super(R.layout.organizer_home_fragment);  // Use a layout specific to the organizer's home screen
    }

    private ListView eventList;
    private EventArrayAdapter eventAdapter;
    private EventViewModel eventViewModel;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        eventList = view.findViewById(R.id.organizer_event_list);

        // Initialize the adapter with an empty list
        eventAdapter = new EventArrayAdapter(view.getContext(), new ArrayList<>());
        eventList.setAdapter(eventAdapter);

        // Instantiate the ViewModel
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Observe LiveData for events by organizer
        eventViewModel.getEventsByOrganizerLiveData().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                eventAdapter.clear();
                if (events != null) {
                    eventAdapter.addAll(events);
                }
                eventAdapter.notifyDataSetChanged();
            }
        });

        // Observe LiveData for error messages
        eventViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    // Handle the error message (e.g., show a Toast or log it)
                    // Example: Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    // Since we're not adding new elements, you can log it instead
                    android.util.Log.e("OrganizerHomeFragment", errorMessage);
                    eventViewModel.clearErrorMessage(); // Clear the error after handling
                }
            }
        });

        // Fetch events for the current organizer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String organizerId = CurrentUserHandler.getSingleton().getCurrentUserId();
            if (organizerId != null && !organizerId.isEmpty()) {
                eventViewModel.fetchEventsByOrganizer(organizerId);
            } else {
                // Handle invalid organizer ID (e.g., show a Toast or log it)
                // Example: Toast.makeText(getContext(), "Organizer ID is invalid.", Toast.LENGTH_LONG).show();
                android.util.Log.e("OrganizerHomeFragment", "Organizer ID is invalid.");
            }
        }
    }
}
