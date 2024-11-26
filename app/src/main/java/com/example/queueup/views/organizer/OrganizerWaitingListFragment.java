package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.AttendeeViewModel;
import com.example.queueup.viewmodels.UsersArrayAdapter;

import java.util.ArrayList;

/**
 * Fragment responsible for displaying a list of users on the event's waiting list.
 * This fragment fetches data from the ViewModel and updates the UI accordingly.
 */
public class OrganizerWaitingListFragment extends Fragment {
    public OrganizerWaitingListFragment() {
        super(R.layout.organizer_waiting_list_fragment);
    }
    private ArrayList<User> waitingList, invitedList, cancelledList, enrolledList;
    private ListView userList;
    private UsersArrayAdapter usersWaitingListAdapter, usersInvitedAdapter, usersCancelledAdapter, usersEnrolledAdapter;
    private Event event;
    private AttendeeViewModel attendeeViewModel;
    private AttendeeController attendeeController;

    private Button invitedButton, cancelledButton, enrolledButton;

    /**
     * Called when the fragment's view has been created.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        event = this.getArguments().getSerializable("event", Event.class);

        attendeeViewModel = new ViewModelProvider(this).get(AttendeeViewModel.class);
        waitingList = new ArrayList<User>();
        attendeeController = AttendeeController.getInstance();

        userList = getView().findViewById(R.id.event_waiting_list);
        usersWaitingListAdapter = new UsersArrayAdapter(view.getContext(), waitingList);
        usersInvitedAdapter = new UsersArrayAdapter(view.getContext(), invitedList);
        usersCancelledAdapter = new UsersArrayAdapter(view.getContext(), cancelledList);
        usersEnrolledAdapter = new UsersArrayAdapter(view.getContext(), enrolledList);

        if (event.getIsDrawn()) {
            userList.setAdapter(usersWaitingListAdapter);
        } else {
            userList.setAdapter(usersInvitedAdapter);
        }

        invitedButton = view.findViewById(R.id.invited_button);
        cancelledButton = view.findViewById(R.id.cancelled_button);
        enrolledButton = view.findViewById(R.id.enrolled_button);

        invitedButton.setOnClickListener(v -> {
            userList.setAdapter(usersInvitedAdapter);
        });
        cancelledButton.setOnClickListener(v -> {
            userList.setAdapter(usersCancelledAdapter);
        });
        enrolledButton.setOnClickListener(v -> {
            userList.setAdapter(usersEnrolledAdapter);
        });

        attendeeViewModel.fetchAttendeesWithUserInfo(event.getEventId());

        observeViewModel();
        attendeeController.fetchUserListsForAttendees(event.getAttendeeIds());

    }
    
    public void onResume() {
        super.onResume();
        attendeeViewModel.fetchAttendeesWithUserInfo(event.getEventId());
    }
    
    private void observeViewModel() {
        if (event.getIsDrawn()) {
        } else {
            attendeeViewModel.getAttendeesWithUserLiveData().observe(getViewLifecycleOwner(), attendees -> {
                waitingList.clear();
                if (attendees != null && !attendees.isEmpty()) {
                    for (AttendeeViewModel.AttendeeWithUser attendeeWithUser : attendees) {
                        waitingList.add(attendeeWithUser.getUser());
                    }
                }
                usersWaitingListAdapter.notifyDataSetChanged();
            });
        }


    }

}
