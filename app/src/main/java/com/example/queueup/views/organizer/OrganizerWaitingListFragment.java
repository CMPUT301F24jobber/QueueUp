package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private AttendeeController attendeeController;

    /**
     * Called when the view is created. Initializes a sample user and binds the list to a ListView.
     * Also initializes the ViewModel to fetch data for attendees on the waiting list.
     *
     * @param view The view returned by onCreateView.
     * @param savedInstanceState The saved instance state if the fragment is being recreated.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        event = this.getArguments().getSerializable("event", Event.class);

        attendeeViewModel = new ViewModelProvider(this).get(AttendeeViewModel.class);
        dataList = new ArrayList<User>();
        attendeeController = AttendeeController.getInstance();

        userList = getView().findViewById(R.id.event_waiting_list);
        usersAdapter = new UsersArrayAdapter(view.getContext(), dataList);
        userList.setAdapter(usersAdapter);
        attendeeViewModel.fetchAttendeesWithUserInfo(event.getEventId());

        observeViewModel();
    }
    public void onResume() {
        super.onResume();
        attendeeViewModel.fetchAttendeesWithUserInfo(event.getEventId());


    }
    private void observeViewModel() {
        attendeeViewModel.getAttendeesWithUserLiveData().observe(getViewLifecycleOwner(), attendees -> {
            dataList.clear();
            if (attendees != null && !attendees.isEmpty()) {
                for (AttendeeViewModel.AttendeeWithUser attendeeWithUser : attendees) {
                    dataList.add(attendeeWithUser.getUser());
                }
            }
            usersAdapter.notifyDataSetChanged();
        });


    }

}
