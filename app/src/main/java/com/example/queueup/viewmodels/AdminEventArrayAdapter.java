package com.example.queueup.viewmodels;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.views.admin.AdminEventFragment;
import com.example.queueup.views.attendee.AttendeeEventFragment;

import java.util.ArrayList;

/**
 * Adapter class to display a list of events for admin users extending the EventArrayAdapter
 * to include additional functionality specifically for admins. EAch event in the list
 * can be clicked on to navigate to {@link AdminEventFragment} for detailed viewing and management.
 */
public class AdminEventArrayAdapter extends EventArrayAdapter {

    /**
     * constructs a new AdminEventArrayAdapter
     *
     * @param context the context in which the adapter is being used
     * @param event the list of events to be displayed
     */
    public AdminEventArrayAdapter(Context context, ArrayList<Event> event) {
        super(context, event);
    }

    /**
     * when an event is clicked, it opens the {@link AdminEventFragment} to view and manage the event details
     * @param view the view being clicked
     * @param position the 
     * @return an OnlickListener that opens the AdminEventFragment with the selected event's details
     */
    protected View.OnClickListener onClickListener(View view, int position) {
        return (v) -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", getItem(position));
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_activity_fragment, AdminEventFragment.class, bundle)
                    .addToBackStack(null)
                    .commit();
            this.notifyDataSetChanged();

        };
    }

}
