package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.AdminEventImagesAdapter;
import com.example.queueup.viewmodels.EventViewModel;

import java.util.ArrayList;

public class AdminGalleryFragment extends Fragment {
    private ArrayList<Event> eventList;
    private ListView eventListView;
    private AdminEventImagesAdapter eventAdapter;
    private EventViewModel eventViewModel;

    public AdminGalleryFragment() {
        super(R.layout.admin_gallery_fragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize list and adapter
        eventList = new ArrayList<>();
        eventListView = view.findViewById(R.id.admin_images_list);
        eventAdapter = new AdminEventImagesAdapter(view.getContext(), eventList);
        eventListView.setAdapter(eventAdapter);

        // Set click listener
        eventListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (position < eventList.size()) {
                Event selectedEvent = eventList.get(position);
                if (selectedEvent != null) {
                    navigateToEventDetail(selectedEvent);
                }
            }
        });

        // Set up observers and fetch events
        setupObservers();
        eventViewModel.fetchAllEvents();
    }

    private void setupObservers() {
        eventViewModel.getAllEventsLiveData().observe(getViewLifecycleOwner(), events -> {
            if (events != null) {
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "No events found", Toast.LENGTH_SHORT).show();
            }
        });

        eventViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToEventDetail(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        AdminGalleriesFragment fragment = new AdminGalleriesFragment();
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.admin_activity_fragment, fragment)
                .addToBackStack("Event")
                .commit();
    }
}