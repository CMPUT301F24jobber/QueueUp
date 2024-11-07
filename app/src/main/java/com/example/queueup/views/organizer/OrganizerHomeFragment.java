package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventViewModel;
import com.example.queueup.viewmodels.OrganizerEventArrayAdapter;
import com.example.queueup.handlers.CurrentUserHandler;

import java.util.ArrayList;
import java.util.List;

public class OrganizerHomeFragment extends Fragment {

    private static final String TAG = "OrganizerHomeFragment";

    private ArrayList<Event> dataList;
    private ListView eventList;
    private OrganizerEventArrayAdapter eventAdapter;
    private EventViewModel eventViewModel;

    public OrganizerHomeFragment() {
        super(R.layout.organizer_home_fragment);  // Ensure this layout exists
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Initialize ListView and Adapter
        eventList = view.findViewById(R.id.organizer_event_list);  // Ensure this ID matches your XML
        dataList = new ArrayList<>();
        eventAdapter = new OrganizerEventArrayAdapter(requireContext(), dataList);
        eventList.setAdapter(eventAdapter);

        // Observe LiveData from ViewModel
        observeViewModel();

        // Fetch events for the current organizer
        String organizerId = CurrentUserHandler.getSingleton().getCurrentUserId();
        if (organizerId != null && !organizerId.isEmpty()) {
            eventViewModel.fetchEventsByOrganizer(organizerId);
        } else {
            Toast.makeText(getContext(), "Organizer ID is missing. Please log in again.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Organizer ID is null or empty.");
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        String organizerId = CurrentUserHandler.getSingleton().getCurrentUserId();
        if (organizerId != null && !organizerId.isEmpty()) {
            eventViewModel.fetchEventsByOrganizer(organizerId);
        } else {
            Toast.makeText(getContext(), "Organizer ID is missing. Please log in again.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Organizer ID is null or empty.");
        }
    }

    /**
     * Observes LiveData from EventViewModel to update the UI accordingly.
     */
    private void observeViewModel() {
        // Observe events by organizer
        eventViewModel.getEventsByOrganizerLiveData().observe(getViewLifecycleOwner(), events -> {
            dataList.clear();
            if (events != null && !events.isEmpty()) {
                dataList.addAll(events);
            }
            eventAdapter.notifyDataSetChanged();
        });

        // Observe error messages
        eventViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Optionally, perform cleanup here
    }

}
