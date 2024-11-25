package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventViewModel;
import com.example.queueup.viewmodels.OrganizerEventArrayAdapter;

import java.util.ArrayList;

/**
 * Fragment representing the home screen for an organizer, displaying a list of events they have created.
 * It fetches and displays events associated with the current organizer using a ViewModel and LiveData.
 */
public class OrganizerHomeFragment extends Fragment {

    private static final String TAG = "OrganizerHomeFragment";

    private ArrayList<Event> dataList;
    private ListView eventList;
    private OrganizerEventArrayAdapter eventAdapter;
    private EventViewModel eventViewModel;

    public OrganizerHomeFragment() {
        super(R.layout.organizer_home_fragment);
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState I
     * @return Return the View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_home_fragment, container, false);
        return view;
    }

    /**
     * Called when the view is created.
     *
     * @param view
     * @param savedInstanceState
     */
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
        eventViewModel.fetchEventsByOrganizer(organizerId);
    }

    /**
     * Method to observe LiveData from the ViewModel.
     */
    private void observeViewModel() {
        eventViewModel.getEventsByOrganizerLiveData().observe(getViewLifecycleOwner(), events -> {
            dataList.clear();
            if (events != null && !events.isEmpty()) {
                dataList.addAll(events);
            }
            eventAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Called when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
