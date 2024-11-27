package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.models.Attendee;
import com.example.queueup.viewmodels.AttendeeViewModel;
import com.example.queueup.viewmodels.UsersArrayAdapter;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Map<String, Attendee> userAttendeeMap = new HashMap<>(); // To store user-attendee mapping

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

        // Setup item click listener
        userList.setOnItemClickListener((parent, view1, position, id) -> {
            if (position < dataList.size()) {
                User selectedUser = dataList.get(position);
                if (selectedUser != null) {
                    // Get the corresponding attendee from our map
                    Attendee attendee = userAttendeeMap.get(selectedUser.getUuid());
                    if (attendee != null) {
                        navigateToMap(attendee);
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

    private void navigateToMap(Attendee attendee) {
        Bundle args = new Bundle();
        args.putSerializable("attendee", attendee);
        args.putSerializable("event", event);

        OrganizerMap fragment = new OrganizerMap();
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.organizer_activity_fragment, fragment)
                .addToBackStack("Map")
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        attendeeViewModel.fetchAttendeesWithUserInfo(event.getEventId());
    }

    private void observeViewModel() {
        attendeeViewModel.getAttendeesWithUserLiveData().observe(getViewLifecycleOwner(), attendees -> {
            dataList.clear();
            userAttendeeMap.clear(); // Clear the existing mapping

            if (attendees != null && !attendees.isEmpty()) {
                for (AttendeeViewModel.AttendeeWithUser attendeeWithUser : attendees) {
                    if (attendeeWithUser.getAttendee().getStatus().equals(currentStatus)) {
                        User user = attendeeWithUser.getUser();
                        dataList.add(user);
                        // Store the mapping between user and attendee
                        userAttendeeMap.put(user.getUuid(), attendeeWithUser.getAttendee());
                    }
                }
            }
            usersAdapter.notifyDataSetChanged();
        });
    }
}