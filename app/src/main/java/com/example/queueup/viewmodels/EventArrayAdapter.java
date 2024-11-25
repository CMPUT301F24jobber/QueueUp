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

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.models.Event;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public abstract class EventArrayAdapter extends ArrayAdapter<Event> {
    public EventArrayAdapter(Context context, ArrayList<Event> event) {
        super(context, 0, event);
    }
    TextView eventTitle;
    Event event;
    TextView eventDate;
    TextView eventLocation;
    ImageView eventImage;
    DateTimeFormatter formatter;

    /**
     * Returns the view for the EventArrayAdapter
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
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
        event = getItem(position);
        eventTitle = view.findViewById(R.id.event_name);
        eventDate = view.findViewById(R.id.event_date);
        eventLocation = view.findViewById(R.id.event_location);
        eventImage = view.findViewById(R.id.event_image);
        Glide.with(parent).load(event.getEventBannerImageUrl()).circleCrop().into(eventImage);

        eventTitle.setText(event.getEventName());
        formatter = DateTimeFormatter.ofPattern("EEEE MMM dd, uuuu");
        eventDate.setText(event.getEventStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(formatter));
        eventLocation.setText(event.getEventLocation());

        view.setOnClickListener(onClickListener(view, position));
        return view;
    }

    /**
     * Returns the onClickListener for the EventArrayAdapter
     * @param view
     * @param position
     * @return
     */
    protected abstract View.OnClickListener onClickListener(View view, int position);
}
