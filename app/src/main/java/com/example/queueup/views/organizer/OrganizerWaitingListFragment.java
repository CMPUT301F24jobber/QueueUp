package com.example.queueup.views.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.models.Event;
import com.example.queueup.models.GeoLocation;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.AttendeeViewModel;
import com.example.queueup.viewmodels.UsersArrayAdapter;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;

public class OrganizerWaitingListFragment extends Fragment {
    public OrganizerWaitingListFragment() {
        super(R.layout.organizer_waiting_list_fragment);
    }

    private ArrayList<User> dataList;
    private ListView userList;
    private UsersArrayAdapter usersAdapter;
    private Event event;
    private AttendeeViewModel attendeeViewModel;
    private AttendeeController attendeeController;
    private String currentStatus = "waiting"; // Default status

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Initialize basics
        event = this.getArguments().getSerializable("event", Event.class);
        attendeeViewModel = new ViewModelProvider(this).get(AttendeeViewModel.class);
        dataList = new ArrayList<>();
        attendeeController = AttendeeController.getInstance();

        // Setup ListView
        userList = getView().findViewById(R.id.event_waiting_list);
        usersAdapter = new UsersArrayAdapter(view.getContext(), dataList);
        userList.setAdapter(usersAdapter);

        // Setup ListView click listener

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = dataList.get(position);
                Log.d("WaitingList", "Selected user: " + selectedUser.getFullName());

                // Debug user data
                if (selectedUser != null) {
                    GeoLocation location = selectedUser.getGeoLocation();
                    Log.d("WaitingList", "User location object: " + location);
                    if (location != null) {
                        Log.d("WaitingList", String.format("Coordinates: lat=%f, lon=%f",
                                location.getLatitude(), location.getLongitude()));
                        Intent intent = new Intent(getActivity(), OrganizerMap.class);
                        intent.putExtra("selected_user", selectedUser);
                        startActivity(intent);
                    } else {
                        Log.d("WaitingList", "Location is null for user: " + selectedUser.getFullName());
                        Toast.makeText(getActivity(), "No location data available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Setup ChipGroup
        ChipGroup chipGroup = view.findViewById(R.id.filter_chip_group);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_invited) {
                currentStatus = "selected";
            } else if (checkedId == R.id.chip_cancelled) {
                currentStatus = "cancelled";
            } else if (checkedId == R.id.chip_enrolled) {
                currentStatus = "waiting";
            }
            // Refresh data with new filter
            if (event != null) {
                attendeeViewModel.fetchAttendeesWithUserInfo(event.getEventId());
            }
        });

        // Set default chip
        chipGroup.check(R.id.chip_enrolled);

        // Start observing and fetch initial data
        observeViewModel();
        attendeeViewModel.fetchAttendeesWithUserInfo(event.getEventId());
    }

    @Override
    public void onResume() {
        super.onResume();
        attendeeViewModel.fetchAttendeesWithUserInfo(event.getEventId());
    }

    private void observeViewModel() {
        attendeeViewModel.getAttendeesWithUserLiveData().observe(getViewLifecycleOwner(), attendees -> {
            dataList.clear();
            if (attendees != null && !attendees.isEmpty()) {
                for (AttendeeViewModel.AttendeeWithUser attendeeWithUser : attendees) {
                    if (attendeeWithUser.getAttendee().getStatus().equals(currentStatus)) {
                        dataList.add(attendeeWithUser.getUser());
                    }
                }
            }
            usersAdapter.notifyDataSetChanged();
        });
    }
}