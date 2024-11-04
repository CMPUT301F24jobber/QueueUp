package com.example.queueup.viewmodels;

import android.content.Context;
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

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.views.admin.AdminEventFragment;
import com.example.queueup.views.admin.AdminProfileFragment;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    public EventArrayAdapter(Context context, ArrayList<Event> event) {
        super(context, 0, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.home_event_content,
                    parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        TextView eventTitle = view.findViewById(R.id.event_name);
        TextView eventDate = view.findViewById(R.id.event_date);
        TextView eventLocation = view.findViewById(R.id.event_location);
        ImageView eventImage = view.findViewById(R.id.event_image);
        eventImage.setImageResource(R.drawable.ic_nav_users);

        eventTitle.setText(event.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE MMM dd, uuuu");
        String date_text = event.getStartDate().format(formatter) + " to " + event.getEndDate().format(formatter);
        eventDate.setText(date_text);
        eventLocation.setText(event.getLocation());
        view.setOnClickListener( (v) -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_activity_fragment, AdminEventFragment.class, null)
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }

}
