package com.example.queueup.views.attendee;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.AttendeeEventArrayAdapter;

import java.util.ArrayList;

public class AttendeeHomeFragment extends Fragment {

    // Constructor for the fragment, loading the layout file
    public AttendeeHomeFragment() {
        super(R.layout.home_fragment);
    }

    private ArrayList<Event> dataList;
    private ListView eventList;
    private AttendeeEventArrayAdapter eventAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the data list and add an event
        dataList = new ArrayList<>();  // Proper initialization of ArrayList
        Event event = null;  // Completed constructor parameters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        }

        // Set up the ListView and its adapter
        eventList = view.findViewById(R.id.event_list);  // Use 'view' to find the ListView in the fragment's layout
        eventAdapter = new AttendeeEventArrayAdapter(view.getContext(), dataList);  // Initialize the adapter with the context and data list
        eventList.setAdapter(eventAdapter);  // Set the adapter to the ListView

    }
}
