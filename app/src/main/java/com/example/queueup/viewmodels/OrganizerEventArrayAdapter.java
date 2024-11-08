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

import com.example.queueup.views.organizer.OrganizerCreateEvent;
import com.example.queueup.views.organizer.OrganizerEvent;
import com.example.queueup.views.organizer.OrganizerHome;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying event details in the organizer's event list.
 * This class extends the base EventArrayAdapter and provides a custom onClickListener
 * that triggers the opening of an event details activity specific to the organizer.
 */
public class OrganizerEventArrayAdapter extends EventArrayAdapter {

    /**
     * Constructor for the OrganizerEventArrayAdapter.
     *
     * @param context the context in which the adapter is being used
     * @param event   the list of events to be displayed in the adapter
     */
    public OrganizerEventArrayAdapter(Context context, ArrayList<Event> event) {
        super(context, event);
    }

    /**
     * Creates an OnClickListener for each event item. When an event is clicked, it opens the
     * OrganizerEvent activity and passes the event data via Intent.
     *
     * @param view     the view that was clicked
     * @param position the position of the clicked item in the adapter
     * @return a View.OnClickListener that handles the event click
     */
    protected View.OnClickListener onClickListener(View view, int position) {
        return (v) -> {
            Intent intent = new Intent(view.getContext(), OrganizerEvent.class);
            intent.putExtra("event", getItem(position));
            view.getContext().startActivity(intent);
            this.notifyDataSetChanged();

        };
    }

}
