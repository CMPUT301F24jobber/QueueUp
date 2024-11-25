package com.example.queueup.views.attendee;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.AttendeeEventArrayAdapter;
import com.example.queueup.viewmodels.EventViewModel;
import com.example.queueup.viewmodels.UserViewModel;

import java.util.ArrayList;

public class AttendeeHomeFragment extends Fragment {

    public AttendeeHomeFragment() {
        super(R.layout.home_fragment);
    }

    private ArrayList<Event> dataList;
    private ListView eventList;
    private AttendeeEventArrayAdapter eventAdapter;
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;

    /**
     * Called when the fragment is created.
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
        Event event = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        }

        eventList = view.findViewById(R.id.event_list);
        eventAdapter = new AttendeeEventArrayAdapter(view.getContext(), dataList);
        eventList.setAdapter(eventAdapter);  // Set the adapter to the ListView
        String attendeeId = CurrentUserHandler.getSingleton().getCurrentUserId();
        if (attendeeId != null && !attendeeId.isEmpty()) {
            eventViewModel.fetchEventsByAttendee(attendeeId);
        }
        observeViewModel();


    }
    private void observeViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        eventViewModel.fetchEventsByOrganizer(userViewModel.getCurrentUser().getValue().getUuid());
        eventViewModel.getEventsByAttendeeLiveData().observe(getViewLifecycleOwner(), events -> {
            dataList.clear();
            dataList.addAll(events);
            eventAdapter.notifyDataSetChanged();
        });
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
