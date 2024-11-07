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

public class OrganizerWaitingListFragment extends Fragment {
    public OrganizerWaitingListFragment() {
        super(R.layout.organizer_waiting_list_fragment);
    }
    private ArrayList<User> dataList;
    private ListView userList;
    private UsersArrayAdapter usersAdapter;
    private Event event;
    private AttendeeViewModel attendeeViewModel;
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
