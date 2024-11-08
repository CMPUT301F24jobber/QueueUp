package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.AttendeeViewModel;
import com.example.queueup.viewmodels.EventViewModel;
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
    private ArrayList<User> dataList;
    private ListView userList;
    private UsersArrayAdapter usersAdapter;
    private Event event;
    private AttendeeViewModel attendeeViewModel;

    /**
     * Called when the view is created. Initializes a sample user and binds the list to a ListView.
     * Also initializes the ViewModel to fetch data for attendees on the waiting list.
     *
     * @param view The view returned by onCreateView.
     * @param savedInstanceState The saved instance state if the fragment is being recreated.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        User e = new User("e","e","e","e","e","e", false);
        attendeeViewModel = new ViewModelProvider(this).get(AttendeeViewModel.class);
        attendeeViewModel.getAttendancesByEventLiveData();
        dataList = new ArrayList<User>();
        dataList.add(e);

        userList = getView().findViewById(R.id.event_waiting_list);
        usersAdapter = new UsersArrayAdapter(view.getContext(), dataList);
        userList.setAdapter(usersAdapter);

    }

    private void observeViewModel() {
        attendeeViewModel.getAllAttendancesLiveData().observe(getViewLifecycleOwner(), attendees -> {
            dataList.clear();
            if (attendees != null && !attendees.isEmpty()) {
           //     dataList.addAll(attendees);
            }
            usersAdapter.notifyDataSetChanged();
        });


    }

}
