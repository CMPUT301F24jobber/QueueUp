package com.example.queueup.views.organizer;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventArrayAdapter;
import com.example.queueup.viewmodels.EventViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class OrganizerHomeFragment extends Fragment {

    // Constructor for the fragment, loading the layout file
    public OrganizerHomeFragment() {
        super(R.layout.organizer_home_fragment);  // Use a layout specific to the organizer's home screen
    }

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;

    private EventViewModel eventViewModel;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // Initialize the data list and add events
            dataList = new ArrayList<>();  // Proper initialization of ArrayList
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Add sample events or logic to retrieve events for the organizer
            }

            // Set up the ListView and its adapter
            eventList = view.findViewById(R.id.organizer_event_list);  // Ensure ID matches the organizer's ListView in XML
            if (eventList == null) {
                Log.e("OrganizerHomeFragment", "ListView not found in layout");
            }
            eventAdapter = new EventArrayAdapter(view.getContext(), dataList);  // Initialize the adapter with the context and data list
            eventList.setAdapter(eventAdapter);  // Set the adapter to the ListView

            eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
            eventViewModel.getAllEventsLiveData().observe(getViewLifecycleOwner(), events -> {
                dataList.clear();
                dataList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            });
        } catch (Exception e) {
            Log.e("OrganizerHomeFragment", "Error in onViewCreated", e);
        }
    }


}
