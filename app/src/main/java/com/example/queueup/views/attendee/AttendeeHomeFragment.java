package com.example.queueup.views.attendee;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.AttendeeEventArrayAdapter;
import com.example.queueup.viewmodels.EventViewModel;
import com.example.queueup.viewmodels.UserViewModel;

import java.util.ArrayList;

/**
 * AttendeeHomeFragment is a fragment that displays a list of events for an attendee.
 * It retrieves event data from the ViewModel and populates a ListView with the events.
 * This fragment is part of the attendee's home screen, where they can view upcoming events.
 */
public class AttendeeHomeFragment extends Fragment {

    // Constructor for the fragment, loading the layout file
    public AttendeeHomeFragment() {
        super(R.layout.home_fragment);
    }

    private ArrayList<Event> dataList;
    private ListView eventList;
    private AttendeeEventArrayAdapter eventAdapter;
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;

    /**
     * Called when the fragment's view is created. This method initializes the event list,
     * sets up the ListView with an adapter, and observes the ViewModel for changes in event data.
     *
     * @param view The root view of the fragment's layout.
     * @param savedInstanceState A Bundle containing the fragment's saved state, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Initialize the data list and add an event
        dataList = new ArrayList<>();  // Proper initialization of ArrayList
        Event event = null;  // Completed constructor parameters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        }
        // Set up the ListView and its adapter
//        observeViewModel();
        eventList = view.findViewById(R.id.event_list);  // Use 'view' to find the ListView in the fragment's layout
        eventAdapter = new AttendeeEventArrayAdapter(view.getContext(), dataList);  // Initialize the adapter with the context and data list
        eventList.setAdapter(eventAdapter);  // Set the adapter to the ListView
        String attendeeId = CurrentUserHandler.getSingleton().getCurrentUserId();
        if (attendeeId != null && !attendeeId.isEmpty()) {
            eventViewModel.fetchEventsByAttendee(attendeeId);
        }
        observeViewModel();


    }
    private void observeViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        eventViewModel.fetchEventsByOrganizer(userViewModel.getCurrentUser().getValue().getUuid());
        eventViewModel.getEventsByAttendeeLiveData().observe(getViewLifecycleOwner(), events -> {
            dataList.clear();
            dataList.addAll(events);
            eventAdapter.notifyDataSetChanged();
        });
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
