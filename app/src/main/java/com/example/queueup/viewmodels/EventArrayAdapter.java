package com.example.queueup.viewmodels;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.views.admin.AdminEventFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    private static final String TAG = "EventArrayAdapter";

    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.home_event_content, parent, false);
            holder = new ViewHolder();
            holder.eventTitle = view.findViewById(R.id.event_name);
            holder.eventDate = view.findViewById(R.id.event_date);
            holder.eventLocation = view.findViewById(R.id.event_location);
            holder.eventImage = view.findViewById(R.id.event_image);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Event event = getItem(position);
        if (event != null) {
            // Set Event Image
            if (holder.eventImage != null) {
                holder.eventImage.setImageResource(R.drawable.ic_nav_users);
            } else {
                Log.e(TAG, "ImageView with ID 'event_image' not found.");
            }

            // Set Event Title
            if (holder.eventTitle != null) {
                holder.eventTitle.setText(event.getEventName());
            } else {
                Log.e(TAG, "TextView with ID 'event_name' not found.");
            }

            // Set Event Date
            if (holder.eventDate != null && event.getEventStartDate() != null) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("EEEE MMM dd, yyyy", Locale.getDefault());
                    String dateText = formatter.format(event.getEventStartDate());
                    holder.eventDate.setText(dateText);
                } catch (Exception e) {
                    Log.e(TAG, "Error formatting date: " + e.getMessage());
                    holder.eventDate.setText("Invalid date");
                }
            } else {
                if (holder.eventDate == null) {
                    Log.e(TAG, "TextView with ID 'event_date' not found.");
                } else {
                    holder.eventDate.setText("No date available");
                }
            }

            // Set Event Location
            if (holder.eventLocation != null) {
                holder.eventLocation.setText(event.getEventLocation());
            } else {
                Log.e(TAG, "TextView with ID 'event_location' not found.");
            }

            // Set Click Listener
            view.setOnClickListener(v -> {
                Context context = view.getContext();
                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    Fragment adminEventFragment = new AdminEventFragment();
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.admin_activity_fragment, adminEventFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Log.e(TAG, "Context is not an AppCompatActivity.");
                }
            });
        } else {
            Log.e(TAG, "Event at position " + position + " is null.");
        }

        return view;
    }

    // ViewHolder pattern to optimize performance
    private static class ViewHolder {
        TextView eventTitle;
        TextView eventDate;
        TextView eventLocation;
        ImageView eventImage;
    }
}
