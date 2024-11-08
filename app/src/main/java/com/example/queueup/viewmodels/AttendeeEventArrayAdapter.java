package com.example.queueup.viewmodels;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.MainActivity;
import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.views.admin.AdminEventFragment;
import com.example.queueup.views.attendee.AttendeeEvent;
import com.example.queueup.views.attendee.AttendeeEventFragment;
import com.example.queueup.views.attendee.AttendeeHome;
import com.example.queueup.views.attendee.AttendeeHomeFragment;
import com.example.queueup.views.organizer.OrganizerCreateEvent;
import com.example.queueup.views.organizer.OrganizerHome;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Adapter class to display a list of events for attendee users, extending the EventArrayAdapter
 * to provide additional functionality specifically for attendees. Each event in the list
 * can be clicked to navigate to an {@link AttendeeEvent} activity for viewing the event details.
 */
public class AttendeeEventArrayAdapter extends EventArrayAdapter {

    /**
     * Constructs a new AttendeeEventArrayAdapter.
     *
     * @param context the context in which the adapter is being used
     * @param event   the list of events to be displayed
     */
    public AttendeeEventArrayAdapter(Context context, ArrayList<Event> event) {
        super(context, event);
    }

    /**
     * Returns an OnClickListener that handles click events for an item in the event list.
     * When an event is clicked, it starts the {@link AttendeeEvent} activity to view the selected event's details.
     *
     * @param view     the view being clicked
     * @param position the position of the event item in the list
     * @return an OnClickListener that opens the AttendeeEvent activity with the selected event's details
     */
    protected View.OnClickListener onClickListener(View view, int position) {
        return (v) -> {
            Intent intent = new Intent(view.getContext(), AttendeeEvent.class);
            intent.putExtra("event", getItem(position));
            view.getContext().startActivity(intent);
            this.notifyDataSetChanged();
        };
    }

}
