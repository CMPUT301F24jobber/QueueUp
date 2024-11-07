package com.example.queueup.views.attendee;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Date;

public class AttendeeHomeFragment extends Fragment {

    // Constructor for the fragment, loading the layout file
    public AttendeeHomeFragment() {
        super(R.layout.home_fragment);
    }

    private ArrayList<Event> dataList;
    private ListView eventList;
    private AttendeeEventArrayAdapter eventAdapter;
    private EventViewModel eventViewModel;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Initialize the data list and add an event
        dataList = new ArrayList<>();  // Proper initialization of ArrayList
        Event event = null;  // Completed constructor parameters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        }
        Event eventee = new Event("id","name", "ee", "hi", "ee", "ee", new Date(1), new Date(2), 5, true);
        // Set up the ListView and its adapter
//        observeViewModel();
        dataList.add(eventee);
        eventList = view.findViewById(R.id.event_list);  // Use 'view' to find the ListView in the fragment's layout
        eventAdapter = new AttendeeEventArrayAdapter(view.getContext(), dataList);  // Initialize the adapter with the context and data list
        eventList.setAdapter(eventAdapter);  // Set the adapter to the ListView
//        String attendeeId = CurrentUserHandler.getSingleton().getCurrentUserId();
//        if (attendeeId != null && !attendeeId.isEmpty()) {
//            eventViewModel.fetchEventsByAttendee(attendeeId);
//        }
        observeViewModel();
        eventViewModel.fetchAllEvents();

    }
    private void observeViewModel() {
        eventViewModel.getAllEventsLiveData().observe(getViewLifecycleOwner(), events -> {
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
}
