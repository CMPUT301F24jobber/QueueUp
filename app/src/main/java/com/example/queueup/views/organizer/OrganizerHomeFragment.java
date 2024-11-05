package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.viewmodels.EventArrayAdapter;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrganizerHomeFragment extends Fragment {

    public OrganizerHomeFragment() {
        super(R.layout.organizer_home_fragment);
    }

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;
    private EventViewModel eventViewModel;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the data list
        dataList = new ArrayList<>();

        // Set up the ListView and its adapter
        eventList = view.findViewById(R.id.organizer_event_list);
        eventAdapter = new EventArrayAdapter(view.getContext(), dataList);
        eventList.setAdapter(eventAdapter);

        // Initialize EventViewModel
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Observe the LiveData for events by organizer
        eventViewModel.getEventsByOrganizerLiveData().observe(getViewLifecycleOwner(), events -> {
            if (events != null) {
                dataList.clear();
                dataList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }
        });

        // Fetch events for the current organizer
        fetchEvents();
    }

    private void fetchEvents() {
        String organizerId = CurrentUserHandler.getSingleton().getCurrentUserId();
        if (organizerId == null) {
            Log.e("OrganizerHome", "Organizer ID is null");
            return;
        }

        eventViewModel.fetchEventsByOrganizer(organizerId);
    }
}
