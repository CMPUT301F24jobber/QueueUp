package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Attendee;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.AttendeeViewModel;
import com.example.queueup.viewmodels.UsersArrayAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private LinearLayout toggleButtons;
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
        waitingList = new ArrayList<>();
        invitedList = new ArrayList<>();
        cancelledList = new ArrayList<>();
        enrolledList = new ArrayList<>();
        attendeeController = AttendeeController.getInstance();

        userList = view.findViewById(R.id.event_waiting_list);
        usersWaitingListAdapter = new UsersArrayAdapter(view.getContext(), waitingList);
        usersInvitedAdapter = new UsersArrayAdapter(view.getContext(), invitedList);
        usersCancelledAdapter = new UsersArrayAdapter(view.getContext(), cancelledList);
        usersEnrolledAdapter = new UsersArrayAdapter(view.getContext(), enrolledList);
        toggleButtons = view.findViewById(R.id.toggle_buttons);
        invitedButton = view.findViewById(R.id.invited_button);
        cancelledButton = view.findViewById(R.id.cancelled_button);
        enrolledButton = view.findViewById(R.id.enrolled_button);

        if (event.getIsDrawn()) {
            userList.setAdapter(usersInvitedAdapter);
            toggleButtons.setVisibility(View.VISIBLE);
        } else {
            userList.setAdapter(usersWaitingListAdapter);
        }

        invitedButton.setOnClickListener(v -> {
            userList.setAdapter(usersInvitedAdapter);
        });
        cancelledButton.setOnClickListener(v -> {
            userList.setAdapter(usersCancelledAdapter);
        });
        enrolledButton.setOnClickListener(v -> {
            userList.setAdapter(usersEnrolledAdapter);
        });

        userList.setAdapter(usersWaitingListAdapter);
        waitingList.add(CurrentUserHandler.getSingleton().getCurrentUser().getValue());
        usersWaitingListAdapter.notifyDataSetChanged();
        observeViewModel();



    }

    public void onResume() {
        super.onResume();
    }

    private void observeViewModel() {
        if (event.getIsDrawn()) {
            attendeeController.getAttendanceByEventId(event.getEventId())
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            ArrayList<Attendee> attendees = new ArrayList<>();
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Attendee attendee = doc.toObject(Attendee.class);
                                if (attendee != null) {
                                    attendee.setId(doc.getId());
                                    attendees.add(attendee);
                                }
                            }
                            attendeeController.fetchUserListsForAttendees(attendees).addOnSuccessListener(arrayOfLists -> {
                                Log.d("jwewfo", Integer.toString(arrayOfLists.size()));
                                ArrayList<User> l1 = arrayOfLists.get(0), l2 = arrayOfLists.get(1), l3 = arrayOfLists.get(2);
                                for (User user : l1) {
                                    invitedList.add(user);
                                }
                                for (User user : l2) {
                                    cancelledList.add(user);
                                }
                                for (User user : l1) {
                                    enrolledList.add(user);
                                }
                                usersInvitedAdapter.notifyDataSetChanged();
                                usersCancelledAdapter.notifyDataSetChanged();
                                usersEnrolledAdapter.notifyDataSetChanged();

                            });
                        }
                    });
        } else {
            attendeeController.getAttendanceByEventId(event.getEventId())
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            ArrayList<Attendee> attendees = new ArrayList<>();
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Attendee attendee = doc.toObject(Attendee.class);
                                if (attendee != null) {
                                    attendees.add(attendee);
                                }
                            }
                            attendeeController.fetchUsersForAttendees(attendees).addOnSuccessListener(listUsers -> {
                                waitingList.clear();
                                for (User user : listUsers) {
                                    waitingList.add(user);
                                }
                                userList.setAdapter(usersWaitingListAdapter);

                                usersWaitingListAdapter.notifyDataSetChanged();
                            });
                        }
                    });
        }


    }

}