package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.AdminEventArrayAdapter;
import com.example.queueup.viewmodels.EventViewModel;

import java.util.ArrayList;

/**
 * AdminHomeFragment displays a list of events for the admin. It fetches the events from the ViewModel and
 * displays them using a ListView and a custom adapter. The fragment observes data changes in the ViewModel
 * and updates the UI accordingly.
 */
public class AdminHomeFragment extends Fragment {

    // Constructor for the fragment, loading the layout file
    public AdminHomeFragment() {
        super(R.layout.home_fragment);
    }

    private ArrayList<Event> dataList;
    private ListView eventList;
    private AdminEventArrayAdapter eventAdapter;
    private EventViewModel eventViewModel;

    /**
     * Called when the fragment's view has been created.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        // Initialize the data list and add an event
        dataList = new ArrayList<>();

        // Set up the ListView and its adapter
        eventList = view.findViewById(R.id.event_list);
        eventAdapter = new AdminEventArrayAdapter(view.getContext(), dataList);  // Initialize the adapter with the context and data list
        eventList.setAdapter(eventAdapter);  // Set the adapter to the ListView
        observeViewModel();
        eventViewModel.fetchAllEvents();

    }

    /**
     * Called when the fragment is resumed. Fetches all events from the ViewModel.
     */
    @Override
    public void onResume() {
        super.onResume();
        eventViewModel.fetchAllEvents();

    }

    /**
     * Observes the ViewModel for changes in the list of events. Updates the UI accordingly.
     */
    private void observeViewModel() {
        eventViewModel.getAllEventsLiveData().observe(getViewLifecycleOwner(), events -> {
            dataList.clear();
            if (events != null && !events.isEmpty()) {
                dataList.addAll(events);
            }
            eventAdapter.notifyDataSetChanged();
        });
        

    }

}
