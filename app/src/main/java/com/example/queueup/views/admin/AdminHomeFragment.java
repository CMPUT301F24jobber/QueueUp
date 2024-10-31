package com.example.queueup.views.admin;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventArrayAdapter;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

public class AdminHomeFragment extends Fragment {

    // Constructor for the fragment, loading the layout file
    public AdminHomeFragment() {
        super(R.layout.admin_home_fragment);
    }

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the data list and add an event
        dataList = new ArrayList<>();  // Proper initialization of ArrayList
        Event event = null;  // Completed constructor parameters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            event = new Event(
                    "1",
                    "Event 1",
                    "Description 1",
                    "https://www.example.com/image1.jpg",
                    0.0,
                    0.0,
                    Date.from(LocalDate.of(2021, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)),
                    Date.from(LocalDate.of(2021, 1, 2).atStartOfDay().toInstant(ZoneOffset.UTC))
            );
            dataList.add(event);
        }

        // Set up the ListView and its adapter
        eventList = view.findViewById(R.id.admin_event_list);  // Use 'view' to find the ListView in the fragment's layout
        eventAdapter = new EventArrayAdapter(view.getContext(), dataList);  // Initialize the adapter with the context and data list
        eventList.setAdapter(eventAdapter);  // Set the adapter to the ListView
    }
}
